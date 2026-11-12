package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonFirstSky {
    private LessonFirstSky() {
    }

    public static final Lesson LESSON = new Lesson("lesson_first_sky", "Your first sky", "colors, mix and the horizon", 6, """
                    <p>Everything in a sky effect happens inside one function, <code>sky</code>. Minecraft calls it once for
                    every pixel of the sky, every frame, and paints that pixel whatever color you give back.</p>
                    <p>The only thing that changes from pixel to pixel is <code>dir</code>, the direction that pixel looks in.
                    It's a vector of length 1, so <code>dir.y</code> is 1 when you look straight up, 0 at the horizon and -1
                    straight down. <code>dir.y</code> alone is enough for a simple sky.</p>
                    <p>Colors are <code>vec3(red, green, blue)</code> with each part from 0 to 1. <code>mix(a, b, t)</code> blends
                    two of them: all <code>a</code> when t is 0, all <code>b</code> when t is 1.</p>
                    <h2>Why one number is enough</h2>
                    <p>The sky is the inside of a huge ball around you. Every pixel is one point on that ball, and
                    <code>dir</code> is the arrow from you to that point. Because the arrow always has length 1, its <code>y</code> part
                    says exactly how high up the point is, no matter which way you face.</p>
                    <h2>What mix really does</h2>
                    <p><code>mix(a, b, t)</code> is <code>a * (1.0 - t) + b * t</code>. At t = 0.5 you get the color halfway between. If t
                    goes outside 0 to 1, mix keeps going past the colors you gave it, which is where the odd colors under the horizon
                    come from. <code>max</code> or <code>clamp</code> keeps t in range.</p>
                    <h2>Shaping the gradient</h2>
                    <p>A straight fade looks flat. Bend it before mixing:</p>
                    <table>
                    <tr><td><code>pow(up, 0.5)</code></td><td>the top color reaches down further</td></tr>
                    <tr><td><code>pow(up, 2.0)</code></td><td>the horizon color climbs higher</td></tr>
                    <tr><td><code>smoothstep(0.0, 0.3, up)</code></td><td>a quick band at the horizon, then even color above</td></tr>
                    </table>
                    <p>Try each one on the lesson's gradient and compare.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Colors above 1.0 all show as the same white, so a bright mix loses its detail.</li>
                    <li><code>float up = 1;</code> is an error, write <code>1.0</code>.</li>
                    <li>Without its own ground color, everything below the horizon is just the horizon color again.</li>
                    </ul>
<h2>Working through the tasks</h2>
                    <p>You start with one color mix and finish with a sky that has a ground, a glow on the horizon and a slow change
                    over time. Each task changes only one thing, so when something breaks you know which change caused it. Keep <code>dir.y</code>
                    in mind: almost every trick on this page is just a different way of bending that one number.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Use two mixes for three colors: one from the horizon to the middle, one from the middle to the top.</li>
                    <li>Make a night sky with a faint glow along the horizon.</li>
                    </ul>
                    """, """
                    // your first sky
                    // sky() runs once for every pixel of the sky. dir is the direction that pixel looks in,
                    // so dir.y is 1 straight up, 0 at the horizon and -1 straight down. time counts seconds.
                    // whatever color you return is what that pixel shows

                    vec3 sky(vec3 dir, float time) {
                        return vec3(0.1, 0.2, 0.4);
                    }
                    """, List.of(
                    step("Change the color", "Every number in vec3 is red, green and blue from 0 to 1. Try a warm orange.",
                            "return vec3(1.0, 0.5, 0.2);", lacks("vec3(0.1, 0.2, 0.4)")),
                    step("Fade between two colors with mix", "Use dir.y as the mix amount, so the first color sits on the horizon and the second straight up.",
                            "return mix(vec3(1.0, 0.6, 0.3), vec3(0.1, 0.2, 0.5), dir.y);", has("mix\\s*\\(").and(has("dir\\.y"))),
                    step("Keep the mix from going below zero", "Below the horizon dir.y is negative, and mix with a negative amount pushes colors past the first one into odd shades. max(dir.y, 0.0) stops at zero.",
                            "float up = max(dir.y, 0.0);\nreturn mix(vec3(1.0, 0.6, 0.3), vec3(0.1, 0.2, 0.5), up);", has("(max|clamp)\\s*\\([^;]*dir\\.y")),
                    step("Give the ground its own color", "Check dir.y and send back a different color for everything below the horizon.",
                            "if (dir.y < 0.0) return vec3(0.12, 0.09, 0.08);", has("dir\\.y\\s*<|dir\\.y\\s*<=").or(count("mix\\s*\\(", 2))),
                    step("Make it breathe with time", "sin(time) swings from -1 to 1 and back every few seconds. Add a little of it to the mix amount.",
                            "float up = max(dir.y + sin(time) * 0.1, 0.0);", uses("time")),
                    step("Soften the horizon", "smoothstep(0.0, 0.3, dir.y) rises from 0 to 1 over the first 0.3 above the horizon, so the fade is smooth instead of a straight line.",
                            "float band = smoothstep(0.0, 0.3, dir.y);", has("smoothstep\\s*\\(\\s*-?0\\.?\\d*\\s*,\\s*-?\\d*\\.\\d+\\s*,\\s*dir\\.y")),
                    step("Add a glow along the horizon", "exp(-abs(dir.y) * 8.0) is 1 right on the horizon and dies away quickly above and below it. Add it in a warm color.",
                            "float glow = exp(-abs(dir.y) * 8.0);\nreturn mix(vec3(0.1, 0.2, 0.5), vec3(1.0, 0.6, 0.3), up) + vec3(1.0, 0.5, 0.2) * glow * 0.4;", has("exp\\s*\\(\\s*-\\s*abs\\s*\\(\\s*dir\\.y"))));
}
