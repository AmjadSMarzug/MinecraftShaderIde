package me.thesnowbound.shaderIDE.learn;

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

// follows the scroll pane's width so text wraps, but keeps its own height so nothing gets stretched
public final class Page extends JPanel implements javax.swing.Scrollable {
    public Page() {
        setOpaque(false);
    }

    public Page(java.awt.LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(java.awt.Rectangle r, int orientation, int direction) {
        return 18;
    }

    public int getScrollableBlockIncrement(java.awt.Rectangle r, int orientation, int direction) {
        return r.height - 40;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
