package me.thesnowbound.shaderIDE.mcp;

import me.thesnowbound.shaderIDE.build.BuildConfig;
import me.thesnowbound.shaderIDE.build.Forge;
import me.thesnowbound.shaderIDE.library.Library;
import me.thesnowbound.shaderIDE.project.Project;
import me.thesnowbound.shaderIDE.util.Json;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

// an MCP server over stdin and stdout, so AI tools (Claude Code, Codex, and other MCP clients) can read and edit
// a project, check it, build it, and look at a preview. start it with: shader-ide --mcp
public final class McpServer {
    static final String PROTOCOL = "2024-11-05";
    static final String INSTRUCTIONS = "Shader IDE projects: each effect is a .glsl file in the project. "
            + "A sky is vec3 sky(vec3 dir, float time). An object is vec4 object(vec3 origin, vec3 dir, float time). "
            + "A text effect is vec4 text(vec2 p, vec2 size, float time). Use check_effect after edits, preview_effect to see it, "
            + "and build_pack to make the resource pack.";

    private static PrintStream out;

    private McpServer() {
    }

    public static void run() throws Exception {
        // the protocol uses stdout, so anything else the IDE prints goes to stderr instead
        out = new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
        System.setOut(System.err);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) continue;
            Map<?, ?> request;
            try {
                request = (Map<?, ?>) Json.parse(line);
            } catch (RuntimeException e) {
                continue;
            }
            Object id = request.get("id");
            if (id == null) continue; // notifications need no answer
            String method = String.valueOf(request.get("method"));
            Map<?, ?> params = request.get("params") instanceof Map<?, ?> p ? p : Map.of();
            try {
                send(reply(id, handle(method, params)));
            } catch (Exception e) {
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("code", -32000);
                error.put("message", String.valueOf(e.getMessage() == null ? e : e.getMessage()));
                Map<String, Object> failed = new LinkedHashMap<>();
                failed.put("jsonrpc", "2.0");
                failed.put("id", id);
                failed.put("error", error);
                send(failed);
            }
        }
    }

    private static Map<String, Object> reply(Object id, Map<String, Object> result) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("jsonrpc", "2.0");
        m.put("id", id);
        m.put("result", result);
        return m;
    }

    private static void send(Map<String, Object> message) {
        out.println(Json.write(message));
    }

    private static Map<String, Object> handle(String method, Map<?, ?> params) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();
        switch (method) {
            case "initialize" -> {
                result.put("protocolVersion", PROTOCOL);
                result.put("capabilities", Map.of("tools", Map.of()));
                result.put("serverInfo", Map.of("name", "shader-ide", "version", Forge.VERSION));
                result.put("instructions", INSTRUCTIONS);
            }
            case "ping" -> {
            }
            case "tools/list" -> result.put("tools", Tools.list());
            case "tools/call" -> {
                String name = String.valueOf(params.get("name"));
                Map<?, ?> args = params.get("arguments") instanceof Map<?, ?> a ? a : Map.of();
                try {
                    List<Map<String, Object>> content = Tools.call(name, args);
                    result.put("content", content);
                } catch (Exception e) {
                    result.put("content", List.of(text(String.valueOf(e.getMessage() == null ? e : e.getMessage()))));
                    result.put("isError", true);
                }
            }
            default -> throw new IllegalArgumentException("unknown method " + method);
        }
        return result;
    }

    static Map<String, Object> text(String s) {
        return Map.of("type", "text", "text", s);
    }

    // the tools an AI client can call
    static final class Tools {
        static List<Map<String, Object>> list() {
            List<Map<String, Object>> tools = new ArrayList<>();
            tools.add(tool("list_effects", "List the effects in a project, with each one's kind, item model and obfuscation style.",
                    schema(Map.of("project", "string"), "project")));
            tools.add(tool("read_effect", "Read an effect's GLSL source.", schema(Map.of("project", "string", "effect", "string"), "project", "effect")));
            tools.add(tool("write_effect", "Replace an effect's GLSL source, then check it. Returns the compiler's messages.",
                    schema(Map.of("project", "string", "effect", "string", "source", "string"), "project", "effect", "source")));
            tools.add(tool("check_effect", "Compile an effect with glslang the way the build does, and report errors.",
                    schema(Map.of("project", "string", "effect", "string"), "project", "effect")));
            tools.add(tool("build_pack", "Build the project into a resource pack zip, using the project's build.json settings.",
                    schema(Map.of("project", "string"), "project")));
            tools.add(tool("preview_effect", "Render an effect and return a picture of it. Needs a display.",
                    schema(Map.of("project", "string", "effect", "string"), "project", "effect")));
            return tools;
        }

        static Map<String, Object> tool(String name, String description, Map<String, Object> schema) {
            Map<String, Object> t = new LinkedHashMap<>();
            t.put("name", name);
            t.put("description", description);
            t.put("inputSchema", schema);
            return t;
        }

        static Map<String, Object> schema(Map<String, String> props, String... required) {
            Map<String, Object> p = new LinkedHashMap<>();
            for (Map.Entry<String, String> e : props.entrySet()) p.put(e.getKey(), Map.of("type", e.getValue()));
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("type", "object");
            s.put("properties", p);
            s.put("required", List.of(required));
            return s;
        }

        static List<Map<String, Object>> call(String name, Map<?, ?> args) throws Exception {
            Path dir = Path.of(String.valueOf(args.get("project"))).toAbsolutePath().normalize();
            switch (name) {
                case "list_effects": {
                    Project p = Project.load(dir);
                    List<Object> rows = new ArrayList<>();
                    for (Project.Effect e : p.effects) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("id", e.id());
                        row.put("file", e.file());
                        row.put("kind", kind(Files.readString(dir.resolve(e.file()))));
                        row.put("model", e.model());
                        row.put("obfuscation", e.obfuscation().style());
                        rows.add(row);
                    }
                    return List.of(text(Json.write(rows)));
                }
                case "read_effect": {
                    return List.of(text(Files.readString(effect(dir, args).file)));
                }
                case "write_effect": {
                    Found f = effect(dir, args);
                    Files.writeString(f.file, String.valueOf(args.get("source")));
                    String problems = check(dir, f);
                    return List.of(text(problems.isEmpty() ? "saved " + f.id + ", it compiles" : "saved " + f.id + ", but:\n" + problems));
                }
                case "check_effect": {
                    Found f = effect(dir, args);
                    String problems = check(dir, f);
                    return List.of(text(problems.isEmpty() ? f.id + " compiles" : problems));
                }
                case "build_pack": {
                    Project p = Project.load(dir);
                    BuildConfig config = BuildConfig.read(dir);
                    StringBuilder log = new StringBuilder();
                    Path zip = new Forge(p, msg -> log.append(msg).append('\n'), config).build(config.obfuscate(), config.check());
                    return List.of(text(log + "built " + zip));
                }
                case "preview_effect": {
                    Found f = effect(dir, args);
                    BufferedImage picture = PreviewPicture.render(Files.readString(f.file), kind(Files.readString(f.file)));
                    ByteArrayOutputStream png = new ByteArrayOutputStream();
                    ImageIO.write(picture, "png", png);
                    Map<String, Object> image = new LinkedHashMap<>();
                    image.put("type", "image");
                    image.put("data", Base64.getEncoder().encodeToString(png.toByteArray()));
                    image.put("mimeType", "image/png");
                    return List.of(image);
                }
                default:
                    throw new IllegalArgumentException("unknown tool " + name);
            }
        }

        record Found(String id, Path file, Project.Effect effect) {
        }

        static Found effect(Path dir, Map<?, ?> args) throws Exception {
            Project p = Project.load(dir);
            String id = String.valueOf(args.get("effect"));
            for (Project.Effect e : p.effects) {
                if (e.id().equals(id)) return new Found(id, dir.resolve(e.file()), e);
            }
            throw new IllegalArgumentException("no effect called " + id + " in " + dir);
        }

        static final Pattern TEXT = Pattern.compile("(?m)^\\s*vec4\\s+text\\s*\\(");
        static final Pattern OBJECT = Pattern.compile("(?m)^\\s*vec4\\s+object\\s*\\(");
        static final Pattern OVERLAY = Pattern.compile("(?m)^\\s*vec4\\s+overlay\\s*\\(");
        static final Pattern SURFACE = Pattern.compile("(?m)^\\s*vec4\\s+surface\\s*\\(");
        static final Pattern MATERIAL = Pattern.compile("(?m)^\\s*vec4\\s+material\\s*\\(");
        static final Pattern SKY = Pattern.compile("(?m)^\\s*vec3\\s+sky\\s*\\(");

        static String kind(String src) {
            if (TEXT.matcher(src).find()) return "text";
            if (OBJECT.matcher(src).find()) return "object";
            if (OVERLAY.matcher(src).find()) return "overlay";
            if (SURFACE.matcher(src).find()) return "surface";
            if (MATERIAL.matcher(src).find()) return "material";
            if (SKY.matcher(src).find()) return "sky";
            return "unknown";
        }

        static final Map<String, String> CALLS = Map.of(
                "sky", "fragColor = vec4(sky(vec3(0.3, 0.4, 1.0), 1.0), 1.0);",
                "object", "fragColor = object(vec3(0.0), vec3(0.0, 0.0, 1.0), 1.0);",
                "overlay", "fragColor = overlay(vec2(0.5), vec3(0.0, 0.0, 1.0), 1.0);",
                "surface", "fragColor = surface(vec2(0.5), vec3(0.0), vec3(0.0, 0.0, 1.0), vec3(0.0, 0.0, 1.0), 1.0);",
                "material", "fragColor = material(vec4(1.0), vec2(0.5), vec3(0.0, 0.0, 1.0), vec3(0.0, 0.0, 1.0), 1.0);",
                "text", "fragColor = text(vec2(1.0), vec2(8.0), 1.0);");

        // the same check the build runs: the effect's own #defines and #imports, then glslang on a small main
        static String check(Path dir, Found f) throws Exception {
            String src = Files.readString(f.file);
            String kind = kind(src);
            if (kind.equals("unknown")) {
                return "no entry function found. A sky needs vec3 sky(vec3 dir, float time), an object needs vec4 object(vec3 origin, vec3 dir, float time), "
                        + "a text effect needs vec4 text(vec2 p, vec2 size, float time)";
            }
            StringBuilder head = new StringBuilder("#version 330\n");
            f.effect.defines().forEach((k, v) -> head.append("#define ").append(k).append(' ').append(v).append('\n'));
            String wrapped = head + Library.expand(src) + "\nout vec4 fragColor;\nvoid main() { " + CALLS.get(kind) + " }\n";
            Path tmp = Files.createTempFile("shader-ide-check", ".frag");
            try {
                Files.writeString(tmp, wrapped, StandardCharsets.UTF_8);
                Path glslang = Forge.glslang();
                if (glslang == null) return "glslang isn't installed, so the effect can't be checked";
                Process p = new ProcessBuilder(glslang.toString(), "-S", "frag", tmp.toString()).redirectErrorStream(true).start();
                String output = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                p.waitFor();
                StringBuilder errors = new StringBuilder();
                for (String l : output.split("\\R")) if (l.contains("ERROR")) errors.append(l).append('\n');
                return errors.toString().strip();
            } finally {
                Files.deleteIfExists(tmp);
            }
        }
    }
}
