package me.thesnowbound.shaderIDE.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static me.thesnowbound.shaderIDE.ui.Chrome.*;

// the icon rail down the left of the window
public final class Rail extends JComponent {
    public record Item(String key, String glyph, String label, Runnable action) {
    }

    private static final int ROW = 72, TOP = 74;
    private final List<Item> items;
    private final Item bottom;
    private final Image logo;
    private String selected = "";
    private Item from;
    private Item hover;
    private final Glide slide = new Glide(this, 1);
    private final java.util.Map<Item, Glide> hovers = new java.util.HashMap<>();

    public Rail(List<Item> items, Item bottom) {
        this.items = items;
        this.bottom = bottom;
        java.net.URL icon = Rail.class.getResource("/icon.png");
        logo = icon == null ? null : new javax.swing.ImageIcon(icon).getImage();
        setPreferredSize(new Dimension(84, 0));
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hover = at(e.getY());
                for (Item i : all()) glow(i).to(i == hover ? 1 : 0);
                setCursor(Cursor.getPredefinedCursor(hover == null ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = null;
                for (Item i : all()) glow(i).to(0);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Item i = at(e.getY());
                if (i != null) i.action().run();
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    public void select(String key) {
        if (key.equals(selected)) return;
        from = find(selected);
        selected = key;
        slide.value0();
        slide.to(1);
        repaint();
    }

    private List<Item> all() {
        return Stream.concat(items.stream(), Stream.of(bottom)).toList();
    }

    private Item find(String key) {
        for (Item i : all()) if (i.key().equals(key)) return i;
        return null;
    }

    private Glide glow(Item i) {
        return hovers.computeIfAbsent(i, k -> new Glide(this, 0));
    }

    private int top(Item i) {
        return i == bottom ? getHeight() - ROW - 10 : TOP + items.indexOf(i) * (ROW + 6);
    }

    private Item at(int y) {
        for (Item i : items) if (y >= top(i) && y < top(i) + ROW) return i;
        return y >= top(bottom) && y < top(bottom) + ROW ? bottom : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = smooth(g);
        Theme t = Fx.t();
        g2.setColor(t.frame());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(t.border());
        g2.fillRect(getWidth() - 1, 0, 1, getHeight());
        if (logo != null) g2.drawImage(logo, (getWidth() - 38) / 2, 18, 38, 38, null);
        int w = getWidth();
        for (Item i : all()) {
            double a = glow(i).get();
            if (a > 0 && !i.key().equals(selected)) {
                g2.setColor(Fx.mix(t.frame(), t.hover(), a));
                g2.fill(new RoundRectangle2D.Float(8, top(i), w - 17, ROW - 4, 12, 12));
            }
        }
        Item on = find(selected);
        if (on != null) {
            // the pill travels from the last section to this one
            double p = slide.get();
            float y = (float) (from == null ? top(on) : top(from) + (top(on) - top(from)) * p);
            g2.setColor(t.select());
            g2.fill(new RoundRectangle2D.Float(8, y, w - 17, ROW - 4, 12, 12));
            g2.setColor(t.strong());
            g2.fill(new RoundRectangle2D.Float(0, y + 18, 3, ROW - 40, 3, 3));
        }
        for (Item i : all()) paintItem(g2, t, i);
        g2.dispose();
    }

    private void paintItem(Graphics2D g2, Theme t, Item i) {
        boolean on = i.key().equals(selected);
        int y = top(i), w = getWidth();
        g2.setColor(on ? Fx.mix(t.dim(), t.strong(), slide.get()) : Fx.mix(t.dim(), t.text(), glow(i).get()));
        if (ICONS != null) {
            g2.setFont(new Font(ICONS, Font.PLAIN, 21));
            g2.drawString(i.glyph(), (w - 1 - g2.getFontMetrics().stringWidth(i.glyph())) / 2f, y + 34);
        }
        g2.setFont(getFont() == null ? new Font("Segoe UI", Font.PLAIN, 12) : getFont().deriveFont(12f));
        g2.drawString(i.label(), (w - 1 - g2.getFontMetrics().stringWidth(i.label())) / 2f, y + 56);
    }
}
