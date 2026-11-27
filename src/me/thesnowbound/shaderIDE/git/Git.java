package me.thesnowbound.shaderIDE.git;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// git and GitHub through their command line tools: git for the commits, gh for creating the repo
public final class Git {
    private Git() {
    }

    // runs a command in the folder and returns what it printed. a failed command throws with what it said
    static String run(Path dir, List<String> command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command).directory(dir.toFile()).redirectErrorStream(true);
        Process p = pb.start();
        String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int code;
        try {
            code = p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted");
        }
        if (code != 0) throw new IOException(String.join(" ", command) + " failed:\n" + out.strip());
        return out.strip();
    }

    static List<String> cmd(String... parts) {
        return new ArrayList<>(List.of(parts));
    }

    public static boolean isRepo(Path dir) {
        try {
            return run(dir, cmd("git", "rev-parse", "--is-inside-work-tree")).equals("true");
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean hasChanges(Path dir) throws IOException {
        return !run(dir, cmd("git", "status", "--porcelain")).isBlank();
    }

    // stages everything, commits with the summary and description, and pushes to the upstream branch
    public static String commitAndPush(Path dir, String summary, String description) throws IOException {
        StringBuilder log = new StringBuilder();
        run(dir, cmd("git", "add", "-A"));
        if (hasChanges(dir) || !run(dir, cmd("git", "diff", "--cached", "--name-only")).isBlank()) {
            List<String> commit = cmd("git", "commit", "-m", summary);
            if (!description.isBlank()) {
                commit.add("-m");
                commit.add(description);
            }
            log.append(run(dir, commit)).append('\n');
        } else {
            log.append("nothing new to commit\n");
        }
        if (hasUpstream(dir)) {
            log.append(run(dir, cmd("git", "push")));
        } else {
            log.append(run(dir, cmd("git", "push", "-u", "origin", "HEAD")));
        }
        return log.toString().strip();
    }

    static boolean hasUpstream(Path dir) {
        try {
            run(dir, cmd("git", "rev-parse", "--abbrev-ref", "--symbolic-full-name", "@{u}"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // makes the folder a repo, commits what's there, and creates the GitHub repo with gh, then pushes
    public static String createRepo(Path dir, String name, boolean isPrivate, String description) throws IOException {
        StringBuilder log = new StringBuilder();
        if (!isRepo(dir)) log.append(run(dir, cmd("git", "init", "-b", "main"))).append('\n');
        run(dir, cmd("git", "add", "-A"));
        if (!run(dir, cmd("git", "diff", "--cached", "--name-only")).isBlank()) {
            log.append(run(dir, cmd("git", "commit", "-m", "Start the project"))).append('\n');
        }
        List<String> create = cmd("gh", "repo", "create", name, isPrivate ? "--private" : "--public", "--source", ".", "--remote", "origin", "--push");
        if (!description.isBlank()) {
            create.add("--description");
            create.add(description);
        }
        log.append(run(dir, create));
        return log.toString().strip();
    }
}
