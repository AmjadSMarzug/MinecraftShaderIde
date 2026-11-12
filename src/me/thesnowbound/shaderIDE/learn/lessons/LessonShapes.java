package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonShapes {
    private LessonShapes() {
    }

    public static final Lesson LESSON = new Lesson("lesson_shapes", "Combining shapes", "union, cut and smooth blends", 12, """
                    <p>Distance functions combine with plain math. <code>min(a, b)</code> keeps whichever surface is closer,
                    which glues two shapes together. <code>max(a, -b)</code> keeps <code>a</code> only where you're outside
                    <code>b</code>, which carves <code>b</code> out of it.</p>
                    <p>A plain min leaves a hard crease where shapes meet. A <b>smooth min</b> blends the two distances over a
                    small range, so the shapes melt into each other like clay.</p>
                    <p>Keeping the shape in its own function means the normal and the march both use the same thing.</p>
                    <h2>The three ways to combine</h2>
                    <table>
                    <tr><td><code>min(a, b)</code></td><td>both shapes together</td></tr>
                    <tr><td><code>max(a, b)</code></td><td>only where they overlap</td></tr>
                    <tr><td><code>max(a, -b)</code></td><td>a with b carved out of it</td></tr>
                    </table>
                    <h2>Smooth min, explained</h2>
                    <p><code>k</code> is how wide the blend is, measured in the same units as the shapes. 0.05 rounds the joint like a
                    fillet, 0.3 melts the shapes into one blob. There's a smooth version of carving too: <code>-smin(-a, b, k)</code>
                    cuts with a soft, rounded edge.</p>
                    <h2>Repeating a shape</h2>
                    <p>Folding space repeats whatever you measure in it. <code>q = mod(p + 0.5 * c, c) - 0.5 * c</code> makes copies every
                    <code>c</code> units along each axis, all for the price of one shape. Keep the result inside the box with a
                    <code>max</code> against a big box, or the copies fill everything.</p>
                    <h2>Twisting and bending</h2>
                    <p>Turning <code>p.xz</code> by an angle that depends on <code>p.y</code> twists the shape like a towel. Deformations
                    like this break the "never too far" rule a little, so step by <code>d * 0.7</code> instead of the full d, or you'll
                    see holes.</p>
                    <h2>Keeping it readable</h2>
                    <p>Give each part its own named distance (<code>ball</code>, <code>ring</code>, <code>stem</code>) and combine them
                    at the end. When something looks wrong, you can return a single part to see it on its own.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Carving with the wrong sign removes everything outside the cutter instead of inside it.</li>
                    <li>Heavy twisting with full steps leaves holes and missing chunks.</li>
                    </ul>
<h2>Working through the tasks</h2>
                    <p>You build a shape out of parts, then carve, blend, repeat and animate it. The new tasks repeat a copy of the shape
                    across space with <code>mod</code>, and twist it, which is the fastest way to get complex forms from one simple one.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>A twisted box.</li>
                    <li>A row of rings using repetition along one axis only.</li>
                    </ul>
                    """, """
                    // combining shapes
                    // shape() is the distance to the surface. everything you build goes in here

                    float shape(vec3 p) {
                        return length(p) - 0.45;
                    }

                    vec4 object(vec3 origin, vec3 dir, float time) {
                        float t = 0.0;
                        for (int i = 0; i < 80; i++) {
                            vec3 p = origin + dir * t;
                            float d = shape(p);
                            if (d < 0.001) {
                                vec2 e = vec2(0.001, 0.0);
                                vec3 n = normalize(vec3(shape(p + e.xyy) - shape(p - e.xyy),
                                                        shape(p + e.yxy) - shape(p - e.yxy),
                                                        shape(p + e.yyx) - shape(p - e.yyx)));
                                float light = max(dot(n, normalize(vec3(0.5, 0.8, 0.3))), 0.0);
                                return vec4(vec3(0.9, 0.7, 0.4) * (0.15 + light), 1.0);
                            }
                            t += d;
                            if (t > 3.5) break;
                        }
                        return vec4(0.0);
                    }
                    """, List.of(
                    step("Glue a box onto the sphere", "Work out both distances and keep the smaller one with min.",
                            "float ball = length(p) - 0.45;\nfloat box = length(max(abs(p) - vec3(0.6, 0.15, 0.15), 0.0));\nreturn min(ball, box);", has("min\\s*\\(")),
                    step("Carve a hole through it", "max with the negative of another shape cuts that shape away.",
                            "float hole = length(p.xy) - 0.2;\nreturn max(min(ball, box), -hole);", has("max\\s*\\([^;]*-")),
                    step("Blend shapes smoothly", "Write a smooth min above shape, then use it in shape instead of min. k is how wide the blend is.",
                            "float smin(float a, float b, float k) {\n    float h = clamp(0.5 + 0.5 * (b - a) / k, 0.0, 1.0);\n    return mix(b, a, h) - k * h * (1.0 - h);\n}\n\n// and in shape():\nreturn max(smin(ball, box, 0.15), -hole);",
                            has("float\\s+\\w+\\s*\\(\\s*float\\s+\\w+\\s*,\\s*float\\s+\\w+\\s*,\\s*float").or(has("0\\.5\\s*\\+\\s*0\\.5\\s*\\*"))),
                    step("Add a ring", "A torus is a circle in the xz plane with thickness. length(p.xz) gives the distance from the middle.",
                            "vec2 q = vec2(length(p.xz) - 0.7, p.y);\nfloat ring = length(q) - 0.06;\nreturn min(max(smin(ball, box, 0.15), -hole), ring);", has("length\\s*\\(\\s*p\\.xz\\s*\\)")),
                    step("Animate it", "shape() can't see time, and it's called seven times, so instead of passing it everywhere keep it in a variable above shape. Set it first thing in object, then move a piece with it, like the ring bobbing up and down.",
                            "float now;   // above shape()\n\nnow = time;   // first line of object()\n\nvec2 q = vec2(length(p.xz) - 0.7, p.y - sin(now) * 0.2);   // in shape()", uses("time")),
                    step("Repeat the shape", "mod(p + half, c) - half folds space so the shape copies itself every c units along x.",
                            "vec3 q = mod(p + vec3(0.5), vec3(1.0)) - vec3(0.5);", has("mod\\s*\\(\\s*p")),
                    step("Twist it", "Turning p.xz by an angle that depends on p.y twists the shape like a towel. Use a small angle so it doesn't tear.",
                            "float a = p.y * 2.0;\nvec2 twisted = mat2(cos(a), -sin(a), sin(a), cos(a)) * p.xz;", has("cos\\s*\\(\\s*p\\.y|a\\s*=\\s*p\\.y"))));
}
