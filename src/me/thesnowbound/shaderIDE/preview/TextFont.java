package me.thesnowbound.shaderIDE.preview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// the 16 by 16 grid of 8 by 8 letters the chat draws from. The game's own font is read from a Minecraft jar the user
// points to, since Mojang's font can't be shipped here. Without a jar, letters are drawn from a bundled font and cut
// down to 8 by 8 pixels, which gives the same blocky look.
public final class TextFont {
    static final String ASCII = "assets/minecraft/textures/font/ascii.png";
    static final int SIZE = 128;

    private TextFont() {
    }

    // looks for an installed game in the usual .minecraft folders, and returns the first version jar that has the font.
    // the font is read from the player's own game files, the IDE never ships Mojang's font
    public static Path findGameJar() {
        List<Path> roots = new ArrayList<>();
        String home = System.getProperty("user.home");
        String appData = System.getenv("APPDATA");
        roots.add(Path.of(home, ".minecraft", "versions"));
        if (appData != null) roots.add(Path.of(appData, ".minecraft", "versions"));
        roots.add(Path.of(home, "Library", "Application Support", "minecraft", "versions"));
        for (Path root : roots) {
            if (!Files.isDirectory(root)) continue;
            try (Stream<Path> dirs = Files.list(root)) {
                List<Path> jars = dirs.map(d -> d.resolve(d.getFileName() + ".jar")).filter(Files::isRegularFile)
                        .sorted(Comparator.reverseOrder()).toList();
                for (Path jar : jars) {
                    if (fromJarQuietly(jar) != null) return jar;
                }
            } catch (IOException e) {
                // an unreadable folder is skipped, the next one is tried
            }
        }
        return null;
    }

    private static BufferedImage fromJarQuietly(Path jar) {
        try {
            return fromJar(jar);
        } catch (IOException e) {
            return null;
        }
    }

    // the game's own ascii.png from a Minecraft jar, or null if the jar doesn't have it
    public static BufferedImage fromJar(Path jar) throws IOException {
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            ZipEntry entry = zip.getEntry(ASCII);
            if (entry == null) return null;
            try (InputStream in = zip.getInputStream(entry)) {
                BufferedImage img = javax.imageio.ImageIO.read(in);
                if (img == null || img.getWidth() != SIZE || img.getHeight() != SIZE) return null;
                return img;
            }
        }
    }

    // the font that ships with the IDE, or a plain monospaced one if it can't be read
    static Font bundled() {
        try (InputStream in = TextFont.class.getResourceAsStream("/fonts/JetBrainsMono-Bold.ttf")) {
            if (in != null) return Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(Font.BOLD, 10f);
        } catch (Exception e) {
            // fall through to the monospaced font
        }
        return new Font(Font.MONOSPACED, Font.BOLD, 9);
    }

    // a bundled font drawn into the same 128 by 128 grid, each letter 8 pixels, and any pixel with ink becomes solid
    public static BufferedImage generated() {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int code = 0; code < 256; code++) {
            BufferedImage cell = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = cell.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g.setColor(Color.WHITE);
            g.setFont(bundled());
            String ch = String.valueOf((char) code);
            int w = g.getFontMetrics().stringWidth(ch);
            g.drawString(ch, Math.max(0, (8 - w) / 2), 8);
            g.dispose();
            int ox = (code % 16) * 8, oy = (code / 16) * 8;
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    int a = (cell.getRGB(x, y) >>> 24) & 255;
                    img.setRGB(ox + x, oy + y, (a > 60 ? 0xff : 0) << 24 | 0xffffff);
                }
            }
        }
        return img;
    }
}
