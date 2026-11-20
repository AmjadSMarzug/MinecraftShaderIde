package me.thesnowbound.shaderIDE.build;

import me.thesnowbound.shaderIDE.util.Json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

// the build settings from build.json in the project folder. every key is optional, and the command line can override them
public record BuildConfig(boolean obfuscate, boolean check, boolean skript, boolean remap) {
    public static final BuildConfig DEFAULT = new BuildConfig(true, true, false, true);
    private static final Set<String> KEYS = Set.of("obfuscate", "check", "skript", "remap");

    // no build.json means the defaults. a broken one stops the build with a message saying what's wrong
    public static BuildConfig read(Path dir) {
        Path file = dir.resolve("build.json");
        if (!Files.isRegularFile(file)) return DEFAULT;
        Map<?, ?> j;
        try {
            Object parsed = Json.parse(Files.readString(file, StandardCharsets.UTF_8));
            if (!(parsed instanceof Map<?, ?> map)) throw new IllegalArgumentException("build.json must hold an object in { }");
            j = map;
        } catch (IOException e) {
            throw new IllegalArgumentException("couldn't read build.json: " + e.getMessage(), e);
        }
        for (Object k : j.keySet()) {
            if (!KEYS.contains(String.valueOf(k))) throw new IllegalArgumentException("unknown setting \"" + k + "\" in build.json");
        }
        return new BuildConfig(
                bool(j, "obfuscate", DEFAULT.obfuscate()),
                bool(j, "check", DEFAULT.check()),
                bool(j, "skript", DEFAULT.skript()),
                bool(j, "remap", DEFAULT.remap()));
    }

    private static boolean bool(Map<?, ?> j, String key, boolean fallback) {
        Object v = j.get(key);
        if (v == null) return fallback;
        if (v instanceof Boolean b) return b;
        throw new IllegalArgumentException("\"" + key + "\" in build.json must be true or false");
    }
}
