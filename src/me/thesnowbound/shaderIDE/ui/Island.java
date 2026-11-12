package me.thesnowbound.shaderIDE.ui;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;
import static me.thesnowbound.shaderIDE.ui.Fx.*;

// a rounded panel floating on the frame colour, the building block of the window
public final class Island extends JPanel {
    private final int arc;

    public Island(java.awt.Component content, int pad) {
        this(content, pad, 12);
    }

    public Island(java.awt.Component content, int pad, int arc) {
        super(new BorderLayout());
        this.arc = arc;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        add(content);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(t().island());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
        if (!t().dark() || t().name().equals("High Contrast")) {
            g2.setColor(t().border());
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, arc, arc));
        }
        g2.dispose();
    }
}
