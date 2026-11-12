package me.thesnowbound.shaderIDE.ui;

import me.thesnowbound.shaderIDE.Main;

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

public final class Fx {
    public static final Color BACKDROP = Theme.LIVE.frame(), BLACK = Theme.LIVE.island(), SIDE = Theme.LIVE.island(), LINE = Theme.LIVE.border(),
            TEXT = Theme.LIVE.text(), DIM = Theme.LIVE.dim(), WHITE = Theme.LIVE.strong(), RED = Theme.LIVE.error();

    static final List<JWindow> toasts = new ArrayList<>();

    private Fx() {
    }

    public static Theme t() {
        return Theme.LIVE;
    }

    static final java.util.prefs.Preferences PREFS = java.util.prefs.Preferences.userNodeForPackage(Main.class);
    public static boolean motion = PREFS.getBoolean("motion", true);

    public static void motion(boolean on) {
        motion = on;
        PREFS.putBoolean("motion", on);
    }

    // eased out, so things arrive fast and settle softly
    public static Timer animate(int ms, DoubleConsumer step, Runnable done) {
        if (!motion) {
            step.accept(1);
            if (done != null) done.run();
            return new Timer(ms, null);
        }
        long start = System.nanoTime();
        Timer t = new Timer(1000 / 60, null);
        t.addActionListener(e -> {
            double p = Math.min(1, (System.nanoTime() - start) / 1e6 / ms);
            step.accept(1 - Math.pow(1 - p, 3));
            if (p >= 1) {
                t.stop();
                if (done != null) done.run();
            }
        });
        step.accept(0);
        t.start();
        return t;
    }

    public static void opacity(Window w, float a) {
        try {
            w.setOpacity(Math.max(0f, Math.min(1f, a)));
        } catch (RuntimeException ignored) {
            // no translucent windows on this screen, it just pops in
        }
    }

    public static Color mix(Color a, Color b, double t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t), (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t), (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t));
    }

    public static void hover(AbstractButton b, Color idle, Color hot) {
        b.setForeground(idle);
        b.getModel().addChangeListener(e -> b.setForeground(b.getModel().isRollover() || b.isSelected() ? hot : idle));
    }

    public static void fadeIn(Window w, int dy) {
        Point end = w.getLocation();
        opacity(w, 0f);
        w.setLocation(end.x, end.y + dy);
        w.setVisible(true);
        animate(180, p -> {
            opacity(w, (float) p);
            w.setLocation(end.x, (int) Math.round(end.y + dy * (1 - p)));
        }, null);
    }

    public static void fadeOut(Window w, Runnable done) {
        animate(120, p -> opacity(w, (float) (1 - p)), () -> {
            w.dispose();
            if (done != null) done.run();
        });
    }

    public static void round(Window w, int arc) {
        w.setShape(new RoundRectangle2D.Double(0, 0, w.getWidth(), w.getHeight(), arc, arc));
    }

    // a real window, not a lightweight popup, because the GL preview would paint right over a lightweight one
    public static void toast(JFrame owner, String text, boolean bad, String actionText, Runnable action) {
        Theme t = t();
        JWindow w = new JWindow(owner);
        JPanel box = new JPanel(new BorderLayout(14, 0));
        box.setBackground(t.raised());
        box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(bad ? t.error() : t.border()),
                BorderFactory.createEmptyBorder(12, 16, 12, 14)));
        JLabel label = new JLabel(text);
        label.setForeground(bad ? t.error() : t.text());
        box.add(label, BorderLayout.CENTER);
        if (actionText != null) {
            JButton go = new JButton(actionText);
            go.putClientProperty("JButton.buttonType", "borderless");
            go.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            go.setFocusable(false);
            go.setForeground(t.accent());
            go.addActionListener(e -> {
                action.run();
                close(w);
            });
            box.add(go, BorderLayout.EAST);
        }
        w.setContentPane(box);
        w.pack();
        w.setSize(Math.max(300, w.getWidth()), w.getHeight());
        round(w, 10);

        int y = owner.getY() + owner.getHeight() - w.getHeight() - 46;
        for (JWindow other : toasts) y -= other.getHeight() + 10;
        w.setLocation(owner.getX() + owner.getWidth() - w.getWidth() - 22, y);
        toasts.add(w);
        w.setVisible(true);
        Timer life = new Timer(actionText != null ? 7000 : 3500, e -> close(w));
        life.setRepeats(false);
        life.start();
    }

    static void close(JWindow w) {
        if (toasts.remove(w)) w.dispose();
    }

    // wheel scrolling like IntelliJ's: every notch adds to a target and the view eases there over a fixed
    // short time, starting again from wherever it is, so fast scrolling stays smooth and stops right away
    public static void smooth(JScrollPane pane, boolean hideBars) {
        if (hideBars) {
            pane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            pane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        }
        pane.setWheelScrollingEnabled(false);
        double[] from = new double[2], target = new double[2];
        long[] started = {0};
        Timer glide = new Timer(5, null);
        glide.addActionListener(e -> {
            JViewport vp = pane.getViewport();
            if (vp.getView() == null) {
                glide.stop();
                return;
            }
            double p = Math.min(1, (System.nanoTime() - started[0]) / 1e6 / 150.0);
            double ease = 1 - Math.pow(1 - p, 3);
            int x = (int) Math.round(from[0] + (target[0] - from[0]) * ease);
            int y = (int) Math.round(from[1] + (target[1] - from[1]) * ease);
            vp.setViewPosition(new Point(x, y));
            if (p >= 1) glide.stop();
        });
        pane.addMouseWheelListener(e -> {
            JViewport vp = pane.getViewport();
            if (vp.getView() == null) return;
            Point at = vp.getViewPosition();
            if (!glide.isRunning()) {
                target[0] = at.x;
                target[1] = at.y;
            }
            from[0] = at.x;
            from[1] = at.y;
            int axis = e.isShiftDown() ? 0 : 1;
            int max = axis == 0 ? vp.getView().getWidth() - vp.getWidth() : vp.getView().getHeight() - vp.getHeight();
            target[axis] = Math.max(0, Math.min(Math.max(0, max), target[axis] + e.getPreciseWheelRotation() * 90));
            started[0] = System.nanoTime();
            glide.start();
            e.consume();
        });
    }

    // shortens text with an ellipsis so it never runs past the space it has
    public static String fit(Graphics2D g, String text, int width) {
        java.awt.FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(text) <= width) return text;
        String dots = "…";
        int end = text.length();
        while (end > 0 && fm.stringWidth(text.substring(0, end) + dots) > width) end--;
        return text.substring(0, end).stripTrailing() + dots;
    }

    public static String hex(Color c) {
        return String.format("#%06x", c.getRGB() & 0xffffff);
    }

    public static Font heading(float size) {
        return new Font("Segoe UI Semibold", Font.PLAIN, 12).deriveFont(size);
    }

}
