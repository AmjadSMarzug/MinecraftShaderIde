package me.thesnowbound.shaderIDE.preview;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;

// a chat box in the game's style, showing a text effect. type in the box to change the words
public final class TextChat extends JPanel {
    static final String FONT_JAR = "textFontJar";

    private final TextCanvas canvas;
    private final JTextField words = new JTextField("Shader IDE", 24);
    private final JLabel status = new JLabel(" ");
    private final JButton jar = new JButton("Use my Minecraft jar...");

    public TextChat(String source) {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        canvas = new TextCanvas(msg -> SwingUtilities.invokeLater(() -> {
            status.setText(msg.isBlank() ? "compiles" : msg);
            status.setForeground(msg.isBlank() ? new Color(0x6fbf73) : new Color(0xe06c6c));
        }));
        canvas.setPreferredSize(new java.awt.Dimension(640, 160));
        canvas.setSource(source);
        loadFont();

        words.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                canvas.setText(words.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                canvas.setText(words.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                canvas.setText(words.getText());
            }
        });
        jar.addActionListener(e -> pickJar());
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.add(new JLabel("Text: "), BorderLayout.WEST);
        top.add(words, BorderLayout.CENTER);
        top.add(jar, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        status.setFont(status.getFont().deriveFont(Font.PLAIN, 12f));
        add(status, BorderLayout.SOUTH);
        // the game's chat is a flat box, so the canvas is drawn at the window's own rate
        new Timer(33, e -> canvas.render()).start();
    }

    public void setSource(String source) {
        canvas.setSource(source);
    }

    // the font is read from the user's Minecraft jar when one was picked, otherwise the bundled font is used
    private void loadFont() {
        String saved = Preferences.userNodeForPackage(TextChat.class).get(FONT_JAR, "");
        if (saved.isBlank()) return;
        try {
            BufferedImage font = TextFont.fromJar(Path.of(saved));
            if (font != null) canvas.setGlyphs(font);
        } catch (Exception e) {
            status.setText("couldn't read that jar, using the bundled font");
        }
    }

    private void pickJar() {
        JFileChooser pick = new JFileChooser();
        pick.setDialogTitle("Pick your Minecraft jar (the one in .minecraft/versions)");
        if (pick.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = pick.getSelectedFile();
        Preferences.userNodeForPackage(TextChat.class).put(FONT_JAR, file.getAbsolutePath());
        loadFont();
        status.setText("using the font from " + file.getName());
    }

    // opens the chat box in its own window
    public static void open(String source) {
        JFrame frame = new JFrame("Chat preview");
        frame.add(new TextChat(source));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
