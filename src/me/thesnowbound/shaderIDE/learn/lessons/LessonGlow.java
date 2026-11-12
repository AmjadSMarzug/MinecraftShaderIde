package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonGlow {
    private LessonGlow() {
    }

    public static final Lesson LESSON = new Lesson("lesson_glow", "Glow and see-through", "light that isn't a surface", 10, """
                    <p>Not everything has a surface. Plasma, magic and energy are light floating in the air. Instead of stopping
                    when the ray hits something, you walk the ray in small steps and add up how much light there is at each
                    point. That's <b>volumetric glow</b>.</p>
                    <p><code>exp(-distance * k)</code> is the go to shape for it: 1 right at the source and fading smoothly away.
                    A big <code>k</code> gives a tight hot core, a small one a wide haze.</p>
                    <p>Glow doesn't block the world behind it, so it needs an alpha. A simple one is the brightest color channel,
                    and dividing the color by that alpha keeps it from going dim when it's blended in.</p>
                    <h2>Walking through a volume</h2>
                    <p>With a surface you stop at the first hit. With glow there's nothing to hit, so the ray takes fixed steps through the
                    whole box and adds a little light at each one. 90 steps of 0.04 cover 3.6 units, just enough to cross the box. Smaller
                    steps give finer detail and cost more.</p>
                    <h2>Shapes of glow</h2>
                    <table>
                    <tr><td><code>exp(-d * k)</code></td><td>smooth falloff, the usual choice</td></tr>
                    <tr><td><code>1.0 / (1.0 + d * d * k)</code></td><td>a bright core with a long tail, like real light</td></tr>
                    <tr><td>noise times either</td><td>broken up glow for plasma, smoke and fire</td></tr>
                    </table>
                    <h2>Glow that hides the background</h2>
                    <p>Pure glow only adds. Real smoke also hides what's behind it. Keep a running <code>transmittance</code> that starts
                    at 1 and shrinks with every step, <code>transmittance *= exp(-density * step)</code>, and multiply the light you add
                    by it. That makes thick parts look solid and lets clouds and fire have a shape.</p>
                    <h2>Scale by the step</h2>
                    <p>Multiply what you add at each step by the step size. Then changing the step size changes the detail but not the
                    brightness. Without it, halving the step doubles the glow.</p>
                    <h2>Hiding the rings</h2>
                    <p>Fixed steps leave rings where the samples line up. Start each ray at a slightly different spot, by adding a random
                    fraction of a step to <code>t</code> from a hash of <code>gl_FragCoord.xy</code>, and the rings turn into fine grain
                    that's much harder to see.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Too few steps make the glow look like stacked slices.</li>
                    <li>Forgetting to tone map turns the core into a flat white blob.</li>
                    </ul>
<h2>Working through the tasks</h2>
                    <p>Glow is built by adding light at many points along the ray. The first tasks add a core, a ring and a pulse, then
                    tone map the result. The new tasks add a long soft halo, and a fine grain so the glow doesn't show bands.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Plasma: multiply the core's glow by a moving noise.</li>
                    <li>Smoke: add transmittance and use a gray color.</li>
                    </ul>
                    """, """
                    // glow and see-through
                    // this walks the ray in small steps through the box. right now nothing adds any light

                    vec4 object(vec3 origin, vec3 dir, float time) {
                        vec3 glow = vec3(0.0);
                        float t = 0.0;
                        for (int i = 0; i < 90; i++) {
                            vec3 p = origin + dir * t;
                            t += 0.04;
                            if (t > 3.5) break;
                        }
                        return vec4(glow, 0.0);
                    }
                    """, List.of(
                    step("Add a glowing core", "Inside the loop, add light that fades with the distance from the middle.",
                            "glow += vec3(1.0, 0.6, 0.2) * exp(-length(p) * 8.0) * 0.05;", has("glow\\s*\\+=").and(has("exp\\s*\\("))),
                    step("Turn the glow into alpha", "Use the brightest channel as alpha and divide the color by it.",
                            "float alpha = clamp(max(glow.r, max(glow.g, glow.b)), 0.0, 1.0);\nreturn vec4(glow / max(alpha, 0.001), alpha);",
                            lacks("return vec4(glow, 0.0)").and(has("max\\s*\\("))),
                    step("Add a ring of light", "abs(length(p) - radius) is the distance to a sphere's shell, so its glow is a ring.",
                            "glow += vec3(0.3, 0.6, 1.0) * exp(-abs(length(p) - 0.6) * 30.0) * 0.03;", has("abs\\s*\\(\\s*length")),
                    step("Make it pulse", "Make a pulse from sin(time) and use it to grow and shrink the core. This replaces the core line in the loop.",
                            "float pulse = 0.8 + 0.2 * sin(time * 3.0);\nglow += vec3(1.0, 0.6, 0.2) * exp(-length(p) * 8.0 / pulse) * 0.05;", uses("time")),
                    step("Tone map before returning", "1.0 - exp(-glow) keeps the core from clipping into a flat white blob. Put it after the loop, before the alpha is worked out.",
                            "glow = 1.0 - exp(-glow * 1.5);\nfloat alpha = clamp(max(glow.r, max(glow.g, glow.b)), 0.0, 1.0);", has("1\\.0\\s*-\\s*exp\\s*\\(")),
                    step("Add a faint halo", "1.0 / (1.0 + d * d) falls off more slowly than an exponential, so it leaves a long soft tail around the core.",
                            "glow += vec3(0.5, 0.2, 0.8) * (1.0 / (1.0 + dot(p, p) * 20.0)) * 0.01;", has("1\\.0\\s*/\\s*\\(\\s*1\\.0\\s*\\+")),
                    step("Break the glow up with grain", "gl_FragCoord is the pixel's position on the screen. A hash of it changes every pixel, so the glow gets a fine speckle instead of smooth bands.",
                            "float grain = fract(sin(dot(gl_FragCoord.xy, vec2(12.9898, 78.233))) * 43758.5453);\nglow *= 0.9 + 0.1 * grain;", has("gl_FragCoord"))));
}
