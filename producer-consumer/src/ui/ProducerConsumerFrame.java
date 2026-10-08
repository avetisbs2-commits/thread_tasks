package ui;

import buffer.BoundedBuffer;
import worker.Consumer;
import worker.MessageSink;
import worker.Producer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

/**
 * Window that starts and stops the producer and consumer threads and shows
 * what is happening: the buffer, every thread's state, and a log.
 *
 * PROVIDED CODE - students do not need to change it. The thread table shows
 * the real Thread.getState(): a producer blocked in wait() is WAITING.
 */
public class ProducerConsumerFrame extends JFrame implements MessageSink {

    private static final int MAX_LOG_LINES = 500;

    private JSpinner capacitySpinner;
    private JSpinner producerCountSpinner;
    private JSpinner consumerCountSpinner;
    private JSpinner produceDelaySpinner;
    private JSpinner consumeDelaySpinner;
    private JButton startButton;
    private JButton stopButton;

    private BufferPanel bufferPanel;
    private DefaultTableModel threadTableModel;
    private JTextArea logArea;

    private final ArrayList<Producer> producers = new ArrayList<>();
    private final ArrayList<Consumer> consumers = new ArrayList<>();
    private Timer refreshTimer;

    public ProducerConsumerFrame() {
        setTitle("Producer / Consumer");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createComponents();
        createLayout();

        // Redraw the buffer and thread table 10 times per second.
        refreshTimer = new Timer(100, e -> refreshView());
        refreshTimer.start();
    }

    private void createComponents() {
        // Simple start: 1 producer (fast) and 1 consumer (slow), buffer of 3.
        // The buffer fills up after a second or two.
        capacitySpinner = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        producerCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        consumerCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        produceDelaySpinner = new JSpinner(new SpinnerNumberModel(200, 50, 5000, 50));
        consumeDelaySpinner = new JSpinner(new SpinnerNumberModel(800, 50, 5000, 50));

        startButton = new JButton("Start");
        startButton.addActionListener(e -> startThreads());

        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopThreads());

        bufferPanel = new BufferPanel();

        threadTableModel = new DefaultTableModel(
                new Object[]{"Thread", "Role", "State", "Done", "Failed"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logArea = new JTextArea();
        logArea.setEditable(false);
    }

    private void createLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel fieldsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldsRow.add(new JLabel("Buffer size:"));
        fieldsRow.add(capacitySpinner);
        fieldsRow.add(new JLabel("Producers:"));
        fieldsRow.add(producerCountSpinner);
        fieldsRow.add(new JLabel("Consumers:"));
        fieldsRow.add(consumerCountSpinner);
        fieldsRow.add(new JLabel("Produce every (ms):"));
        fieldsRow.add(produceDelaySpinner);
        fieldsRow.add(new JLabel("Consume every (ms):"));
        fieldsRow.add(consumeDelaySpinner);

        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsRow.add(startButton);
        buttonsRow.add(stopButton);

        JPanel settingsPanel = new JPanel(new GridLayout(2, 1));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        settingsPanel.add(fieldsRow);
        settingsPanel.add(buttonsRow);

        JPanel bufferWrapper = new JPanel(new BorderLayout());
        bufferWrapper.setBorder(BorderFactory.createTitledBorder("Buffer (oldest item on the left)"));
        bufferWrapper.add(bufferPanel, BorderLayout.CENTER);

        JTable threadTable = new JTable(threadTableModel);
        threadTable.setFillsViewportHeight(true);
        JScrollPane threadScroll = new JScrollPane(threadTable);
        threadScroll.setBorder(BorderFactory.createTitledBorder("Threads"));
        threadScroll.setPreferredSize(new Dimension(400, 200));

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Log"));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        JButton clearLogButton = new JButton("Clear Log");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        logPanel.add(clearLogButton, BorderLayout.SOUTH);

        JSplitPane lowerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, threadScroll, logPanel);
        lowerSplit.setResizeWeight(0.5);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(bufferWrapper, BorderLayout.NORTH);
        centerPanel.add(lowerSplit, BorderLayout.CENTER);

        add(settingsPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void startThreads() {
        int capacity = (Integer) capacitySpinner.getValue();
        int producerCount = (Integer) producerCountSpinner.getValue();
        int consumerCount = (Integer) consumerCountSpinner.getValue();
        long produceDelay = (Integer) produceDelaySpinner.getValue();
        long consumeDelay = (Integer) consumeDelaySpinner.getValue();

        BoundedBuffer buffer = new BoundedBuffer(capacity);

        producers.clear();
        consumers.clear();
        for (int i = 1; i <= producerCount; i++) {
            producers.add(new Producer(i, buffer, produceDelay, this));
        }
        for (int i = 1; i <= consumerCount; i++) {
            consumers.add(new Consumer(i, buffer, consumeDelay, this));
        }

        bufferPanel.setBuffer(buffer);
        threadTableModel.setRowCount(0);
        logArea.setText("");
        setControlsEnabled(false);

        for (Producer producer : producers) {
            producer.start();
        }
        for (Consumer consumer : consumers) {
            consumer.start();
        }
    }

    private void stopThreads() {
        // interrupt() also wakes up a thread that is blocked inside wait().
        for (Producer producer : producers) {
            producer.interrupt();
        }
        for (Consumer consumer : consumers) {
            consumer.interrupt();
        }
        setControlsEnabled(true);
    }

    private void setControlsEnabled(boolean enabled) {
        capacitySpinner.setEnabled(enabled);
        producerCountSpinner.setEnabled(enabled);
        consumerCountSpinner.setEnabled(enabled);
        produceDelaySpinner.setEnabled(enabled);
        consumeDelaySpinner.setEnabled(enabled);
        startButton.setEnabled(enabled);
        stopButton.setEnabled(!enabled);
    }

    private void refreshView() {
        bufferPanel.repaint();

        int rowCount = producers.size() + consumers.size();
        if (threadTableModel.getRowCount() != rowCount) {
            threadTableModel.setRowCount(rowCount);
        }

        int row = 0;
        for (Producer producer : producers) {
            showRow(row++, producer.getName(), "Producer", producer.getState(),
                    producer.getDoneCount(), producer.getFailedCount());
        }
        for (Consumer consumer : consumers) {
            showRow(row++, consumer.getName(), "Consumer", consumer.getState(),
                    consumer.getDoneCount(), consumer.getFailedCount());
        }
    }

    private void showRow(int row, String name, String role, Thread.State state, int done, int failed) {
        threadTableModel.setValueAt(name, row, 0);
        threadTableModel.setValueAt(role, row, 1);
        threadTableModel.setValueAt(state, row, 2);
        threadTableModel.setValueAt(done, row, 3);
        threadTableModel.setValueAt(failed, row, 4);
    }

    /** Called from worker threads, so the text area is updated on the Swing thread. */
    @Override
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            trimLog();
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void trimLog() {
        int lines = logArea.getLineCount();
        if (lines <= MAX_LOG_LINES) {
            return;
        }
        try {
            int cut = logArea.getLineEndOffset(lines - MAX_LOG_LINES - 1);
            logArea.getDocument().remove(0, cut);
        } catch (javax.swing.text.BadLocationException e) {
            logArea.setText("");
        }
    }
}
