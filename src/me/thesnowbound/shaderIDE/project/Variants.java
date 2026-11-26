package me.thesnowbound.shaderIDE.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// bulk effects. a file called NAME.variants.glsl in the effects folder is one shader written once, and it becomes
// one effect for every combination of its //@each lines. the header lines say what changes, and the body uses the
// placeholders: {axis} is the value, {axis.name} is its label. For example:
//
//   //@each colour: red = vec3(1.0, 0.2, 0.2); blue = vec3(0.2, 0.4, 1.0)
//   //@name sky_{colour.name}
//   vec3 sky(vec3 dir, float time) { return {colour} * (0.5 + 0.5 * dir.y); }
//
// makes sky_red and sky_blue. Their code goes in build/variants and is not saved to project.json.
public final class Variants {
    public static final String FOLDER = "build/variants/";
    static final String SUFFIX = ".variants.glsl";
    static final Pattern EACH = Pattern.compile("^//@each\\s+(\\w+)\\s*:\\s*(.+?)\\s*$");
    static final Pattern NAME = Pattern.compile("^//@name\\s+(\\S.*?)\\s*$");
    static final Pattern MODEL = Pattern.compile("^//@model\\s+(\\w+)\\s*$");
    static final Pattern OBFUSCATION = Pattern.compile("^//@obfuscation\\s+(\\w+)\\s*$");

    record Item(String label, String value) {
    }

    record Axis(String name, List<Item> items) {
    }

    public record Generated(String id, String source, String model, String obfuscation) {
    }

    private Variants() {
    }

    public static boolean isVariant(Project.Effect e) {
        return e.file().startsWith(FOLDER);
    }

    // adds every variant in the project's effects folder to the project's effects
    public static void addTo(Project project) throws IOException {
        Path effects = project.dir.resolve("effects");
        if (!Files.isDirectory(effects)) return;
        List<Path> templates;
        try (Stream<Path> s = Files.list(effects)) {
            templates = s.filter(f -> f.getFileName().toString().endsWith(SUFFIX)).sorted().toList();
        }
        for (Path template : templates) {
            String base = template.getFileName().toString().replace(SUFFIX, "");
            for (Generated g : expand(Files.readString(template), base)) {
                if (project.effects.stream().anyMatch(e -> e.id().equals(g.id()))) {
                    throw new IllegalArgumentException(template.getFileName() + " makes " + g.id() + ", which another effect already uses");
                }
                Path out = project.dir.resolve(FOLDER + g.id() + ".glsl");
                Files.createDirectories(out.getParent());
                Files.writeString(out, g.source());
                project.effects.add(new Project.Effect(g.id(), FOLDER + g.id() + ".glsl", project.freeAlpha(), g.id(),
                        new LinkedHashMap<>(), g.model(), Obfuscation.parse(g.obfuscation(), g.id())));
            }
        }
    }

    // the effects one template makes, one per combination of its axes
    public static List<Generated> expand(String text, String base) {
        List<Axis> axes = new ArrayList<>();
        String name = null, model = "cylinder", obfuscation = null;
        StringBuilder body = new StringBuilder();
        for (String line : text.split("\n", -1)) {
            Matcher m;
            if ((m = EACH.matcher(line)).matches()) axes.add(axis(m.group(1), m.group(2)));
            else if ((m = NAME.matcher(line)).matches()) name = m.group(1);
            else if ((m = MODEL.matcher(line)).matches()) model = m.group(1);
            else if ((m = OBFUSCATION.matcher(line)).matches()) obfuscation = m.group(1);
            else body.append(line).append('\n');
        }
        List<Map<String, Item>> combos = new ArrayList<>();
        combos.add(new LinkedHashMap<>());
        for (Axis axis : axes) {
            List<Map<String, Item>> next = new ArrayList<>();
            for (Map<String, Item> combo : combos) {
                for (Item item : axis.items()) {
                    Map<String, Item> grown = new LinkedHashMap<>(combo);
                    grown.put(axis.name(), item);
                    next.add(grown);
                }
            }
            combos = next;
        }
        List<Generated> out = new ArrayList<>();
        java.util.Set<String> ids = new java.util.HashSet<>();
        for (Map<String, Item> combo : combos) {
            String id = name != null ? fill(name, combo) : base + combo.values().stream().map(i -> "_" + i.label()).reduce("", String::concat);
            id = id.replaceAll("\\W", "_");
            if (!ids.add(id)) throw new IllegalArgumentException(base + " makes " + id + " more than once");
            out.add(new Generated(id, fill(body.toString(), combo), model, obfuscation));
        }
        return out;
    }

    static Axis axis(String name, String items) {
        List<Item> list = new ArrayList<>();
        for (String part : items.split(";")) {
            String p = part.strip();
            if (p.isEmpty()) continue;
            int eq = p.indexOf('=');
            if (eq < 0) throw new IllegalArgumentException("@each " + name + ": '" + p + "' needs label = value");
            list.add(new Item(p.substring(0, eq).strip(), p.substring(eq + 1).strip()));
        }
        if (list.isEmpty()) throw new IllegalArgumentException("@each " + name + " has no values");
        return new Axis(name, list);
    }

    static String fill(String text, Map<String, Item> combo) {
        String out = text;
        for (Map.Entry<String, Item> e : combo.entrySet()) {
            out = out.replace("{" + e.getKey() + ".name}", e.getValue().label());
            out = out.replace("{" + e.getKey() + "}", e.getValue().value());
        }
        return out;
    }
}
