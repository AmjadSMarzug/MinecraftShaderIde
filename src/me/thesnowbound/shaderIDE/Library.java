package me.thesnowbound.shaderIDE;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// built-in shader functions an effect can pull in with a line like "#import noise".
// each module's code replaces its #import line, joined onto that one line, so the line numbers in the
// editor and in any error stay the same. the code here has no // comments, since they would run past
// the end of the joined line. put the imports at the top of the effect, before the functions that use them
final class Library {
    record Module(String[] needs, String code) {
    }

    static final Map<String, Module> MODULES = new LinkedHashMap<>();

    static {
        MODULES.put("hash", new Module(new String[0],
                "float vHash1(float p) { return fract(sin(p * 127.1) * 43758.5453); }\n"
                + "float vHash2(vec2 p) { return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453); }\n"
                + "float vHash3(vec3 p) { return fract(sin(dot(p, vec3(12.9898, 78.233, 37.719))) * 43758.5453); }\n"
                + "vec2 vHash22(vec2 p) { return vec2(vHash2(p), vHash2(p + vec2(19.19, 47.77))); }\n"));

        MODULES.put("noise", new Module(new String[] {"hash"},
                "float vNoise(vec2 p) {\n"
                + "    vec2 i = floor(p);\n"
                + "    vec2 f = fract(p);\n"
                + "    f = f * f * (3.0 - 2.0 * f);\n"
                + "    return mix(mix(vHash2(i), vHash2(i + vec2(1.0, 0.0)), f.x), mix(vHash2(i + vec2(0.0, 1.0)), vHash2(i + vec2(1.0, 1.0)), f.x), f.y);\n"
                + "}\n"
                + "float vNoise3(vec3 p) {\n"
                + "    vec3 i = floor(p);\n"
                + "    vec3 f = fract(p);\n"
                + "    f = f * f * (3.0 - 2.0 * f);\n"
                + "    float a = mix(mix(vHash3(i), vHash3(i + vec3(1.0, 0.0, 0.0)), f.x), mix(vHash3(i + vec3(0.0, 1.0, 0.0)), vHash3(i + vec3(1.0, 1.0, 0.0)), f.x), f.y);\n"
                + "    float b = mix(mix(vHash3(i + vec3(0.0, 0.0, 1.0)), vHash3(i + vec3(1.0, 0.0, 1.0)), f.x), mix(vHash3(i + vec3(0.0, 1.0, 1.0)), vHash3(i + vec3(1.0, 1.0, 1.0)), f.x), f.y);\n"
                + "    return mix(a, b, f.z);\n"
                + "}\n"));

        MODULES.put("fbm", new Module(new String[] {"noise"},
                "float vFbm(vec2 p) {\n"
                + "    float sum = 0.0;\n"
                + "    float amp = 0.5;\n"
                + "    for (int i = 0; i < 5; i++) {\n"
                + "        sum += amp * vNoise(p);\n"
                + "        p = p * 2.03 + vec2(11.7, 3.1);\n"
                + "        amp *= 0.5;\n"
                + "    }\n"
                + "    return sum;\n"
                + "}\n"
                + "float vFbm3(vec3 p) {\n"
                + "    float sum = 0.0;\n"
                + "    float amp = 0.5;\n"
                + "    for (int i = 0; i < 4; i++) {\n"
                + "        sum += amp * vNoise3(p);\n"
                + "        p = p * 2.03 + vec3(11.7, 3.1, 7.9);\n"
                + "        amp *= 0.5;\n"
                + "    }\n"
                + "    return sum;\n"
                + "}\n"));

        MODULES.put("voronoi", new Module(new String[] {"hash"},
                "float vVoronoi(vec2 p) {\n"
                + "    vec2 i = floor(p);\n"
                + "    vec2 f = fract(p);\n"
                + "    float d = 8.0;\n"
                + "    for (int y = -1; y <= 1; y++) {\n"
                + "        for (int x = -1; x <= 1; x++) {\n"
                + "            vec2 g = vec2(float(x), float(y));\n"
                + "            d = min(d, length(g + vHash22(i + g) - f));\n"
                + "        }\n"
                + "    }\n"
                + "    return d;\n"
                + "}\n"
                + "float vVoronoiEdge(vec2 p) {\n"
                + "    vec2 i = floor(p);\n"
                + "    vec2 f = fract(p);\n"
                + "    float d1 = 8.0;\n"
                + "    float d2 = 8.0;\n"
                + "    for (int y = -1; y <= 1; y++) {\n"
                + "        for (int x = -1; x <= 1; x++) {\n"
                + "            vec2 g = vec2(float(x), float(y));\n"
                + "            float d = length(g + vHash22(i + g) - f);\n"
                + "            if (d < d1) { d2 = d1; d1 = d; } else if (d < d2) { d2 = d; }\n"
                + "        }\n"
                + "    }\n"
                + "    return d2 - d1;\n"
                + "}\n"));

        MODULES.put("rotate", new Module(new String[0],
                "vec2 vRotate(vec2 p, float a) {\n"
                + "    float c = cos(a);\n"
                + "    float s = sin(a);\n"
                + "    return vec2(c * p.x - s * p.y, s * p.x + c * p.y);\n"
                + "}\n"));

        MODULES.put("palette", new Module(new String[0],
                "vec3 vPalette(float t) {\n"
                + "    return vec3(0.5) + vec3(0.5) * cos(6.28318 * (vec3(1.0) * t + vec3(0.0, 0.33, 0.67)));\n"
                + "}\n"));

        MODULES.put("sdf", new Module(new String[0],
                "float vSdSphere(vec3 p, float r) { return length(p) - r; }\n"
                + "float vSdBox(vec3 p, vec3 b) { vec3 q = abs(p) - b; return length(max(q, 0.0)) + min(max(q.x, max(q.y, q.z)), 0.0); }\n"
                + "float vSdTorus(vec3 p, vec2 t) { vec2 q = vec2(length(p.xz) - t.x, p.y); return length(q) - t.y; }\n"));
    }

