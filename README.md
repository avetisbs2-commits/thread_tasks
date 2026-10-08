# Thread Tasks

This project contains separate Java Swing apps for practicing **multithreading**.

Each app is an independent IntelliJ IDEA module and can be started separately.

The modules will grow from simple to harder topics:

```text
Thread
Runnable
synchronized
wait / notify
producer / consumer
deadlock
thread pools
```

## Project Structure

```text
thread_tasks/
├── README.md
├── .gitignore
└── producer-consumer/
    ├── README.md
    ├── producer-consumer.iml
    └── src/
```

## Run/Debug Configurations

Open:

```text
Run -> Edit Configurations...
```

Click `+`, then choose `Application`.

## Configuration 1: Producer Consumer Desktop App

Use these values:

- Name: `Producer Consumer Desktop App`
- Main class: `Main`
- Module: `producer-consumer`
- JRE: project default JDK
- Program arguments: leave empty
- VM options: leave empty
- Working directory:

```text
/Users/tigranho/Projects/test/thread_tasks/producer-consumer
```

Build and run settings:

- Before launch: `Build`
- Activate tool window: checked

If `Build` is missing under `Before launch`:

1. Click `+` in the `Before launch` section.
2. Choose `Build`.
3. Click `OK`.

## Start The Apps From IntelliJ

1. Open the Run/Debug configuration dropdown near the top-right of IntelliJ IDEA.
2. Select `Producer Consumer Desktop App`.
3. Click the green `Run` button.

Use `Debug` instead of `Run` if you want to practice breakpoints. In debug mode you can see every thread in the `Threads` tab and check which ones are waiting.

## Notes

- Each module has its own `README.md` with student tasks.
- Start each app from its own `Main.java`.
- The apps are separate and do not share code.
