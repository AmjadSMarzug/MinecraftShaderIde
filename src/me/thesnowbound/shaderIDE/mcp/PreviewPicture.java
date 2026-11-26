package me.thesnowbound.shaderIDE.mcp;

import me.thesnowbound.shaderIDE.preview.Preview;
import me.thesnowbound.shaderIDE.preview.TextCanvas;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

// draws one effect in a small window and returns the picture. needs a display
final class PreviewPicture {
    static final int W = 480, H = 300;

    private PreviewPicture() {
    }

    static BufferedImage render(String source, String kind) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        AtomicReference<BufferedImage> picture = new AtomicReference<>();
        AtomicReference<String> problem = new AtomicReference<>("");
        AtomicReference<JFrame> frame = new AtomicReference<>();
        AtomicReference<Timer> timer = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            JFrame f = new JFrame("preview");
            Component view;
            Runnable pump;
            if (kind.equals("text")) {
                TextCanvas canvas = new TextCanvas(msg -> problem.set(msg));
                canvas.setPreferredSize(new Dimension(W, H));
                canvas.setSource(source);
                canvas.setText("Shader IDE");
                canvas.freeze(1.5f);
                canvas.shoot(img -> {
                    picture.set(img);
                    done.countDown();
                });
                view = canvas;
                pump = canvas::render;
            } else {
                Preview preview = new Preview(list -> {
                    if (!list.isEmpty()) problem.set(list.get(0).message());
                });
                preview.setPreferredSize(new Dimension(W, H));
                preview.show(source, Collections.emptyMap());
                preview.shoot(img -> {
                    picture.set(img);
                    done.countDown();
                });
                view = preview;
                pump = preview::render;
            }
            f.add(view);
            f.pack();
            f.setVisible(true);
            frame.set(f);
            Timer t = new Timer(40, e -> pump.run());
            t.start();
            timer.set(t);
        });
        boolean ok = done.await(30, TimeUnit.SECONDS);
        SwingUtilities.invokeAndWait(() -> {
            timer.get().stop();
            frame.get().dispose();
        });
        if (!ok || picture.get() == null) {
            throw new IllegalStateException("no picture was drawn" + (problem.get().isBlank() ? "" : ": " + problem.get()));
        }
        return picture.get();
    }
}
