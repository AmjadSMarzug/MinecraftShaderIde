package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonWarp {
    private LessonWarp() {
    }

    public static final Lesson LESSON = new Lesson("lesson_warp", "Domain warping", "noise that bends itself", 12, """
                    <p>fbm on its own looks like fog. To get swirls, or gas that looks like it's flowing, you <b>warp</b> the position before looking up the noise. Compute a few fbm values first, use them to shift the position, then run fbm again on the shifted position.</p>
                    <p>The shift is smooth noise too, so the result stays smooth. But it gets twisted into shapes plain noise never makes. Animate the shift and the shapes start to churn.</p>
                    <h2>What the offset does</h2>
                    <p>The warp moves the point before the noise is read, so the pattern gets dragged along with it. How far it moves decides the look:</p>
                    <table>
                    <tr><td><code>warp * 0.3</code></td><td>a gentle wobble, like heat haze</td></tr>
                    <tr><td><code>warp * 1.5</code></td><td>proper swirls and folds</td></tr>
                    <tr><td><code>warp * 4.0</code></td><td>chaotic, almost marbled</td></tr>
                    </table>
                    <h2>Three different offsets</h2>
                    <p>Each axis of the warp gets its own fbm with its own shift, like <code>p + vec3(5.2, 1.3, 2.8)</code>. If all three used the same position, they'd match, and the pattern would just slide along one diagonal.</p>
                    <h2>Going two levels deep</h2>
                    <p>You can warp the warp: build a first offset, use it to build a second, and feed the second into the final lookup. Each level adds another layer of folding. Two levels is usually enough for a smoky, folded look.</p>
                    <h2>Colour from the warp</h2>
                    <p><code>length(warp)</code> is large where the space was pushed hardest. Mixing colours by it lights up the edges of the swirls, which looks like veins or filaments.</p>
                    <h2>Cost</h2>
                    <p>Each fbm is five noise calls, and each noise call uses eight hashes. Three fbms for the warp plus the final one comes to 160 hashes per pixel. That's fine for a sky, but it grows fast with two levels, so give the warp fewer octaves than the final lookup.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Animating everything turns it to mush. Move only the warp and let the final pattern follow.</li>
                    <li>A huge strength makes the pattern noisy rather than swirly.</li>
                    </ul>
                    <h2>Working through the tasks</h2>
                    <p>The first tasks build one warp, feed it into the final lookup and make it move. The new tasks warp the warp a second
                    time, which makes folds inside folds, and color the result by how far the space was pushed, so the folds show up
                    as veins.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Marble: warp a pattern of stripes, <code>sin(p.x * 10.0)</code>, instead of fbm.</li>
                    <li>Color by <code>length(warp)</code> and see the veins appear.</li>
                    </ul>
                    """, """
                    // domain warping
                    // fbm here is the same octave loop from the clouds lesson, already written for you

                    float hash(vec3 p) {
                        return fract(sin(dot(p, vec3(127.1, 311.7, 74.7))) * 43758.5453);
                    }

                    float noise(vec3 p) {
                        vec3 i = floor(p);
                        vec3 f = fract(p);
                        f = f * f * (3.0 - 2.0 * f);
                        return mix(mix(mix(hash(i), hash(i + vec3(1, 0, 0)), f.x),
                                       mix(hash(i + vec3(0, 1, 0)), hash(i + vec3(1, 1, 0)), f.x), f.y),
                                   mix(mix(hash(i + vec3(0, 0, 1)), hash(i + vec3(1, 0, 1)), f.x),
                                       mix(hash(i + vec3(0, 1, 1)), hash(i + vec3(1, 1, 1)), f.x), f.y), f.z);
                    }

                    float fbm(vec3 p) {
                        float sum = 0.0;
                        float amp = 0.5;
                        for (int i = 0; i < 5; i++) {
                            sum += noise(p) * amp;
                            p = p * 2.03 + vec3(1.7, 9.2, 4.1);
                            amp *= 0.5;
                        }
                        return sum;
                    }

                    vec3 sky(vec3 dir, float time) {
                        vec3 p = dir * 2.5;
                        float n = fbm(p);
                        return vec3(n);
                    }
                    """, List.of(
                    step("Build a warp offset out of noise", "Three fbm calls with different offsets, one for each axis.",
                            "vec3 warp = vec3(fbm(p), fbm(p + vec3(5.2, 1.3, 2.8)), fbm(p + vec3(2.1, 7.7, 3.3)));", count("\\bfbm\\s*\\(", 4)),
                    step("Feed the warp back in", "Shift the position by the warp before the final fbm. A bigger multiplier twists it harder.",
                            "float n = fbm(p + warp * 1.6);", has("fbm\\s*\\([^;]*warp")),
                    step("Make it churn", "Add time to the position used for the warp so the swirls move. Make a moving copy of p and build the warp from that.",
                            "vec3 q = p + vec3(0.0, time * 0.05, 0.0);\nvec3 warp = vec3(fbm(q), fbm(q + vec3(5.2, 1.3, 2.8)), fbm(q + vec3(2.1, 7.7, 3.3)));", uses("time")),
                    step("Color it in two tones", "mix a dark color and a bright one using n, and return that instead of vec3(n).",
                            "vec3 color = mix(vec3(0.05, 0.0, 0.1), vec3(0.6, 0.2, 0.8), n);\nreturn color;", has("mix\\s*\\(\\s*vec3")),
                    step("Light up the thickest parts", "pow of n with a high power only lets the brightest spots through. Add them on top in a warm color.",
                            "color += vec3(1.0, 0.7, 0.4) * pow(n, 4.0) * 3.0;\nreturn color;", has("pow\\s*\\(")),
                    step("Warp the warp", "Feed the first warp back in to build a second one. Each level adds another fold.",
                            "vec3 warp2 = vec3(fbm(p + warp), fbm(p + warp + vec3(3.1, 1.7, 0.4)), fbm(p + warp + vec3(9.2, 5.5, 2.8)));", has("fbm\\s*\\(\\s*p\\s*\\+\\s*warp\\b")),
                    step("Color by how far the space was pushed", "length(warp) is big where the warp moved things the most. Mixing with it lights up the folds like veins.",
                            "color = mix(color, vec3(1.0, 0.9, 0.6), length(warp) * 0.3);", has("length\\s*\\(\\s*warp"))));
}
