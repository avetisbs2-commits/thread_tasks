package worker;

/**
 * Where worker threads send their log messages.
 * The UI implements it and shows the messages in the log area.
 * It can be called from any thread.
 */
public interface MessageSink {

    void log(String message);
}
