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

// white when there's something to save, a quiet outline once everything is
public final class SaveButton extends JComponent {
    private final Runnable action;
    private int unsaved;
    private final Glide fill = new Glide(this, 0), glow = new Glide(this, 0);

    public SaveButton(Runnable action) {
        this.action = action;
        setPreferredSize(new Dimension(104, 40));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                glow.to(1);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                glow.to(0);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (unsaved > 0 && SwingUtilities.isLeftMouseButton(e)) action.run();
            }
        });
    }

    public void unsaved(int count) {
        unsaved = count;
        fill.to(count > 0 ? 1 : 0);
        setCursor(Cursor.getPredefinedCursor(count > 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        setToolTipText(count > 1 ? "Save every changed file (Ctrl+Shift+S)" : count == 1 ? "Save (Ctrl+S)" : "Everything is saved");
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = smooth(g);
        Theme t = Fx.t();
        int w = getWidth(), h = getHeight();
        RoundRectangle2D.Float shape = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 10, 10);
        String label = unsaved > 1 ? "Save all" : unsaved == 1 ? "Save" : "Saved";
        double f = fill.get();
        java.awt.Color solid = Fx.mix(t.accent(), mix(t.accent(), t.frame()), unsaved > 0 ? glow.get() : 0);
        g2.setColor(Fx.mix(t.frame(), solid, f));
        g2.fill(shape);
        g2.setColor(Fx.mix(t.border(), solid, f));
        g2.draw(shape);
        g2.setColor(Fx.mix(t.dim(), t.onAccent(), f));
        g2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        g2.drawString(label, (w - g2.getFontMetrics().stringWidth(label)) / 2f, h / 2f + 5);
        g2.dispose();
    }

    private static java.awt.Color mix(java.awt.Color a, java.awt.Color b) {
        return new java.awt.Color((a.getRed() * 7 + b.getRed()) / 8, (a.getGreen() * 7 + b.getGreen()) / 8, (a.getBlue() * 7 + b.getBlue()) / 8);
    }
}
