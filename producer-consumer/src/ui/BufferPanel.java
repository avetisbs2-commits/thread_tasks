package ui;

import buffer.BoundedBuffer;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

/**
 * Draws the buffer as a row of boxes: filled boxes hold items (oldest on the
 * left), empty boxes are free slots.
 *
 * PROVIDED CODE - students do not need to change it.
 */
public class BufferPanel extends JPanel {

    private static final Color FILLED = new Color(66, 133, 244);
    private static final Color EMPTY = new Color(235, 235, 235);
    private static final Color FULL_BORDER = new Color(217, 48, 37);

    private BoundedBuffer buffer;

    public BufferPanel() {
        setPreferredSize(new Dimension(600, 90));
        setBackground(Color.WHITE);
    }

    /** Sets the buffer to draw (null clears the panel). */
    public void setBuffer(BoundedBuffer buffer) {
        this.buffer = buffer;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (buffer == null) {
            g2.setColor(Color.GRAY);
            g2.drawString("Press Start to create a buffer.", 12, getHeight() / 2);
            g2.dispose();
            return;
        }

        ArrayList<Integer> items = buffer.snapshot();
        int capacity = buffer.getCapacity();
        boolean full = items.size() >= capacity;

        int margin = 10;
        int gap = 4;
        int cellWidth = (getWidth() - 2 * margin - (capacity - 1) * gap) / capacity;
        int cellHeight = getHeight() - 2 * margin - 16;
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < capacity; i++) {
            int x = margin + i * (cellWidth + gap);
            boolean filled = i < items.size();

            g2.setColor(filled ? FILLED : EMPTY);
            g2.fillRoundRect(x, margin, cellWidth, cellHeight, 8, 8);
            g2.setColor(full ? FULL_BORDER : Color.LIGHT_GRAY);
            g2.drawRoundRect(x, margin, cellWidth, cellHeight, 8, 8);

            if (filled) {
                String text = String.valueOf(items.get(i));
                g2.setColor(Color.WHITE);
                int textX = x + (cellWidth - fm.stringWidth(text)) / 2;
                int textY = margin + (cellHeight + fm.getAscent()) / 2 - 2;
                g2.drawString(text, textX, textY);
            }
        }

        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Buffer: " + items.size() + " / " + capacity + (full ? "  (FULL)" : ""),
                margin, getHeight() - 6);
        g2.dispose();
    }
}
