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

// a tabbed pane with no tabs, the effect bar does that job
public final class Bare extends JTabbedPane {
    @Override
    public void updateUI() {
        setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int placement, int runs, int height) {
                return 0;
            }

            @Override
            protected Insets getContentBorderInsets(int placement) {
                return new Insets(0, 0, 0, 0);
            }

            @Override
            protected void paintTabArea(Graphics g, int placement, int selected) {
            }

            @Override
            protected void paintContentBorder(Graphics g, int placement, int selected) {
            }
        });
    }
}
