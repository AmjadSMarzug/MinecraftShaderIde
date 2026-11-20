package me.thesnowbound.shaderIDE.build;

import me.thesnowbound.shaderIDE.project.Project;

import java.nio.file.Path;
import java.util.List;

// the command line builder: builds a project folder into a resource pack without opening the editor
// shader-build [options] [project folder]
public final class Builder {
    static final String USAGE = """
            shader-build - build a Shader IDE project into a resource pack

            usage: shader-build [options] [project folder]

              project folder   the folder with project.json (default: the current folder)
              --readable       keep names readable instead of obfuscating them
              --no-check       skip the glslang check
              --skript         also write the Skript file next to the pack
              -h, --help       show this help
              -v, --version    show the version
            """;

    private Builder() {
    }

    public static void main(String[] args) {
        List<String> flags = List.of(args);
        if (flags.contains("-h") || flags.contains("--help")) {
            System.out.print(USAGE);
            return;
        }
        if (flags.contains("-v") || flags.contains("--version")) {
            System.out.println(Forge.TOOL + " " + Forge.VERSION);
            return;
        }
        for (String f : flags) {
            if (f.startsWith("-") && !List.of("--readable", "--no-check", "--skript").contains(f)) {
                System.err.println("unknown option: " + f);
                System.err.print(USAGE);
                System.exit(2);
            }
        }
        List<String> folders = flags.stream().filter(f -> !f.startsWith("-")).toList();
        if (folders.size() > 1) {
            System.err.println("give one project folder, not " + folders.size());
            System.exit(2);
        }
        Path dir = Path.of(folders.isEmpty() ? "." : folders.get(0)).toAbsolutePath().normalize();
        if (!dir.resolve("project.json").toFile().isFile()) {
            System.err.println("no project.json in " + dir);
            System.exit(2);
        }
        try {
            Project project = Project.load(dir);
            Forge forge = new Forge(project, System.out::println);
            Path zip = forge.build(!flags.contains("--readable"), !flags.contains("--no-check"));
            if (flags.contains("--skript")) forge.writeSkript(zip.getParent());
            System.out.println("done: " + zip);
        } catch (Exception e) {
            System.err.println("build failed: " + (e.getMessage() == null ? e : e.getMessage()));
            System.exit(1);
        }
    }
}
