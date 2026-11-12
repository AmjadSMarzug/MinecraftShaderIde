package me.thesnowbound.shaderIDE.learn;

import me.thesnowbound.shaderIDE.editor.CodeEditor;
import me.thesnowbound.shaderIDE.ui.Fx;

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

// takes over the left side of the window while a lesson is open and ticks the tasks off as the code changes
public final class Panel extends JPanel {
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
