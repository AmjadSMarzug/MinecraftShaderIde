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

// a number that slides toward wherever it's told to go and repaints its owner on the way
public final class Glide {
    private final JComponent owner;
    private double value, target;
    private Timer timer;

    public Glide(JComponent owner, double start) {
        this.owner = owner;
        value = target = start;
    }

    public double get() {
        return value;
    }

    void value0() {
        value = 0;
    }

    public void to(double t) {
        target = t;
        if (!motion) {
            value = t;
            owner.repaint();
            return;
        }
        if (timer != null || value == target) return;
        timer = new Timer(1000 / 60, e -> {
            value += (target - value) * 0.22;
            if (Math.abs(target - value) < 0.004) {
                value = target;
                timer.stop();
                timer = null;
            }
            owner.repaint();
        });
        timer.start();
    }
}
