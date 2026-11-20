package me.thesnowbound.shaderIDE.project;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// how one sky shader is scrambled in a build. "lite" renames and strips, "max" also hides the numbers,
// adds decoy code and scrambles the wrapper. any of the parts can be changed on top of a style
public record Obfuscation(String style, boolean rename, boolean strip, boolean numbers, int decoys, boolean wrapper) {
    public static final List<String> STYLES = List.of("lite", "max");
    static final List<String> KEYS = List.of("style", "rename", "strip", "numbers", "decoys", "wrapper");
    static final int MAX_DECOYS = 20;

    public Obfuscation {
        if (!STYLES.contains(style) && !style.equals("readable")) {
            throw new IllegalArgumentException("obfuscation style must be lite or max, not \"" + style + "\"");
        }
        if (decoys < 0 || decoys > MAX_DECOYS) throw new IllegalArgumentException("decoys must be 0 to " + MAX_DECOYS + ", not " + decoys);
    }

    public static Obfuscation lite() {
        return new Obfuscation("lite", true, true, false, 0, false);
    }

    public static Obfuscation max() {
        return new Obfuscation("max", true, true, true, 4, true);
    }

    // nothing scrambled: the shader's own names and layout are kept
    public static Obfuscation readable() {
        return new Obfuscation("readable", false, false, false, 0, false);
    }

    // project.json accepts "lite", "max", or an object that starts from a style and changes any of its parts
    public static Obfuscation parse(Object raw, String effect) {
        if (raw == null) return lite();
        if (raw instanceof String s) return preset(s, effect);
        if (!(raw instanceof Map<?, ?> m)) throw new IllegalArgumentException("effect " + effect + ": obfuscation must be a style or an object");
        for (Object k : m.keySet()) {
            if (!KEYS.contains(String.valueOf(k))) throw new IllegalArgumentException("effect " + effect + ": unknown obfuscation setting \"" + k + "\"");
        }
        Obfuscation base = preset(m.containsKey("style") ? String.valueOf(m.get("style")) : "lite", effect);
        return new Obfuscation(base.style(),
                bool(m, "rename", base.rename(), effect), bool(m, "strip", base.strip(), effect),
                bool(m, "numbers", base.numbers(), effect), decoys(m, base.decoys(), effect),
                bool(m, "wrapper", base.wrapper(), effect));
    }

    private static Obfuscation preset(String style, String effect) {
        return switch (style) {
            case "lite" -> lite();
            case "max" -> max();
            default -> throw new IllegalArgumentException("effect " + effect + ": obfuscation style must be lite or max, not \"" + style + "\"");
        };
    }

    private static boolean bool(Map<?, ?> m, String key, boolean fallback, String effect) {
        Object v = m.get(key);
        if (v == null) return fallback;
        if (v instanceof Boolean b) return b;
        throw new IllegalArgumentException("effect " + effect + ": \"" + key + "\" must be true or false");
    }

    private static int decoys(Map<?, ?> m, int fallback, String effect) {
        Object v = m.get("decoys");
        if (v == null) return fallback;
        if (v instanceof Number n && n.doubleValue() == Math.rint(n.doubleValue())) return n.intValue();
        throw new IllegalArgumentException("effect " + effect + ": \"decoys\" must be a whole number");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("style", style);
        m.put("rename", rename);
        m.put("strip", strip);
        m.put("numbers", numbers);
        m.put("decoys", decoys);
        m.put("wrapper", wrapper);
        return m;
    }
}
