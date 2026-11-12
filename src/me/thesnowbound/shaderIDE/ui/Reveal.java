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

// fades and lifts its contents in when a page changes
public class Reveal extends JPanel {
    private float alpha = 1f, lift;
    private Timer running;

    public Reveal() {
        setOpaque(true);
    }

    public void play() {
        play(260, 10);
    }

    public void play(int ms, float dy) {
        if (running != null) running.stop();
        running = animate(ms, p -> {
            alpha = (float) p;
            lift = (float) (dy * (1 - p));
            repaint();
        }, null);
    }

    @Override
    public void paint(Graphics g) {
        if (alpha >= 1f) {
            super.paint(g);
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setComposite(java.awt.AlphaComposite.SrcOver.derive(alpha));
        g2.translate(0, Math.round(lift));
        super.paint(g2);
        g2.dispose();
    }
}
