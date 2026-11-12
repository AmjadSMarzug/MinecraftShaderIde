package me.thesnowbound.shaderIDE.learn;

import me.thesnowbound.shaderIDE.Main;
import me.thesnowbound.shaderIDE.editor.CodeEditor;
import me.thesnowbound.shaderIDE.ui.Fx;
import me.thesnowbound.shaderIDE.ui.Soft;
import me.thesnowbound.shaderIDE.ui.Theme;

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

// the guides and the interactive lessons. a lesson is starter code plus tasks that get ticked off
// as soon as the code in the editor does what the task asks
public final class Learn {
    static final Preferences PREFS = Preferences.userNodeForPackage(Main.class);

    private Learn() {
    }

    public static boolean finished(Lesson lesson) {
        return PREFS.getBoolean("done." + lesson.id(), false);
    }

    public static void markFinished(Lesson lesson) {
        PREFS.putBoolean("done." + lesson.id(), true);
    }

    public static int finishedCount() {
        return (int) LESSONS.stream().filter(Learn::finished).count();
    }

    public static final List<Lesson> LESSONS = Lessons.LESSONS;
    static final java.util.Map<String, String> CHALLENGES = Lessons.CHALLENGES;
    public static final List<Guide> GUIDES = Guides.GUIDES;

    // swing's html only takes a single plain family name, and the bundled mono font is registered by the editor
    static final String MONO = CodeEditor.mono(12).getFamily();

    static String css() {
        Theme t = Theme.current;
        String text = Fx.hex(t.text()), strong = Fx.hex(t.strong()), dim = Fx.hex(t.dim()), box = Fx.hex(t.editor());
        return "body{font-family:'Segoe UI';font-size:13px;color:" + text + ";margin:0}"
                + "h1{font-family:'Segoe UI';font-size:22px;color:" + strong + ";font-weight:bold;margin:0 0 4px 0}"
                + "h2{font-family:'Segoe UI';font-size:15px;color:" + strong + ";font-weight:bold;margin:18px 0 6px 0}"
                + "p{margin:0 0 9px 0}b{color:" + strong + "}code{font-family:" + MONO + ";color:" + Fx.hex(t.keyword()) + ";font-size:10px}"
                + "pre{font-family:" + MONO + ";font-size:10px;color:" + text + ";background:" + box + ";padding:10px;margin:4px 0 12px 0}"
                + "td{color:" + text + ";padding:3px 14px 3px 0;vertical-align:top}.dim{color:" + dim + "}.lead{font-size:14px;color:" + dim + "}"
                + ".tip{background:" + box + ";padding:10px 12px;margin:6px 0 12px 0;color:" + text + "}";
    }

    public static JEditorPane page(String html) {
        JEditorPane pane = new JEditorPane() {
            // in a plain column the pane measures itself as if nothing wrapped and cuts the rest off,
            // so its height is worked out for the width it's actually given
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                int w = getWidth() > 0 ? getWidth() : getParent() != null ? getParent().getWidth() : 0;
                if (w <= 0) return d;
                javax.swing.text.View root = getUI().getRootView(this);
                root.setSize(w, Integer.MAX_VALUE);
                java.awt.Insets in = getInsets();
                return new Dimension(w, (int) Math.ceil(root.getPreferredSpan(javax.swing.text.View.Y_AXIS)) + in.top + in.bottom);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.getStyleSheet().addRule(css());
        pane.setEditorKit(kit);
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setText("<html><body>" + html + "</body></html>");
        pane.setCaretPosition(0);
        return pane;
    }

    public static JScrollPane scroller(JComponent c) {
        JScrollPane s = new JScrollPane(c);
        s.setBorder(null);
        s.setOpaque(false);
        s.getViewport().setOpaque(false);
        Fx.smooth(s, true);
        return s;
    }

    public static JButton primary(String text) {
        return new Soft(text, true);
    }

    public static JButton secondary(String text) {
        return new Soft(text, false);
    }

    static JComponent left(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    static JPanel column() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        return p;
    }

    // the page a lesson card opens in the home screen's Learn section
    public static String lessonHtml(Lesson lesson) {
        int n = LESSONS.indexOf(lesson) + 1;
        StringBuilder html = new StringBuilder("<p class='dim'>LESSON " + n + " OF " + LESSONS.size() + "</p><h1>" + lesson.title()
                + "</h1><p class='lead'>" + lesson.steps().size() + " tasks &nbsp;&middot;&nbsp; about " + lesson.minutes() + " minutes"
                + (finished(lesson) ? " &nbsp;&middot;&nbsp; done" : "") + "</p>" + lesson.theory() + "<h2>What you'll do</h2>");
        int k = 1;
        for (Step s : lesson.steps()) html.append("<p><b>").append(k++).append(".</b>&nbsp; ").append(s.task()).append("</p>");
        html.append("<div class='tip'>The code opens in the editor with the preview running. The lesson takes over the left side,"
                + " and each task ticks off the moment your code does it.</div>");
        return html.toString();
    }

    public static void resetProgress() {
        for (Lesson l : LESSONS) {
            PREFS.remove("done." + l.id());
            forget(l);
        }
    }

    static String markKey(Lesson lesson, int step) {
        return "marked." + lesson.id() + "." + step;
    }

    static void forget(Lesson lesson) {
        for (int i = 0; i < lesson.steps().size(); i++) PREFS.remove(markKey(lesson, i));
    }

}
