package worker;

import buffer.BoundedBuffer;

/**
 * The cook. Every few milliseconds it makes a new item and puts it into the buffer.
 *
 * PROVIDED CODE - students do not need to change it.
 */
public class Producer extends Thread {

    private final int number;
    private final BoundedBuffer buffer;
    private final long delayMillis;
    private final MessageSink sink;

    private volatile int doneCount;
    private volatile int failedCount;

    public Producer(int number, BoundedBuffer buffer, long delayMillis, MessageSink sink) {
        super("Producer-" + number);
        this.number = number;
        this.buffer = buffer;
        this.delayMillis = delayMillis;
        this.sink = sink;
    }

    @Override
    public void run() {
        int count = 0;
        try {
            while (!isInterrupted()) {
                count++;
                int item = number * 1000 + count; // Producer-1 makes 1001, 1002, 1003 ...

                try {
                    buffer.put(item);            // may wait here if the buffer is full
                    doneCount++;
                    sink.log(getName() + " produced item " + item);
                } catch (IllegalStateException e) {
                    failedCount++;               // the buffer said "full" instead of waiting
                    sink.log(getName() + " FAILED: " + e.getMessage());
                }

                Thread.sleep(delayMillis);       // rest a little
            }
        } catch (InterruptedException e) {
            // The Stop button was pressed. Leave the loop.
        }
        sink.log(getName() + " stopped");
    }

    public int getDoneCount() {
        return doneCount;
    }

    public int getFailedCount() {
        return failedCount;
    }
}
