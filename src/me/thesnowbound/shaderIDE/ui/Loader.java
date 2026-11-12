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

// thin bar that sweeps across while something is working
public final class Loader extends JComponent {
    private final Timer tick = new Timer(15, e -> repaint());
    private long started;

    public Loader() {
        setPreferredSize(new Dimension(0, 2));
        setOpaque(false);
    }

    public void busy(boolean on) {
        if (on) {
            started = System.nanoTime();
            tick.start();
        } else {
            tick.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!tick.isRunning()) return;
        double p = ((System.nanoTime() - started) / 1e9 * 0.8) % 1.0;
        int w = getWidth(), seg = w / 4;
        g.setColor(t().accent());
        g.fillRect((int) (p * (w + seg)) - seg, 0, seg, 2);
    }
}
