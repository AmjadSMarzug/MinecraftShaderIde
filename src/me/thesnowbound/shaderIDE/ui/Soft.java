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

// a button that's filled with the accent when it's the main one, outlined otherwise
public final class Soft extends JButton {
    public Soft(String text, boolean bold) {
        super(text);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        putClientProperty("FlatLaf.style", "margin: 5,16,5,16");
        if (bold) {
            setBackground(t().accent());
            setForeground(t().onAccent());
        }
    }
}
