package com.chitieu.ui;


import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/**
 * Khung nền trắng bo góc (dạng "thẻ"/card), dùng ở khắp nơi trong giao diện.
 */
public class KhungBoGoc extends JPanel {
    private final int arc;
    private Color borderColor = GiaoDien.BORDER;

    public KhungBoGoc(LayoutManager layout, int arc) {
        super(layout);
        this.arc = arc;
        setOpaque(false);          // tự vẽ nền bo góc bên dưới
        setBackground(Color.WHITE);
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
