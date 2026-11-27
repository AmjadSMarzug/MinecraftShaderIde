package me.thesnowbound.shaderIDE.presets;

import me.thesnowbound.shaderIDE.util.Json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

// presets you made yourself: right click an effect and choose Convert to Preset. They live in your home folder,
// not in the repo, and show up in the gallery next to the built-in ones
public final class UserPresets {
    static final Path FOLDER = Path.of(System.getProperty("user.home"), ".shader-ide", "presets");
    static final Path INDEX = FOLDER.resolve("presets.json");
    public static final List<String> LEVELS = List.of("Sky", "Object", "Surface", "Material", "Overlay", "Text");

    private UserPresets() {
    }

    // what kind of effect the code is, from the function it defines
    public static String levelOf(String source) {
        if (Pattern.compile("(?m)^\\s*vec4\\s+text\\s*\\(").matcher(source).find()) return "Text";
        if (Pattern.compile("(?m)^\\s*vec4\\s+object\\s*\\(").matcher(source).find()) return "Object";
        if (Pattern.compile("(?m)^\\s*vec4\\s+overlay\\s*\\(").matcher(source).find()) return "Overlay";
        if (Pattern.compile("(?m)^\\s*vec4\\s+surface\\s*\\(").matcher(source).find()) return "Surface";
        if (Pattern.compile("(?m)^\\s*vec4\\s+material\\s*\\(").matcher(source).find()) return "Material";
        return "Sky";
    }

    public static List<Example> load() {
        List<Example> out = new ArrayList<>();
        if (!Files.isRegularFile(INDEX)) return out;
        try {
            for (Object o : (List<?>) Json.parse(Files.readString(INDEX, StandardCharsets.UTF_8))) {
                Map<?, ?> m = (Map<?, ?>) o;
                out.add(new Example(String.valueOf(m.get("id")), String.valueOf(m.get("name")),
                        String.valueOf(m.get("level")), String.valueOf(m.get("blurb"))));
            }
        } catch (IOException | RuntimeException e) {
            // a broken index hides your presets, the built-in ones still show
        }
        return out;
    }

    public static boolean has(String id) {
        return load().stream().anyMatch(e -> e.id().equals(id));
    }

    public static String source(String id) throws IOException {
        return Files.readString(FOLDER.resolve(id + ".glsl"), StandardCharsets.UTF_8);
    }

    public static Path thumb(String id) {
        return FOLDER.resolve(id + ".png");
    }

    // saves an effect's code as a preset. thumbnail may be null
    public static Example save(String id, String name, String level, String blurb, String source, java.awt.image.BufferedImage thumb) throws IOException {
        Files.createDirectories(FOLDER);
        Files.writeString(FOLDER.resolve(id + ".glsl"), source, StandardCharsets.UTF_8);
        if (thumb != null) javax.imageio.ImageIO.write(thumb, "png", thumb(id).toFile());
        List<Example> all = new ArrayList<>(load());
        all.removeIf(e -> e.id().equals(id));
        Example made = new Example(id, name, level, blurb);
        all.add(made);
        List<Object> rows = new ArrayList<>();
        for (Example e : all) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.id());
            m.put("name", e.name());
            m.put("level", e.level());
            m.put("blurb", e.blurb());
            rows.add(m);
        }
        Files.writeString(INDEX, Json.write(rows) + "\n", StandardCharsets.UTF_8);
        return made;
    }
}
