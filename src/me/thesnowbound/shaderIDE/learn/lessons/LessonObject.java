package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonObject {
    private LessonObject() {
    }

    public static final Lesson LESSON = new Lesson("lesson_object", "Your first object", "raymarching inside the item", 10, """
                    <p>An object draws a 3D shape inside the item's box, which goes from -1 to 1 on every axis. For every pixel
                    you get a ray: <code>origin</code> is where it enters the box and <code>dir</code> is where it's heading.</p>
                    <p>To find what the ray hits you use a <b>distance function</b>: give it a point and it says how far that
                    point is from the surface. For a sphere that's just <code>length(p) - radius</code>. You step along the ray
                    by exactly that distance, because nothing can be closer. Near the surface the steps get tiny, and when
                    they're small enough you've hit it. This is called <b>raymarching</b>.</p>
                    <p>Return alpha 1 for solid, 0 for empty space so the world shows through, or anything between for glass and glow.</p>
                    <h2>What a distance function returns</h2>
                    <p>A distance function gives a positive number outside the shape, zero on its surface and a negative number inside.
                    As long as it never says a surface is further away than it really is, stepping forward by that amount can't skip past anything. Raymarching depends on that rule.</p>
                    <h2>The loop, line by line</h2>
                    <ul>
                    <li><code>t</code> is how far along the ray you've walked.</li>
                    <li><code>p = origin + dir * t</code> is where that puts you.</li>
                    <li><code>d</code> is how far the closest surface is from there.</li>
                    <li>If d is tiny you've hit it. If not, walk d further and try again.</li>
                    <li>Once t passes 3.5 you've left the box, so the ray missed.</li>
                    </ul>
                    <h2>Moving, turning and scaling</h2>
                    <p>To move a shape, you move the space it's measured in instead. To put a sphere to the right, measure from a point
                    to the right: <code>length(p - vec3(0.3, 0.0, 0.0)) - 0.2</code>. To turn it, turn <code>p</code> the other way. To
                    scale it, divide p and multiply the result: <code>shape(p / s) * s</code>.</p>
                    <h2>Shapes to know</h2>
                    <table>
                    <tr><td>sphere</td><td><code>length(p) - r</code></td></tr>
                    <tr><td>box</td><td><code>length(max(abs(p) - size, 0.0))</code></td></tr>
                    <tr><td>torus</td><td><code>length(vec2(length(p.xz) - R, p.y)) - r</code></td></tr>
                    <tr><td>capsule</td><td>distance to a line segment, minus the radius</td></tr>
                    </table>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Too few steps leaves holes around the edges where rays brush past.</li>
                    <li>A hit threshold that's too big makes shapes look swollen and blobby.</li>
                    <li>A shape bigger than the box gets cut off flat at its walls.</li>
                    <li>Returning a color with alpha 1 when the ray misses draws a black box around everything.</li>
                    </ul>
<h2>Working through the tasks</h2>
                    <p>Each task changes one part of the ray march: the shape, its size, how it spins and how it's lit. The new tasks add
                    a pattern across the surface and a fog that thickens with distance, so the object sits in the air instead of
                    floating in a void.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Move the sphere round in a circle with <code>cos(time)</code> and <code>sin(time)</code>.</li>
                    <li>Try the torus formula from the table.</li>
                    </ul>
                    """, """
                    // your first object
                    // the box goes from -1 to 1 on every axis and origin is where the ray enters it.
                    // step along the ray by the distance to the surface until that distance gets tiny

                    vec4 object(vec3 origin, vec3 dir, float time) {
                        float t = 0.0;
                        for (int i = 0; i < 64; i++) {
                            vec3 p = origin + dir * t;
                            float d = length(p) - 0.5;
                            if (d < 0.001) {
                                vec3 n = normalize(p);
                                float light = max(dot(n, normalize(vec3(0.5, 0.8, 0.3))), 0.0);
                                return vec4(vec3(0.3, 0.6, 1.0) * (0.2 + light), 1.0);
                            }
                            t += d;
                            if (t > 3.5) break;
                        }
                        return vec4(0.0);
                    }
                    """, List.of(
                    step("Change the size of the sphere", "The 0.5 in length(p) - 0.5 is the radius.", "float d = length(p) - 0.3;", lacks("length(p) - 0.5")),
                    step("Turn it into a box", "abs folds every side onto one corner, so one formula covers all six faces.",
                            "float d = length(max(abs(p) - 0.4, 0.0));", has("abs\\s*\\(\\s*p\\s*\\)")),
                    step("Spin it", "Rotate p right after it's worked out and before the distance is measured. Spinning the space spins the shape.",
                            "vec3 p = origin + dir * t;\nfloat a = time;\np.xz = mat2(cos(a), -sin(a), sin(a), cos(a)) * p.xz;", has("mat2\\s*\\(|cos\\s*\\([^;]*\\btime\\b").and(uses("time"))),
                    step("Light it from a second side", "A second dot with another light direction, in another color, adds depth. Work it out next to the first light and add it into the color you return.",
                            "float back = max(dot(n, normalize(vec3(-0.6, 0.2, -0.5))), 0.0);\nreturn vec4(vec3(0.3, 0.6, 1.0) * (0.2 + light) + vec3(1.0, 0.4, 0.2) * back * 0.5, 1.0);", count("\\bdot\\s*\\(", 2)),
                    step("Give it a glow when the ray misses", "Keep the smallest distance the ray ever had. Three lines: one before the loop, one in the loop right after d, and the last return swapped for a soft alpha.",
                            "float closest = 9.0;\nfor (int i = 0; i < 64; i++) {\n    vec3 p = origin + dir * t;\n    float d = length(p) - 0.5;\n    closest = min(closest, d);\n    // ... the rest of the loop stays the same\n}\nreturn vec4(0.3, 0.6, 1.0, exp(-closest * 12.0) * 0.6);",
                            has("min\\s*\\(").and(has("exp\\s*\\("))),
                    step("Stripes across the surface", "fract(p.y * 8.0) repeats every eighth of a unit up the shape, and step turns it into hard bands.",
                            "float stripes = step(0.5, fract(p.y * 8.0));\ncolor *= 0.7 + 0.3 * stripes;", has("fract\\s*\\(\\s*p\\.y")),
                    step("Fog that gets thicker with distance", "t is how far the ray has travelled, so exp(-t * 0.8) fades the shape into the background the further away it is.",
                            "color = mix(vec3(0.5, 0.6, 0.7), color, exp(-t * 0.8));", has("exp\\s*\\(\\s*-\\s*t\\b"))));
}
