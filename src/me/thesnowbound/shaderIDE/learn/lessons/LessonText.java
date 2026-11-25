package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// a text effect: the letters themselves get colours instead of the sky
public final class LessonText {
    private LessonText() {
    }

    public static final Lesson LESSON = new Lesson("lesson_text", "Text effects", "colouring the letters themselves", 8, """
                    <p>A text effect changes the letters the game draws, not the sky. It's a plain <code>.glsl</code> file, checked by the same compiler as the others. Its one function is <code>vec4 text(vec2 p, vec2 size, float time)</code>.</p>
                    <p><code>p</code> is the pixel inside the letter, from 0 to <code>size</code> across and down, with y going down. The game's letters are 8 by 8 pixels, so <code>size</code> is 8 for them.</p>
                    <p>Return a colour and an alpha. The game draws the letter's own shape over it, so you only pick colours.</p>
                    <h2>Working through the tasks</h2>
                    <p>The starter has a missing semicolon. The next tasks use the position, use time, mix two colours, and add a pattern.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Make the top of each letter one colour and the bottom another.</li>
                    <li>Blink the whole word on and off once a second.</li>
                    </ul>
                    """, """
                    // text effect: your first letter colour
                    vec4 text(vec2 p, vec2 size, float time) {
                        vec3 colour = vec3(1.0, 0.8, 0.2)
                        return vec4(colour, 1.0);
                    }
                    """, List.of(
                    step("Fix the missing semicolon", "The colour line has no ; at the end.",
                            "vec3 colour = vec3(1.0, 0.8, 0.2);", has("vec3\\s+colour\\s*=[^;\\n]*;")),
                    step("Use the position", "Change the colour with where the pixel is, using p. Try p.y / size.y, so the colour changes from top to bottom.",
                            "vec3 colour = mix(vec3(1.0, 0.8, 0.2), vec3(1.0, 0.2, 0.2), p.y / size.y);", count("\\bp\\b", 2)),
                    step("Use time", "Use time so the colour moves. sin(time) goes up and down every few seconds.",
                            "colour *= 0.75 + 0.25 * sin(time * 4.0);", count("\\btime\\b", 2)),
                    step("Mix two colours", "mix(a, b, t) blends two colours. Use it to blend your colour with a second one.",
                            "colour = mix(colour, vec3(0.2, 0.9, 1.0), 0.5);", has("mix\\s*\\(")),
                    step("Add a pattern", "fract and floor make repeating patterns. floor(p) gives each pixel a cell number.",
                            "float bit = step(0.5, fract(p.x / 2.0));\ncolour *= bit;", has("(fract|floor)\\s*\\("))));
}
