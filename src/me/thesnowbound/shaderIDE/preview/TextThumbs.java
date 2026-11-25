package me.thesnowbound.shaderIDE.preview;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

// draws every text example in the chat box and saves a picture of each one, for the gallery
// usage: TextThumbs <examples folder> <thumbs folder>
public final class TextThumbs {
    static final int W = 320, H = 200;

    private TextThumbs() {
    }

    public static void main(String[] args) throws Exception {
        Path examples = Path.of(args[0]), thumbs = Path.of(args[1]);
        Files.createDirectories(thumbs);
        List<Path> files;
        try (Stream<Path> s = Files.list(examples)) {
            files = s.filter(f -> f.getFileName().toString().startsWith("text_") && f.toString().endsWith(".glsl")).sorted().toList();
        }
        for (Path file : files) {
            String id = file.getFileName().toString().replace(".glsl", "");
            String source = Files.readString(file, StandardCharsets.UTF_8);
            CountDownLatch done = new CountDownLatch(1);
            AtomicReference<JFrame> frame = new AtomicReference<>();
            AtomicReference<Timer> timer = new AtomicReference<>();
            AtomicReference<String> error = new AtomicReference<>("");
            SwingUtilities.invokeAndWait(() -> {
                JFrame f = new JFrame(id);
                TextCanvas canvas = new TextCanvas(msg -> error.set(msg));
                canvas.setPreferredSize(new java.awt.Dimension(W, H));
                canvas.setSource(source);
                canvas.setText("Shader IDE");
                canvas.freeze(1.5f);
                canvas.shoot(img -> {
                    try {
                        ImageIO.write(img, "png", new File(thumbs.toFile(), id + ".png"));
                    } catch (Exception e) {
                        error.set(e.getMessage());
                    }
                    done.countDown();
                });
                f.add(canvas);
                f.pack();
                f.setVisible(true);
                frame.set(f);
                Timer t = new Timer(40, e -> canvas.render());
                t.start();
                timer.set(t);
            });
            boolean ok = done.await(20, TimeUnit.SECONDS);
            SwingUtilities.invokeAndWait(() -> {
                timer.get().stop();
                frame.get().dispose();
            });
            System.out.println((ok ? "saved " : "FAILED ") + id + (error.get().isBlank() ? "" : " (" + error.get() + ")"));
        }
        System.exit(0);
    }
}
