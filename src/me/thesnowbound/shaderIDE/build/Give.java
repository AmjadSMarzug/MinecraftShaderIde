package me.thesnowbound.shaderIDE.build;

import me.thesnowbound.shaderIDE.project.Project;

import java.util.ArrayList;
import java.util.List;

// one /give command per effect, the same item the generated Skript gives, so a player can pick any colour
public final class Give {
    private Give() {
    }

    public static List<String> commands(Project project) {
        List<String> out = new ArrayList<>();
        for (Project.Effect e : project.effects) {
            out.add("give @p minecraft:paper[minecraft:item_model=\"" + project.namespace + ":" + e.item() + "\"]");
        }
        return out;
    }

    public static String text(Project project) {
        return String.join("\n", commands(project)) + "\n";
    }
}
