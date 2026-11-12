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

// the row of effects above the editor. it stands in for tabs, so it also lists any other file that's open
public final class EffectBar extends JPanel {
    public record Entry(Path file, String label, Image thumb, boolean dirty, boolean closable) {
    }

    private final Consumer<Path> open;
    private Consumer<Path> close = p -> { };
    private final BiConsumer<Path, MouseEvent> menu;
    private final Runnable add;

    public EffectBar(Consumer<Path> open, BiConsumer<Path, MouseEvent> menu, Runnable add) {
        super(new FlowLayout(FlowLayout.LEFT, 6, 0));
        this.open = open;
        this.menu = menu;
        this.add = add;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 8));
    }

    public void onClose(Consumer<Path> close) {
        this.close = close;
    }

    public void set(List<Entry> entries, Path active) {
        removeAll();
        for (Entry e : entries) add(new Chip(e, e.file().equals(active)));
        if (add != null) add(new Chip(null, false));
        revalidate();
        repaint();
    }

    private final class Chip extends JComponent {
        private final Entry entry;
        private final boolean on;
        private boolean hot;
        private boolean overClose;
        private final Glide glow = new Glide(this, 0);

        Chip(Entry entry, boolean on) {
            this.entry = entry;
            this.on = on;
            int w = entry == null ? 40 : getFontMetrics(chipFont()).stringWidth(text()) + (entry.closable() ? 76 : 54);
            setPreferredSize(new Dimension(w, 40));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(entry == null ? "New effect" : entry.file().toString());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hot = true;
                    glow.to(1);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hot = false;
                    glow.to(0);
                    setOverClose(false);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    popup(e);
                    if (!e.isPopupTrigger() && SwingUtilities.isLeftMouseButton(e)) {
                        if (entry == null) add.run();
                        else if (entry.closable() && e.getX() > getWidth() - 28) close.accept(entry.file());
                        else open.accept(entry.file());
                    } else if (entry != null && entry.closable() && SwingUtilities.isMiddleMouseButton(e)) {
                        close.accept(entry.file());
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    popup(e);
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    setOverClose(entry != null && entry.closable() && e.getX() > getWidth() - 28);
                }
            });
        }

        private void setOverClose(boolean over) {
            if (over != overClose) {
                overClose = over;
                repaint();
            }
        }

        private void popup(MouseEvent e) {
            if (e.isPopupTrigger() && entry != null) menu.accept(entry.file(), e);
        }

        private Font chipFont() {
            return new Font("Segoe UI Semibold", Font.PLAIN, 14);
        }

        private String text() {
            return entry.label() + (entry.dirty() ? "  ●" : "");
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            Theme t = Fx.t();
            int h = getHeight();
            if (on || glow.get() > 0) {
                g2.setColor(on ? t.select() : Fx.mix(t.frame(), t.hover(), glow.get()));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), h, 10, 10));
            }
            if (entry == null) {
                g2.setColor(t.dim());
                int c = getWidth() / 2, m = h / 2;
                g2.fillRect(c - 7, m, 14, 1);
                g2.fillRect(c, m - 7, 1, 14);
            } else {
                RoundRectangle2D.Float square = new RoundRectangle2D.Float(8, (h - 24) / 2f, 24, 24, 7, 7);
                if (entry.thumb() != null) {
                    cover(g2, entry.thumb(), square);
                } else {
                    g2.setColor(t.raised());
                    g2.fill(square);
                }
                g2.setFont(chipFont());
                g2.setColor(on ? t.strong() : Fx.mix(t.dim(), t.text(), glow.get()));
                g2.drawString(text(), 42, h / 2f + 5);
                if (entry.closable()) {
                    int x = getWidth() - 20, m = h / 2;
                    if (overClose) {
                        g2.setColor(Fx.mix(t.frame(), t.text(), 0.22f));
                        g2.fill(new Ellipse2D.Float(x - 10, m - 10, 20, 20));
                    }
                    double bright = overClose ? 1.0 : Math.max(glow.get(), on ? 0.6 : 0.35);
                    g2.setColor(on && !overClose ? t.strong() : Fx.mix(t.dim(), t.text(), bright));
                    g2.setStroke(new java.awt.BasicStroke(1.4f));
                    g2.drawLine(x - 4, m - 4, x + 4, m + 4);
                    g2.drawLine(x - 4, m + 4, x + 4, m - 4);
                }
            }
            g2.dispose();
        }
    }
}
