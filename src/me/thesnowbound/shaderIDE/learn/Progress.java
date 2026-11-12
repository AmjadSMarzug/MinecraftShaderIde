package me.thesnowbound.shaderIDE.learn;

import me.thesnowbound.shaderIDE.ui.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static me.thesnowbound.shaderIDE.learn.Learn.*;

// a thin rounded bar that slides to its new value instead of jumping
public final class Progress extends JComponent {
    private float shown;
    private float target;
    private final Timer tick = new Timer(10, null);

    public Progress() {
        setPreferredSize(new Dimension(200, 6));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));

    }

    public void set(float value) {
        target = Math.max(0, Math.min(1, value));
        shown = target;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int h = getHeight();
        g2.setColor(Theme.current.border());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), h, h, h));
        if (shown > 0.001f) {
            g2.setColor(Theme.current.accent());
            g2.fill(new RoundRectangle2D.Float(0, 0, Math.max(h, getWidth() * shown), h, h, h));
        }
        g2.dispose();
    }
}
