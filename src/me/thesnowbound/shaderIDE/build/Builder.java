package me.thesnowbound.shaderIDE.build;

import me.thesnowbound.shaderIDE.project.Project;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// the command line builder: builds a project folder into a resource pack without opening the editor
public final class Builder {
    static final String USAGE = """
            shader-build - build a Shader IDE project into a resource pack

            usage: shader-build [options] [project folder]
                   shader-build --unmap <name.remap.json> <file.glsl>

              project folder      the folder with project.json (default: the current folder)
              --readable          keep names readable instead of obfuscating them
              --no-check          skip the glslang check
              --skript            also write the Skript file next to the pack
              --unmap <map> <f>   print a shader with its original names put back
              -h, --help          show this help
              -v, --version       show the version

            build.json in the project folder sets the defaults for these:
              {"obfuscate": true, "check": true, "skript": false, "remap": true}
            The command line overrides it. "remap" writes <name>.remap.json next to the pack when obfuscating.
            Each effect in project.json sets its own "model" ("cube", "cylinder" or "circle") and "obfuscation":
              "lite" (default): renames and strips comments and spacing
              "max": also hides the numbers, adds decoy code and scrambles the wrapper
              or an object: {"style": "max", "decoys": 6, "numbers": false} to change any part
            An effect is scrambled only when build.json and the effect both allow it.
            """;

    private Builder() {
    }

    public static void main(String[] args) {
        try {
            run(List.of(args));
        } catch (Exception e) {
            System.err.println("shader-build failed: " + (e.getMessage() == null ? e : e.getMessage()));
            System.exit(1);
        }
    }

    static void run(List<String> args) throws Exception {
        if (args.contains("-h") || args.contains("--help")) {
            System.out.print(USAGE);
            return;
        }
        if (args.contains("-v") || args.contains("--version")) {
            System.out.println(Forge.TOOL + " " + Forge.VERSION);
            return;
        }
        if (args.contains("--unmap")) {
            unmap(args);
            return;
        }

        boolean readable = false, noCheck = false, skriptFlag = false;
        List<String> folders = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            String a = args.get(i);
            switch (a) {
                case "--readable" -> readable = true;
                case "--no-check" -> noCheck = true;
                case "--skript" -> skriptFlag = true;
                default -> {
                    if (a.startsWith("-")) {
                        System.err.println("unknown option: " + a);
                        System.err.print(USAGE);
                        System.exit(2);
                    }
                    folders.add(a);
                }
            }
        }
        if (folders.size() > 1) {
            System.err.println("give one project folder, not " + folders.size());
            System.exit(2);
        }
        Path dir = Path.of(folders.isEmpty() ? "." : folders.get(0)).toAbsolutePath().normalize();
        if (!dir.resolve("project.json").toFile().isFile()) {
            System.err.println("no project.json in " + dir);
            System.exit(2);
        }

        BuildConfig file = BuildConfig.read(dir);
        BuildConfig settings = new BuildConfig(
                file.obfuscate() && !readable,
                file.check() && !noCheck,
                file.skript() || skriptFlag,
                file.remap());

        Project project = Project.load(dir);
        Forge forge = new Forge(project, System.out::println, settings);
        Path zip = forge.build(settings.obfuscate(), settings.check());
        if (settings.skript()) forge.writeSkript(zip.getParent());
        System.out.println("done: " + zip);
    }

    // shader-build --unmap <name.remap.json> <file.glsl>
    static void unmap(List<String> args) throws Exception {
        int at = args.indexOf("--unmap");
        if (args.size() != at + 3) throw new IllegalArgumentException("usage: shader-build --unmap <name.remap.json> <file.glsl>");
        Path remap = Path.of(args.get(at + 1)), shader = Path.of(args.get(at + 2));
        String text = Files.readString(shader, StandardCharsets.UTF_8);
        System.out.print(Remap.unmap(text, Remap.reverse(remap)));
    }
}
