package me.thesnowbound.shaderIDE.build;

import me.thesnowbound.shaderIDE.util.Json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// the remap file a build writes next to the pack when it obfuscates. it lists every name that was changed, so a
// shader that was obfuscated can be read again with the original names
public final class Remap {
    private static final Pattern WORD = Pattern.compile("\\b[A-Za-z_][A-Za-z0-9_]*\\b");

    private Remap() {
    }

    // obfuscated name -> original name, for every effect and the wrapper code in one remap file
    @SuppressWarnings("unchecked")
    public static Map<String, String> reverse(Path file) throws IOException {
        Object parsed = Json.parse(Files.readString(file, StandardCharsets.UTF_8));
        if (!(parsed instanceof Map<?, ?> root)) throw new IOException(file + " isn't a remap file");
        Map<String, String> back = new HashMap<>();
        if (root.get("effects") instanceof Map<?, ?> effects) {
            for (Object effect : effects.values()) {
                if (effect instanceof Map<?, ?> e && e.get("names") instanceof Map<?, ?> names) addAll(back, names);
            }
        }
        if (root.get("wrapper") instanceof Map<?, ?> wrapper) addAll(back, wrapper);
        return back;
    }

    private static void addAll(Map<String, String> back, Map<?, ?> names) {
        for (Map.Entry<?, ?> n : names.entrySet()) back.put(String.valueOf(n.getValue()), String.valueOf(n.getKey()));
    }

    // puts the original names back into the text, leaving every other word as it is
    public static String unmap(String text, Map<String, String> back) {
        Matcher m = WORD.matcher(text);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String original = back.get(m.group());
            m.appendReplacement(out, Matcher.quoteReplacement(original == null ? m.group() : original));
        }
        m.appendTail(out);
        return out.toString();
    }
}
