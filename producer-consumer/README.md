# Producer / Consumer - Step By Step Guide

## The Idea

Think about a small table that holds only 3 plates.

- The **cook** (producer) puts plates on the table.
- The **waiter** (consumer) takes plates from the table.

```text
Cook  ──>  [ plate ][ plate ][ plate ]  ──>  Waiter
                     the table
```

Two rules:

1. If the table is **full**, the cook must **wait**.
2. If the table is **empty**, the waiter must **wait**.

In the code, the cook is a `Producer` thread, the waiter is a `Consumer` thread, and the table is `BoundedBuffer`.

Right now the table does not make anyone wait. It only shows an error. Your job is to fix this.

## Two Words You Need

| Code | Meaning |
|---|---|
| `wait();` | "I can't continue now. I will sleep until someone wakes me." |
| `notifyAll();` | "I changed something. Everyone who is sleeping, wake up and check again." |

## Step 1 - Run The App

Run `Main` in IntelliJ.

Or from this folder in the terminal:

```bash
javac -d out/production $(find src -name '*.java')
java -cp out/production Main
```

## Step 2 - See The Problem

1. Press **Start**.
2. Wait 2-3 seconds. The table (buffer) becomes full.
3. Look at the **Log**: you will see `FAILED: Buffer is full`.
4. Look at the **Failed** column: the number grows.

The cook did not wait, so we get an error. Press **Stop**.

## Step 3 - Fix `put()`

Open `src/buffer/BoundedBuffer.java` and find `put()`.

Replace the `TEMPORARY CODE` with this, and fill the blanks:

```java
while (_____) {        // 1. the table is full
    _____;             // 2. wait
}
items.addLast(item);   // put the plate on the table
_____;                 // 3. wake up everyone
```

Help:

1. The table is full when `items.size()` is equal to `capacity`.
2. The command to sleep is `wait()`.
3. The command to wake up everyone is `notifyAll()`.

## Step 4 - Fix `take()`

Find `take()` in the same file.

Replace the `TEMPORARY CODE` with this, and fill the blanks:

```java
while (_____) {                     // 1. the table is empty
    _____;                          // 2. wait
}
int item = items.removeFirst();     // take the plate
_____;                              // 3. wake up everyone
return item;
```

Help:

1. The table is empty when `items.isEmpty()` is true.
2. and 3. are the same as in `put()`.

## Step 5 - Test Your Code

Run the app again and press **Start**.

| What you should see | Where |
|---|---|
| **Failed** is always `0` | thread table |
| Cook state is **WAITING** when the table is full | thread table, column State |
| No `FAILED` lines | log |

If you see this, your code works.

### Extra tests

- **Waiter waits:** set `Produce every` to `1000` and `Consume every` to `100`. Press **Start**. Now the waiter (Consumer) is **WAITING**, because the table is empty.
- **More threads:** set `Producers` to `3` and `Consumers` to `3`. Press **Start**. Nothing should fail.
- **Stop:** press **Stop**. Every thread must stop.

## Common Mistakes

| Problem | Reason |
|---|---|
| Used `if` instead of `while` | After waking up, the thread must check again. Use `while`. |
| Forgot `notifyAll()` | The sleeping threads never wake up and the app freezes. |
| Wrote `try/catch` around `wait()` | **Stop** will not work. Do not catch it. The method already has `throws InterruptedException`. |

## What Is Already Done

Do not change these:

- `ui` - the window
- `worker` - the `Producer` and `Consumer` threads
- `size()`, `getCapacity()`, `snapshot()` in `BoundedBuffer`

## Rules

Use only `synchronized`, `wait()` and `notifyAll()`.

Do not use `BlockingQueue`, `Semaphore`, `Lock`, or `Thread.sleep()` inside the buffer.
