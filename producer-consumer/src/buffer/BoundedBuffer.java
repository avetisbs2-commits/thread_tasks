package buffer;

import worker.Consumer;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A small shared box (buffer) for producer and consumer threads.
 *
 *   put()  - a producer adds an item.   If the box is FULL, it must wait.
 *   take() - a consumer removes an item. If the box is EMPTY, it must wait.
 *
 * Both methods are synchronized, so the lock is this object.
 * That means wait() and notifyAll() are called on "this".
 */
public class BoundedBuffer {

    private final LinkedList<Integer> items = new LinkedList<>();
    private final int capacity;

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    // ------------------------------------------------------------------
    // STUDENT TASK 1: finish put()
    // ------------------------------------------------------------------
    public synchronized void put(int item) throws InterruptedException {
        // TODO: fill the blanks (_____) and write this code instead of the
        // temporary code below.
        //
        while(items.size() == getCapacity())     {
            wait();
        }
          items.addLast(item);
        notifyAll();

    }

    // ------------------------------------------------------------------
    // STUDENT TASK 2: finish take()
    // ------------------------------------------------------------------
    public synchronized int take() throws InterruptedException {
        // TODO: fill the blanks (_____) and write this code instead of the
        // temporary code below.
        //
        while (items.isEmpty()) {
            wait();

        }
        int item = items.removeFirst();
        notifyAll();
        // wake up the waiting threads
        return item;
    }

    // ------------------------------------------------------------------
    // PROVIDED - the UI uses these. Do not change them.
    // ------------------------------------------------------------------
    public int getCapacity() {
        return capacity;
    }

    public synchronized int size() {
        return items.size();
    }

    /** A copy of the current items, oldest first. Used to draw the buffer. */
    public synchronized ArrayList<Integer> snapshot() {
        return new ArrayList<>(items);
    }
}
