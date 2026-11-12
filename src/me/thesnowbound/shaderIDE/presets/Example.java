package me.thesnowbound.shaderIDE.presets;

import me.thesnowbound.shaderIDE.ui.Gallery;

import static me.thesnowbound.shaderIDE.presets.Templates.*;

public record Example(String id, String name, String level, String blurb) {
    java.awt.image.BufferedImage thumb() {
        try (var in = Templates.class.getResourceAsStream("/examples/thumbs/" + id + ".png")) {
            return in == null ? null : javax.imageio.ImageIO.read(in);
        } catch (Exception e) {
            return null;
        }
    }

    public Gallery.Choice choice() {
        return new Gallery.Choice(id, name, level, blurb, source(), thumb());
    }

    public String source() {
        try (var in = Templates.class.getResourceAsStream("/examples/" + id + ".glsl")) {
            return new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return NEW_EFFECT;
        }
    }
}
