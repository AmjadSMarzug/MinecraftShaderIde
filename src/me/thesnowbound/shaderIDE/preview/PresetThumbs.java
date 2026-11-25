package me.thesnowbound.shaderIDE.preview;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Dimension;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

// draws a sky, object, or overlay preset in the preview and saves a picture for the gallery
// usage: PresetThumbs <examples folder> <thumbs folder> <preset id>...
public final class PresetThumbs {
    static final int W = 320, H = 200;

    private PresetThumbs() {
    }

    public static void main(String[] args) throws Exception {
        Path examples = Path.of(args[0]), thumbs = Path.of(args[1]);
        Files.createDirectories(thumbs);
        for (String id : List.of(args).subList(2, args.length)) {
            String source = Files.readString(examples.resolve(id + ".glsl"), StandardCharsets.UTF_8);
            CountDownLatch done = new CountDownLatch(1);
            AtomicReference<JFrame> frame = new AtomicReference<>();
            AtomicReference<Timer> timer = new AtomicReference<>();
            AtomicReference<String> problem = new AtomicReference<>("");
            SwingUtilities.invokeAndWait(() -> {
                JFrame f = new JFrame(id);
                Preview preview = new Preview(list -> {
                    if (!list.isEmpty()) problem.set(list.get(0).message());
                });
                preview.setPreferredSize(new Dimension(W, H));
                f.add(preview);
                f.pack();
                f.setVisible(true);
                preview.show(source, Collections.emptyMap());
                preview.shoot(img -> {
                    try {
                        ImageIO.write(img, "png", new File(thumbs.toFile(), id + ".png"));
                    } catch (Exception e) {
                        problem.set(e.getMessage());
                    }
                    done.countDown();
                });
                frame.set(f);
                Timer t = new Timer(40, e -> preview.render());
                t.start();
                timer.set(t);
            });
            boolean ok = done.await(30, TimeUnit.SECONDS);
            SwingUtilities.invokeAndWait(() -> {
                timer.get().stop();
                frame.get().dispose();
            });
            System.out.println((ok ? "saved " : "FAILED ") + id + (problem.get().isBlank() ? "" : " (" + problem.get() + ")"));
        }
        System.exit(0);
    }
}
