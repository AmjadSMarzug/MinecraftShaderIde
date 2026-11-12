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

// the hogyoku gem on a rounded tile
public final class Badge extends JComponent {
    private static final java.awt.image.BufferedImage GEM = loadGem();

    Badge() {
        setPreferredSize(new Dimension(150, 150));
        setMaximumSize(getPreferredSize());
        setOpaque(false);
    }

    private static java.awt.image.BufferedImage loadGem() {
        try {
            return javax.imageio.ImageIO.read(Fx.class.getResource("/icon.png"));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        int w = getWidth(), h = getHeight();
        float arc = Math.min(28f, Math.min(w, h) * 0.26f);
        g2.setColor(t().dark() ? new Color(0x1a1a1a) : new Color(0x2b2d30));
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
        if (GEM != null) {
            int size = (int) (Math.min(w, h) * 0.78);
            g2.drawImage(GEM, (w - size) / 2, (h - size) / 2, size, size, null);
        }
        g2.dispose();
    }
}
