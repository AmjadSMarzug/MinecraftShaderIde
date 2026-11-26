package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// bulk effects: one template file that the compiler turns into many effects
public final class LessonVariants {
    private LessonVariants() {
    }

    public static final Lesson LESSON = new Lesson("lesson_variants", "Bulk effects", "one template, many effects", 8, """
                    <p>When you want the same sky in several colours, you don't have to copy the file and change one number. Write it once as a <b>variants template</b>, and the compiler makes one effect for every combination.</p>
                    <p>A template is a file called <code>NAME.variants.glsl</code> in the project's <code>effects</code> folder. The lines that start with <code>//@</code> are the settings. Everything else is your shader.</p>
                    <h2>The settings</h2>
                    <table>
                    <tr><td><code>//@each colour: red = vec3(1.0, 0.2, 0.2); blue = vec3(0.2, 0.4, 1.0)</code></td><td>one axis called <code>colour</code> with two values. Each value is a label, then <code>=</code>, then the value that goes in the code. Separate values with <code>;</code></td></tr>
                    <tr><td><code>//@name glow_{colour.name}</code></td><td>the name of each effect. Placeholders are filled in, so this makes <code>glow_red</code> and <code>glow_blue</code></td></tr>
                    <tr><td><code>//@model circle</code></td><td>optional: the item shape for every effect the template makes</td></tr>
                    <tr><td><code>//@obfuscation max</code></td><td>optional: how scrambled those effects are</td></tr>
                    </table>
                    <h2>The placeholders</h2>
                    <p><code>{colour}</code> becomes the value, like <code>vec3(1.0, 0.2, 0.2)</code>. <code>{colour.name}</code> becomes the label, like <code>red</code>. Use the value in the code and the label in the name.</p>
                    <h2>Two axes make every combination</h2>
                    <p>Each extra <code>//@each</code> line multiplies the count. Two colours and two shades make four effects: <code>glow_red_dark</code>, <code>glow_red_light</code>, <code>glow_blue_dark</code> and <code>glow_blue_light</code>.</p>
                    <h2>What you get</h2>
                    <p>The effects appear in the project and build with the rest, but they aren't saved into <code>project.json</code>. The template is the one thing you edit. Change it and every effect it makes changes too.</p>
                    <h2>Working through the tasks</h2>
                    <p>The starter is one plain sky. The tasks add the colour axis, a second axis, a name built from the placeholders, and then use the value in the code.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Add a third axis for brightness and see how many effects you get.</li>
                    <li>Use <code>{colour.name}</code> in the name only, and <code>{colour}</code> in the code only.</li>
                    </ul>
                    """, """
                    // bulk effects: one template, many effects
                    vec3 sky(vec3 dir, float time) {
                        return vec3(1.0, 0.5, 0.2) * (0.5 + 0.5 * dir.y);
                    }
                    """, List.of(
                    step("Add a colour axis", "A //@each line at the top names the axis and lists its values, separated by ;.",
                            "//@each colour: red = vec3(1.0, 0.2, 0.2); blue = vec3(0.2, 0.4, 1.0)", has("//@each\\s+colour\\s*:[^\\n]*=")),
                    step("Name the effects", "A //@name line sets each effect's name. Use {colour.name} to put the label in it.",
                            "//@name sky_{colour.name}", has("//@name\\s+\\S*\\{colour\\.name\\}")),
                    step("Use the value in the code", "{colour} is replaced by the value, so put it where the colour goes.",
                            "return {colour} * (0.5 + 0.5 * dir.y);", has("\\{colour\\}")),
                    step("Add a second axis", "Another //@each line adds a second axis, and every combination of the two becomes an effect.",
                            "//@each shade: dark = 0.4; light = 1.0", count("//@each", 2)),
                    step("Use the second axis", "Put {shade} in the code too, so each effect looks different.",
                            "return {colour} * {shade} * (0.5 + 0.5 * dir.y);", has("\\{shade\\}"))))
                    ;
}