    private static final Pattern IMPORT = Pattern.compile("^\\s*#import\\b(.*)$");
    private static final Pattern CALL = Pattern.compile("\\b(v[A-Z]\\w*)\\s*\\(");
    private static final Pattern DEFINE = Pattern.compile("\\b(?:float|vec[234]|int|void|bool)\\s+(v[A-Z]\\w*)\\s*\\(");

    // which module each library function lives in, found from the code itself
    private static final Map<String, String> OWNER = new LinkedHashMap<>();

    static {
        for (Map.Entry<String, Module> e : MODULES.entrySet()) {
            Matcher m = DEFINE.matcher(e.getValue().code());
            while (m.find()) OWNER.put(m.group(1), e.getKey());
        }
    }

    // swaps every #import line for the code it names, then adds any module whose functions are called
    // without an import. the line count never changes: auto imports go in front of the first code line
    static String expand(String source) {
        String[] lines = source.split("\n", -1);
        Set<String> loaded = new LinkedHashSet<>();
        for (int i = 0; i < lines.length; i++) {
            Matcher m = IMPORT.matcher(lines[i]);
            if (!m.matches()) continue;
            StringBuilder code = new StringBuilder();
            StringBuilder unknown = new StringBuilder();
            for (String name : m.group(1).trim().split("[\\s,]+")) {
                if (name.isEmpty()) continue;
                if (MODULES.containsKey(name)) collect(name, loaded, code);
                else unknown.append(unknown.length() == 0 ? "" : " ").append(name);
            }
            // a #error line shows up as a problem on the import's own line, so a typo is easy to spot
            lines[i] = unknown.length() > 0 ? "#error unknown import: " + unknown : code.toString().trim();
        }

        // functions the effect defines itself, and modules that define any of them, are left alone
        Set<String> own = new LinkedHashSet<>();
        Matcher d = DEFINE.matcher(source);
        while (d.find()) own.add(d.group(1));
        Set<String> skip = new LinkedHashSet<>();
        for (String fn : own) if (OWNER.containsKey(fn)) skip.add(OWNER.get(fn));

        StringBuilder auto = new StringBuilder();
        Matcher c = CALL.matcher(String.join("\n", lines));
        while (c.find()) {
            String fn = c.group(1);
            String mod = OWNER.get(fn);
            if (mod == null || own.contains(fn) || skip.contains(mod)) continue;
            collect(mod, loaded, auto, skip);
        }
        if (auto.length() > 0) {
            int target = lines.length - 1;
            for (int i = 0; i < lines.length; i++) {
                if (!lines[i].trim().startsWith("#")) {
                    target = i;
                    break;
                }
            }
            lines[target] = auto.toString() + lines[target];
        }
        return String.join("\n", lines);
    }

    private static void collect(String name, Set<String> loaded, StringBuilder out) {
        collect(name, loaded, out, Set.of());
    }

    private static void collect(String name, Set<String> loaded, StringBuilder out, Set<String> skip) {
        if (!loaded.add(name) || skip.contains(name)) return;
        Module m = MODULES.get(name);
        for (String need : m.needs()) collect(need, loaded, out, skip);
        out.append(m.code().replace('\n', ' ')).append(' ');
    }

    static java.util.Set<String> functions() {
        return OWNER.keySet();
    }

    static String names() {
        return String.join(", ", MODULES.keySet());
    }
}
