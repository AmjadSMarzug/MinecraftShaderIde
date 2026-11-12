package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonSun {
    private LessonSun() {
    }

    public static final Lesson LESSON = new Lesson("lesson_sun", "A sun", "dot, pow and glow", 8, """
                    <p><code>dot(a, b)</code> of two directions tells you how much they agree: 1 when they point the same way,
                    0 when they're at a right angle, -1 when they're opposite. Take the dot of <code>dir</code> with the
                    direction of the sun and you get a value that's 1 right on the sun and falls off around it.</p>
                    <p>That falloff is far too wide to look like a sun, so you sharpen it with <code>pow</code>. Raising a number
                    a bit under 1 to a big power crushes it toward 0, and only the pixels almost exactly on the sun survive.
                    A big power gives a small disc and a small power gives a wide glow.</p>
                    <p>Light adds up, so suns, glows and halos are usually added on top of the sky with <code>+=</code>.</p>
                    <h2>Directions must have length 1</h2>
                    <p><code>dot</code> only means "how much two directions agree" when both have length 1, so the sun's direction goes through <code>normalize</code> first. In the preview, <code>-z</code> is straight ahead and <code>y</code> is up, so
                    <code>vec3(0.0, 0.3, -1.0)</code> is ahead and a little above the horizon.</p>
                    <h2>How big is the sun?</h2>
                    <p>The power decides the size. <code>pow(sun, 500.0)</code> is still half bright about 3 degrees from the center, which
                    reads as a big soft sun. The real sun is half a degree across, which would be a power near 20000. For a disc with a
                    hard edge instead of a soft one, use <code>smoothstep(0.9995, 0.9998, sun)</code>.</p>
                    <h2>Light stacks up</h2>
                    <p>A convincing sun is several layers added together: a small bright disc, a halo around it and a wide warm glow. Each
                    layer is dimmer and wider than the last. Adding them can push colors past 1.0, which lesson 7 fixes with tone
                    mapping.</p>
                    <h2>A sky that reacts</h2>
                    <p><code>sunDir.y</code> says how high the sun is, so the rest of the sky can follow it, getting darker and warmer as the sun goes down, with stars once it's gone. Mixing the sky color toward orange by how low the sun is makes a simple sunset in one line.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Behind you, <code>dot</code> is negative, and <code>pow</code> of a negative number is undefined, which shows up as
                    black or flickering pixels. Keeping it above zero with <code>max</code> prevents that.</li>
                    <li>Forgetting <code>normalize</code> makes the sun the wrong size, or missing completely.</li>
                    </ul>
<h2>Working through the tasks</h2>
                    <p>The sun is built in layers: the measure of closeness, then a tight disc, then two softer glows, then the color of the
                    sky around it. Once it works, the last tasks move it through the sky and darken the world when it sets. The new tasks add
                    a moon on the far side, and a sun with a hard edge instead of a soft one.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Swap the soft disc for a hard edged one with smoothstep.</li>
                    <li>Tint the sun itself red when it's close to the horizon.</li>
                    </ul>
                    """, """
                    // a sun
                    // dot(a, b) of two directions is 1 when they point the same way and gets smaller as they turn apart.
                    // a sun is just the pixels whose dir is very close to the sun's direction

                    vec3 sky(vec3 dir, float time) {
                        vec3 sunDir = normalize(vec3(0.0, 0.3, -1.0));
                        vec3 color = mix(vec3(0.9, 0.5, 0.3), vec3(0.1, 0.15, 0.4), max(dir.y, 0.0));
                        return color;
                    }
                    """, List.of(
                    step("Measure how close each pixel is to the sun", "dot can go negative on the far side of the sky, so wrap it in max(..., 0.0).",
                            "float sun = max(dot(dir, sunDir), 0.0);", has("(?i)dot\\s*\\([^;]*(\\bdir\\b[^;]*sun|sun[^;]*\\bdir\\b)")),
                    step("Turn it into a small disc with pow", "pow(sun, 500.0) is almost 0 everywhere except right on the sun. Add it to color.",
                            "color += vec3(1.0, 0.95, 0.85) * pow(sun, 500.0);", has("pow\\s*\\(")),
                    step("Add a soft glow around it", "A second pow with a small power spreads out wide. Keep it dim so it doesn't wash the sky out.",
                            "color += vec3(1.0, 0.6, 0.3) * pow(sun, 8.0) * 0.5;", count("pow\\s*\\(", 2)),
                    step("Warm up the sky near the sun", "A third, even wider glow tinted orange makes the air around the sun look lit.",
                            "color += vec3(1.0, 0.5, 0.2) * pow(sun, 2.0) * 0.25;", count("pow\\s*\\(", 3)),
                    step("Let it rise and set", "Put time into sunDir, so its height swings up and down.",
                            "vec3 sunDir = normalize(vec3(0.0, sin(time * 0.2) * 0.4, -1.0));", has("(?i)sun\\w*\\s*=[^;]*\\btime\\b")),
                    step("Darken the sky when the sun is low", "sunDir.y tells you how high the sun is. Scale the sky color by it on the line right after color is made, before any of the sun is added, so the sun itself stays bright.",
                            "vec3 color = mix(vec3(0.9, 0.5, 0.3), vec3(0.1, 0.15, 0.4), max(dir.y, 0.0));\ncolor *= 0.25 + 0.75 * clamp(sunDir.y * 3.0, 0.0, 1.0);", has("(?i)sun\\w*\\.y")),
                    step("Add a moon on the far side", "The opposite of a direction is its negative, so -sunDir points the other way across the sky. Make the moon smaller and pale blue.",
                            "vec3 moonDir = -sunDir;\nfloat moon = pow(max(dot(dir, moonDir), 0.0), 800.0);\ncolor += vec3(0.7, 0.8, 1.0) * moon;", has("-\\s*sunDir")),
                    step("Give the sun a hard edge", "smoothstep between two numbers very close to 1 turns the soft disc into a sharp-edged one, like the real sun.",
                            "float disc = smoothstep(0.9995, 0.9998, sun);\ncolor += vec3(1.0, 0.95, 0.8) * disc;", has("smoothstep\\s*\\(\\s*0\\.999"))));
}
