package worker;

import buffer.BoundedBuffer;

/**
 * The waiter. Every few milliseconds it takes one item out of the buffer.
 *
 * PROVIDED CODE - students do not need to change it.
 */
public class Consumer extends Thread {

    private final BoundedBuffer buffer;
    private final long delayMillis;
    private final MessageSink sink;

    private volatile int doneCount;
    private volatile int failedCount;

    public Consumer(int number, BoundedBuffer buffer, long delayMillis, MessageSink sink) {
        super("Consumer-" + number);
        this.buffer = buffer;
        this.delayMillis = delayMillis;
        this.sink = sink;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                try {
                    int item = buffer.take();    // may wait here if the buffer is empty
                    doneCount++;
                    sink.log(getName() + " consumed item " + item);
                } catch (IllegalStateException e) {
                    failedCount++;               // the buffer said "empty" instead of waiting
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
