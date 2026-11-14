package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonClouds {
    private LessonClouds() {
    }

    public static final Lesson LESSON = new Lesson("lesson_clouds", "Noise clouds", "noise, octaves and a cloud ceiling", 12, """
                    <p>Random numbers for each pixel just look like static. <b>Value noise</b> places random numbers at the corners of a grid and blends smoothly between them. Pixels next to each other then get similar values, which gives soft blobs.</p>
                    <p>One layer of noise looks like blurry blobs. Add a second layer with twice the detail and half the strength, then a third, and so on. This stack is called <b>fbm</b>. Each layer is an <b>octave</b>. Clouds and smoke are usually built this way.</p>
                    <p>Real clouds sit on a ceiling above you. Near the horizon you see many of them squashed together. Dividing <code>dir.xz</code> by <code>dir.y</code> gives that same effect, because it turns directions into positions on a flat ceiling.</p>
                    <h2>Inside the noise</h2>
                    <p><code>hash</code> turns a position into a number that looks random. <code>noise</code> asks the hash for values at the eight corners of the grid cube around a point, then blends them. The blend uses <code>f * f * (3.0 - 2.0 * f)</code>, which eases in and out, so there are no sharp lines along the grid.</p>
                    <h2>Scale and octaves</h2>
                    <p>Multiplying the position zooms the noise in. <code>noise(p * 8.0)</code> has smaller blobs than <code>noise(p * 2.0)</code>. Each octave doubles the zoom and halves the strength, so the big shapes stay big and the small ones only add detail. Four to six octaves is enough for a sky. More looks better but costs more.</p>
                    <h2>Why the ceiling trick works</h2>
                    <p>Near the horizon <code>dir.y</code> is tiny, so the division makes clouds stretch out and flicker. That's why the lesson fades them out near the horizon. <code>max(dir.y, 0.05)</code> stops the division from hitting zero.</p>
                    <h2>Coverage and softness</h2>
                    <p>In <code>smoothstep(a, b, n)</code>, a bigger <code>a</code> gives fewer clouds. Putting <code>a</code> and <code>b</code> close together gives sharper edges. For a lit look, read the noise a second time a little toward the sun. Where that second value is lower, the edge faces the light.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Without a horizon fade, the clouds shimmer in a line where they meet the ground.</li>
                    <li>Adding time inside the loop makes every octave drift at a different speed. That can look good, but it's easy to do by accident.</li>
                    </ul>
                    <h2>Working through the tasks</h2>
                    <p>The tasks go from one noise call, to a stack of octaves, to a cloud ceiling, to drifting and cutting out shapes. The new
                    tasks add lighting that fakes sunlight on one side of a cloud and a second, faster layer above the first.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Two cloud layers at different heights drifting at different speeds.</li>
                    <li>Gray, heavy storm clouds with almost no sky between them.</li>
                    </ul>
                    """, """
                    // noise clouds
                    // noise() gives smooth random values between 0 and 1. adding a few layers of it, each one twice as
                    // detailed and half as strong, is how almost every cloud, smoke and gas effect is made

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

                    vec3 sky(vec3 dir, float time) {
                        vec3 blue = mix(vec3(0.5, 0.7, 0.9), vec3(0.15, 0.35, 0.75), max(dir.y, 0.0));
                        return blue;
                    }
                    """, List.of(
                    step("Use noise for the color", "Feed noise a scaled direction and blend toward white with the result.",
                            "float n = noise(dir * 4.0);\nreturn mix(blue, vec3(1.0), n);", count("\\bnoise\\s*\\(", 2)),
                    step("Stack a few octaves in a loop", "Each pass adds noise, doubles the scale and halves the strength. This replaces the single noise line, the return stays the same.",
                            "float n = 0.0;\nfloat amp = 0.5;\nvec3 p = dir * 3.0;\nfor (int i = 0; i < 5; i++) {\n    n += noise(p) * amp;\n    p *= 2.0;\n    amp *= 0.5;\n}\nreturn mix(blue, vec3(1.0), n);",
                            has("for\\s*\\(")),
                    step("Put the clouds on a ceiling", "Divide dir.xz by dir.y (kept above a small number so it never divides by zero) and use that as the noise position, in place of vec3 p = dir * 3.0.",
                            "vec2 ceiling = dir.xz / max(dir.y, 0.05);\nvec3 p = vec3(ceiling, 0.0);", has("dir\\.xz\\s*/")),
                    step("Let the clouds drift", "Add time to the position before the loop so the whole layer slides along.",
                            "vec3 p = vec3(ceiling + vec2(time * 0.1, 0.0), 0.0);", uses("time")),
                    step("Cut out cloud shapes", "smoothstep(0.45, 0.7, n) turns the soft noise into clouds with clear gaps of sky between them.",
                            "float cover = smoothstep(0.45, 0.7, n);\nreturn mix(blue, vec3(1.0), cover);", has("smoothstep\\s*\\(")),
                    step("Fade them out at the horizon", "A second smoothstep on dir.y hides the squashed clouds right at the edge.",
                            "float cover = smoothstep(0.45, 0.7, n);\ncover *= smoothstep(0.0, 0.25, dir.y);\nreturn mix(blue, vec3(1.0), cover);", has("smoothstep\\s*\\([^;]*dir\\.y")),
                    step("Light the cloud from the sun's side", "Look the noise up a little toward the sun. Where that value is lower, the cloud's edge faces the light and you can brighten it.",
                            "float lit = noise(p + vec3(0.2, 0.4, 0.0));", has("noise\\s*\\(\\s*p\\s*\\+")),
                    step("Add a higher, faster layer", "A second ceiling at half the height moves at a different speed, so the two layers slide past each other.",
                            "vec2 high = dir.xz / max(dir.y, 0.05) * 0.5;", has("dir\\.xz\\s*/\\s*max\\s*\\([^;]*\\)\\s*\\*\\s*0\\.5"))));
}
