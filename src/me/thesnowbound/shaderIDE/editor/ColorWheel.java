package me.thesnowbound.shaderIDE.editor;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

// a colour wheel: hue is the angle around the ring, saturation the distance from the middle, and a slider
// underneath sets brightness. every change is handed back as r, g, b from 0 to 1
public final class ColorWheel extends JComponent {
    private static final int D = 180;
    private static final int PAD = 10;

    private float h, s, v;
    private float discV = -1;
    private BufferedImage disc;
    private final Consumer<float[]> changed;

    ColorWheel(float[] rgb, Consumer<float[]> changed) {
        this.changed = changed;
        int r = Math.round(clamp01(rgb[0]) * 255), g = Math.round(clamp01(rgb[1]) * 255), b = Math.round(clamp01(rgb[2]) * 255);
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        h = hsb[0];
        s = hsb[1];
        v = hsb[2];
        setPreferredSize(new Dimension(D + PAD * 2, D + PAD * 2 + 34));
        MouseAdapter press = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag(e);
            }
        };
        addMouseListener(press);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drag(e);
            }
        });
    }

    // the colour as r, g, b from 0 to 1
    public float[] rgb() {
        int c = Color.HSBtoRGB(h, s, v);
        return new float[] {((c >> 16) & 255) / 255f, ((c >> 8) & 255) / 255f, (c & 255) / 255f};
    }

    private void drag(MouseEvent e) {
        int x = e.getX() - PAD, y = e.getY() - PAD;
        if (y >= D + 12) {
            v = clamp01((x) / (float) D);
        } else {
            double dx = x - D / 2.0, dy = y - D / 2.0;
            double radius = D / 2.0;
            s = (float) Math.min(Math.sqrt(dx * dx + dy * dy) / radius, 1.0);
            double angle = Math.atan2(dy, dx) / (2 * Math.PI);
            h = (float) (angle < 0 ? angle + 1 : angle);
        }
        repaint();
        changed.accept(rgb());
    }

    private void buildDisc() {
        disc = new BufferedImage(D, D, BufferedImage.TYPE_INT_ARGB);
        double radius = D / 2.0;
        for (int y = 0; y < D; y++) {
            for (int x = 0; x < D; x++) {
                double dx = x + 0.5 - radius, dy = y + 0.5 - radius;
                double d = Math.sqrt(dx * dx + dy * dy) / radius;
                if (d > 1) continue;
                double angle = Math.atan2(dy, dx) / (2 * Math.PI);
                float hue = (float) (angle < 0 ? angle + 1 : angle);
                disc.setRGB(x, y, Color.HSBtoRGB(hue, (float) d, v) | 0xff000000);
            }
        }
        discV = v;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (disc == null || discV != v) buildDisc();
        g2.drawImage(disc, PAD, PAD, null);

        double angle = h * 2 * Math.PI;
        double mx = PAD + D / 2.0 + Math.cos(angle) * s * D / 2.0;
        double my = PAD + D / 2.0 + Math.sin(angle) * s * D / 2.0;
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(Color.BLACK);
        g2.drawOval((int) mx - 7, (int) my - 7, 14, 14);
        g2.setColor(Color.WHITE);
        g2.drawOval((int) mx - 6, (int) my - 6, 12, 12);

        int sy = PAD + D + 12;
        g2.setPaint(new GradientPaint(PAD, sy, Color.BLACK, PAD + D, sy, Color.HSBtoRGB(h, s, 1f) == 0 ? Color.BLACK : new Color(Color.HSBtoRGB(h, s, 1f))));
        g2.fillRoundRect(PAD, sy, D, 16, 6, 6);
        int vx = PAD + Math.round(v * D);
        g2.setColor(Color.BLACK);
        g2.fillRect(vx - 2, sy - 3, 5, 22);
        g2.setColor(Color.WHITE);
        g2.fillRect(vx - 1, sy - 2, 3, 20);
    }

    public static float clamp01(float x) {
        return Math.max(0f, Math.min(1f, x));
    }
}
