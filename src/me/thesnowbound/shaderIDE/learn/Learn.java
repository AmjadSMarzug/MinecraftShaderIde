package me.thesnowbound.shaderIDE.learn;

import me.thesnowbound.shaderIDE.Main;
import me.thesnowbound.shaderIDE.ui.Fx;
import me.thesnowbound.shaderIDE.ui.Theme;
import me.thesnowbound.shaderIDE.editor.CodeEditor;

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
    public record Step(String task, String hint, String example, Predicate<String> done) {
    }

    public record Lesson(String id, String title, String blurb, int minutes, String theory, String starter, List<Step> steps) {
    }

    public record Guide(String title, String blurb, String html) {
    }

    private static final Preferences PREFS = Preferences.userNodeForPackage(Main.class);

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
    private static final String MONO = CodeEditor.mono(12).getFamily();

    private static String css() {
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
        return new Fx.Soft(text, true);
    }

    public static JButton secondary(String text) {
        return new Fx.Soft(text, false);
    }

    private static JComponent left(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private static JPanel column() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        return p;
    }

    // follows the scroll pane's width so text wraps, but keeps its own height so nothing gets stretched
    public static final class Page extends JPanel implements javax.swing.Scrollable {
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

    // a thin rounded bar that slides to its new value instead of jumping
    public static final class Progress extends JComponent {
        private float shown;
        private float target;
        private final Timer tick = new Timer(10, null);

        public Progress() {
            setPreferredSize(new Dimension(200, 6));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));

        }

        public void set(float value) {
            target = Math.max(0, Math.min(1, value));
            shown = target;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight();
            g2.setColor(Theme.current.border());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), h, h, h));
            if (shown > 0.001f) {
                g2.setColor(Theme.current.accent());
                g2.fill(new RoundRectangle2D.Float(0, 0, Math.max(h, getWidth() * shown), h, h, h));
            }
            g2.dispose();
        }
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

    private static String markKey(Lesson lesson, int step) {
        return "marked." + lesson.id() + "." + step;
    }

    private static void forget(Lesson lesson) {
        for (int i = 0; i < lesson.steps().size(); i++) PREFS.remove(markKey(lesson, i));
    }

    // takes over the left side of the window while a lesson is open and ticks the tasks off as the code changes
    public static final class Panel extends JPanel {
        public record Actions(Runnable reset, Runnable exit, Runnable next) {
        }

        private final Lesson lesson;
        private final List<Tick> ticks = new ArrayList<>();
        private final List<JComponent> details = new ArrayList<>();
        private final JLabel count = new JLabel();
        private final Progress bar = new Progress();
        private final JButton nextButton = primary("Next lesson");
        private final JComponent doneNote;
        private boolean finishedOnce;
        private String lastSource = "";

        public Panel(Lesson lesson, Actions actions) {
            super(new BorderLayout());
            this.lesson = lesson;
            setBackground(Fx.SIDE);

            JPanel head = column();
            head.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
            int n = LESSONS.indexOf(lesson) + 1;
            JLabel kicker = new JLabel("LESSON " + n + " OF " + LESSONS.size());
            kicker.setFont(kicker.getFont().deriveFont(Font.BOLD, 10f));
            kicker.setForeground(Fx.DIM);
            JLabel title = new JLabel(lesson.title());
            title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
            title.setForeground(Fx.WHITE);
            count.setForeground(Fx.DIM);
            head.add(left(kicker));
            head.add(Box.createVerticalStrut(3));
            head.add(left(title));
            head.add(Box.createVerticalStrut(10));
            head.add(left(bar));
            head.add(Box.createVerticalStrut(6));
            head.add(left(count));
            add(head, BorderLayout.NORTH);

            JPanel body = new Page();
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
            JEditorPane theory = page(lesson.theory());
            JButton about = secondary("Hide the explanation");
            about.addActionListener(e -> {
                theory.setVisible(!theory.isVisible());
                about.setText(theory.isVisible() ? "Hide the explanation" : "About this lesson");
                body.revalidate();
            });
            body.add(left(about));
            body.add(Box.createVerticalStrut(8));
            body.add(left(theory));
            body.add(Box.createVerticalStrut(10));

            for (Step s : lesson.steps()) {
                Tick t = new Tick(s.task());
                ticks.add(t);
                body.add(left(t));
                JComponent detail = detail(s, ticks.size() - 1);
                detail.setVisible(false);
                details.add(detail);
                body.add(left(detail));
            }
            doneNote = challenge(lesson);
            doneNote.setVisible(false);
            body.add(Box.createVerticalStrut(10));
            body.add(left(doneNote));
            add(scroller(body), BorderLayout.CENTER);

            JButton reset = secondary("Reset code");
            reset.addActionListener(e -> {
                forget(lesson);
                actions.reset().run();
            });
            JButton exit = secondary("Exit");
            exit.addActionListener(e -> actions.exit().run());
            nextButton.addActionListener(e -> actions.next().run());
            nextButton.setVisible(false);
            JPanel foot = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            foot.setOpaque(false);
            foot.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Fx.LINE),
                    BorderFactory.createEmptyBorder(10, 6, 2, 6)));
            foot.add(reset);
            foot.add(exit);
            foot.add(nextButton);
            add(foot, BorderLayout.SOUTH);
        }

        private JComponent detail(Step s, int index) {
            JPanel box = column();
            box.setBorder(BorderFactory.createEmptyBorder(2, 34, 12, 4));
            JTextArea hint = text(s.hint(), getFont().deriveFont(12.5f), Fx.DIM);
            JTextArea code = text(s.example(), CodeEditor.mono(12), Fx.TEXT);
            code.setOpaque(true);
            code.setBackground(Fx.t().editor());
            code.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            code.setVisible(false);
            JButton show = secondary("Show example");
            JButton copy = secondary("Copy");
            copy.setVisible(false);
            show.addActionListener(e -> {
                code.setVisible(!code.isVisible());
                copy.setVisible(code.isVisible());
                show.setText(code.isVisible() ? "Hide example" : "Show example");
                box.revalidate();
            });
            copy.addActionListener(e -> {
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(s.example()), null);
                Fx.toast((JFrame) SwingUtilities.getWindowAncestor(this), "Copied the example", false, null, null);
            });
            // the checks read the code, not the picture, so they can miss a right answer written a different way
            JButton mine = secondary("It works, mark done");
            mine.setToolTipText("Use this when the preview shows what the task asks for but the check hasn't ticked");
            mine.addActionListener(e -> {
                PREFS.putBoolean(markKey(lesson, index), true);
                check(lastSource);
            });
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            buttons.setOpaque(false);
            buttons.add(show);
            buttons.add(Box.createHorizontalStrut(6));
            buttons.add(copy);
            buttons.add(Box.createHorizontalStrut(6));
            buttons.add(mine);
            box.add(left(hint));
            box.add(Box.createVerticalStrut(8));
            box.add(left(buttons));
            box.add(Box.createVerticalStrut(8));
            box.add(left(code));
            return box;
        }

        Lesson lesson() {
            return lesson;
        }

        private static JComponent challenge(Lesson lesson) {
            JPanel box = column();
            box.setOpaque(true);
            box.setBackground(Fx.t().editor());
            box.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
            JLabel head = new JLabel("CHALLENGE");
            head.setFont(head.getFont().deriveFont(Font.BOLD, 10f));
            head.setForeground(Fx.WHITE);
            JTextArea task = text(CHALLENGES.getOrDefault(lesson.id(), "Keep playing with it, the preview follows every change."), new JLabel().getFont().deriveFont(13f), Fx.TEXT);
            JTextArea note = text("Every task is done. This one has no example and no check, try it on your own.", new JLabel().getFont().deriveFont(12f), Fx.DIM);
            box.add(left(head));
            box.add(Box.createVerticalStrut(6));
            box.add(left(task));
            box.add(Box.createVerticalStrut(8));
            box.add(left(note));
            return box;
        }

        public void check(String source) {
            lastSource = source;
            int done = 0;
            int current = -1;
            for (int i = 0; i < ticks.size(); i++) {
                boolean ok = PREFS.getBoolean(markKey(lesson, i), false) || lesson.steps().get(i).done().test(source);
                ticks.get(i).set(ok);
                if (ok) done++;
                else if (current < 0) current = i;
            }
            for (int i = 0; i < ticks.size(); i++) {
                ticks.get(i).current(i == current);
                details.get(i).setVisible(i == current);
            }
            count.setText(done + " of " + ticks.size() + " tasks done");
            bar.set(done / (float) ticks.size());
            boolean all = current < 0;
            if (all && !finishedOnce) {
                markFinished(lesson);
                java.awt.Window w = SwingUtilities.getWindowAncestor(this);
                if (w instanceof JFrame f) Fx.toast(f, "Lesson complete: " + lesson.title(), false, null, null);
            }
            finishedOnce = all;
            doneNote.setVisible(all);
            nextButton.setVisible(all && LESSONS.indexOf(lesson) < LESSONS.size() - 1);
            revalidate();
            repaint();
        }

        // a wrapping text area works out its height from its width, so it gets the sidebar's narrowest width up front
        private static JTextArea text(String s, Font font, Color color) {
            JTextArea t = new JTextArea(s) {
                @Override
                public Dimension getMaximumSize() {
                    return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
                }
            };
            t.setFont(font);
            t.setForeground(color);
            t.setLineWrap(true);
            t.setWrapStyleWord(true);
            t.setEditable(false);
            t.setOpaque(false);
            t.setBorder(null);
            t.setSize(340, Short.MAX_VALUE);
            return t;
        }
    }

    // a task row whose circle fills in with a quick pop when it's done
    private static final class Tick extends JComponent {
        private final String text;
        private boolean done;
        private boolean current;
        private float fill;

        Tick(String text) {
            this.text = text;
            setPreferredSize(new Dimension(360, 32));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        }

        void set(boolean ok) {
            if (ok == done) return;
            done = ok;
            fill = ok ? 1f : 0f;
            repaint();
        }

        void current(boolean c) {
            if (c == current) return;
            current = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (current) {
                g2.setColor(Theme.current.hover());
                g2.fill(new RoundRectangle2D.Float(0, 1, getWidth(), getHeight() - 2, 8, 8));
            }
            float r = 8;
            Theme t = Theme.current;
            float cx = 15, cy = getHeight() / 2f;
            g2.setStroke(new BasicStroke(1.4f));
            g2.setColor(current ? t.strong() : t.faint());
            g2.draw(new Ellipse2D.Float(cx - 8, cy - 8, 16, 16));
            if (fill > 0.01f) {
                g2.setColor(t.good());
                g2.fill(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
                g2.setColor(t.dark() ? Color.BLACK : Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int) cx - 4, (int) cy, (int) cx - 1, (int) cy + 3);
                g2.drawLine((int) cx - 1, (int) cy + 3, (int) cx + 4, (int) cy - 3);
            }
            g2.setFont(getFont().deriveFont(current ? Font.BOLD : Font.PLAIN, 13f));
            g2.setColor(done ? t.dim() : current ? t.strong() : t.text());
            g2.drawString(text, 34, cy + 5);
            g2.dispose();
        }
    }
}
