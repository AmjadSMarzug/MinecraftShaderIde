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

// the parts both the home screen and the workspace share
public final class Chrome {
    public static final String ICONS = Stream.of("Segoe Fluent Icons", "Segoe MDL2 Assets")
            .filter(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())::contains)
            .findFirst().orElse(null);
    private Chrome() {
    }

    public static String pretty(String id) {
        String s = id.replace('_', ' ').strip();
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        return g2;
    }

    // fills the shape with the picture, cropping whatever doesn't fit
    public static void cover(Graphics2D g, Image img, Shape shape) {
        java.awt.Rectangle b = shape.getBounds();
        double k = Math.max(b.width / (double) img.getWidth(null), b.height / (double) img.getHeight(null));
        int w = (int) Math.ceil(img.getWidth(null) * k), h = (int) Math.ceil(img.getHeight(null) * k);
        Shape old = g.getClip();
        g.clip(shape);
        g.drawImage(img, b.x + (b.width - w) / 2, b.y + (b.height - h) / 2, w, h, null);
        g.setClip(old);
    }

    public static JButton action(String text, int height, Runnable action) {
        String label = ICONS != null ? "<html><font face='" + ICONS + "'>&#xE768;</font>&nbsp;&nbsp;" + text + "</html>" : text;
        JButton b = new Soft(label, true);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 16f));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 40, height));
        b.addActionListener(e -> action.run());
        return b;
    }

}
