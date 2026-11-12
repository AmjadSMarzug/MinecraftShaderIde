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

// a task row whose circle fills in with a quick pop when it's done
public final class Tick extends JComponent {
    private final String text;
    private boolean done;
    private boolean current;
    private float fill;

    Tick(String text) {
        this.text = text;
        setPreferredSize(new Dimension(360, 32));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

    }

    void set(boolean ok) {
        if (ok == done) return;
        done = ok;
        fill = ok ? 1f : 0f;
        repaint();
    }

    void current(boolean c) {
        if (c == current) return;
        current = c;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (current) {
            g2.setColor(Theme.current.hover());
            g2.fill(new RoundRectangle2D.Float(0, 1, getWidth(), getHeight() - 2, 8, 8));
        }
        float r = 8;
        Theme t = Theme.current;
        float cx = 15, cy = getHeight() / 2f;
        g2.setStroke(new BasicStroke(1.4f));
        g2.setColor(current ? t.strong() : t.faint());
        g2.draw(new Ellipse2D.Float(cx - 8, cy - 8, 16, 16));
        if (fill > 0.01f) {
            g2.setColor(t.good());
            g2.fill(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
            g2.setColor(t.dark() ? Color.BLACK : Color.WHITE);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int) cx - 4, (int) cy, (int) cx - 1, (int) cy + 3);
            g2.drawLine((int) cx - 1, (int) cy + 3, (int) cx + 4, (int) cy - 3);
        }
        g2.setFont(getFont().deriveFont(current ? Font.BOLD : Font.PLAIN, 13f));
        g2.setColor(done ? t.dim() : current ? t.strong() : t.text());
        g2.drawString(text, 34, cy + 5);
        g2.dispose();
    }
}
