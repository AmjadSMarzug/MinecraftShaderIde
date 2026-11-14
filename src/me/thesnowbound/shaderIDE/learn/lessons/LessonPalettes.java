package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonPalettes {
    private LessonPalettes() {
    }

    public static final Lesson LESSON = new Lesson("lesson_palettes", "Color like a pro", "cosine palettes and tone mapping", 8, """
                    <p>Choosing every colour by hand is slow. A <b>cosine palette</b> turns one number into a smooth range of colours: <code>a + b * cos(6.28318 * (c * t + d))</code>. Here <code>a</code>, <code>b</code>, <code>c</code> and <code>d</code> are vec3s. Changing <code>d</code> shifts the hues, which makes it easy to find a set that looks good together.</p>
                    <p>When you add a lot of light, colours go past 1 and turn into flat white. <b>Tone mapping</b> squeezes them back into range. <code>1.0 - exp(-color)</code> leaves dark colours almost alone and bends bright ones smoothly toward 1, so highlights keep their shape.</p>
                    <h2>Reading the four vectors</h2>
                    <table>
                    <tr><td><code>a</code></td><td>the middle brightness of each channel</td></tr>
                    <tr><td><code>b</code></td><td>how far each channel swings, which sets the contrast</td></tr>
                    <tr><td><code>c</code></td><td>how many times it cycles as t goes from 0 to 1</td></tr>
                    <tr><td><code>d</code></td><td>where each channel starts its cycle, which sets the hues</td></tr>
                    </table>
                    <h2>Sets to start from</h2>
                    <table>
                    <tr><td>rainbow</td><td><code>d = vec3(0.0, 0.33, 0.67)</code></td></tr>
                    <tr><td>fire</td><td><code>a = vec3(0.5), b = vec3(0.5), c = vec3(1.0, 0.7, 0.4), d = vec3(0.0, 0.15, 0.2)</code></td></tr>
                    <tr><td>ice</td><td><code>d = vec3(0.5, 0.6, 0.7)</code> with <code>b = vec3(0.3)</code></td></tr>
                    </table>
                    <p>Change one vector at a time to see what each one does.</p>
                    <h2>Why tone mapping</h2>
                    <p>A screen can only show values from 0 to 1, but light adds up without limit. When you stack suns, glows and stars, values pass 1.0 and every bright part becomes the same flat white. <code>1.0 - exp(-color * k)</code> bends the top end, so bright values get close to 1 without reaching it. The <code>k</code> works like exposure: bigger is brighter.</p>
                    <h2>Brighter blends</h2>
                    <p>Mixing two colours in a straight line gives a slightly dull middle. Mixing their squares and taking the square root, <code>sqrt(mix(a * a, b * b, t))</code>, keeps the middle bright. That helps sunsets and gradients.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Adding more light after tone mapping brings the clipping back. Tone map last.</li>
                    <li>A big <code>c</code> makes the colours cycle so fast they look like stripes.</li>
                    </ul>
                    <h2>Working through the tasks</h2>
                    <p>You build the palette function first, then use it, animate it and finally tone map. The new tasks are about the
                    brightness of the whole sky: a square root to lift the dark parts, and one exposure value to control everything at once.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Color the clouds from lesson 4 with a palette.</li>
                    <li>Try exposures of 0.5, 1.5 and 4.0 and compare.</li>
                    </ul>
                    """, """
                    // color like a pro
                    // this sky is way too bright on purpose, half of it clips straight to white

                    vec3 sky(vec3 dir, float time) {
                        float t = dir.y * 0.5 + 0.5;
                        vec3 color = vec3(t * 2.0, 0.6, 2.0 - t * 2.0) * 1.5;
                        return color;
                    }
                    """, List.of(
                    step("Write a cosine palette function", "Above sky, add a function that takes a float and returns a vec3 using cos.",
                            "vec3 palette(float t) {\n    return vec3(0.5) + vec3(0.5) * cos(6.28318 * (vec3(1.0) * t + vec3(0.0, 0.33, 0.67)));\n}",
                            has("vec3\\s+\\w+\\s*\\(\\s*float").and(has("cos\\s*\\("))),
                    step("Use it to color the sky", "Call your palette with t instead of building the color by hand.",
                            "vec3 color = palette(t) * 1.5;", count("palette\\s*\\(", 2).or(has("color\\s*=\\s*(?!vec3\\b)\\w+\\s*\\([^;]*\\bt\\b"))),
                    step("Animate the hues", "Add time to what you pass in, and the colors cycle around.",
                            "vec3 color = palette(t + time * 0.05) * 1.5;", uses("time")),
                    step("Tone map the result", "Return 1.0 - exp(-color) instead of the raw color and watch the clipping go away.",
                            "return 1.0 - exp(-color * 1.5);", has("exp\\s*\\(\\s*-")),
                    step("Brighten the blends with a square root", "The square root of a color lifts the darks, which stops the middle of a gradient looking muddy.",
                            "color = sqrt(color);", has("sqrt\\s*\\(\\s*color")),
                    step("Pick the exposure in one place", "Put the exposure in its own variable, so one number tunes the whole sky.",
                            "float exposure = 1.5;\nreturn 1.0 - exp(-color * exposure);", has("\\bexposure\\b"))));
}
