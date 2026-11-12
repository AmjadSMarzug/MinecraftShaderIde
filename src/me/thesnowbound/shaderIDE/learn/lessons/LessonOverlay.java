package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonOverlay {
    private LessonOverlay() {
    }

    public static final Lesson LESSON = new Lesson("lesson_overlay", "Your first overlay", "vignettes, pulses and the aspect trap", 10, """
                    <p>An overlay is laid over everything the player sees, like rain on the lens or the red edges you get at low
                    health. <code>screen</code> is where the pixel is on the window, 0,0 in the bottom left to 1,1 in the top right.</p>
                    <p>Here alpha is how much of the world you cover. 0 leaves the view clear,
                    1 hides it completely. Keep the middle of the screen low or the player can't play.</p>
                    <p>One trap: <code>screen</code> is squashed into a square, but windows are wide, so a circle measured with it
                    comes out as an oval. Stretch x by about 1.8 before measuring and circles stay round.</p>
                    <p><code>dir</code> is the direction the pixel looks into the world. Things placed with <code>screen</code>
                    stick to your view, things placed with <code>dir</code> stay put when you turn your head.</p>
                    <h2>Two ways to place things</h2>
                    <p>Anything built from <code>screen</code> sticks to the glass, like rain on goggles, and moves with your head. Anything
                    built from <code>dir</code> stays in the world: turn around and it's behind you. Mixing both is how a domain effect
                    can tint your view and also have embers hanging in the air.</p>
                    <h2>The aspect trap, exactly</h2>
                    <p>A normal window is about 16 by 9, so screen x covers 1.78 times as much space as screen y. Multiplying x by about
                    1.8 before measuring makes circles round on most screens. It's not exact on unusual monitors, but it's close enough
                    for soft effects.</p>
                    <h2>Keep it playable</h2>
                    <ul>
                    <li>Keep alpha under about 0.15 in the middle of the screen, where the player aims.</li>
                    <li>Edges can go much higher, up to around 0.8, without getting in the way.</li>
                    <li>Avoid flashing faster than three times a second. Fast flashing is uncomfortable and can be dangerous for people
                    with photosensitive epilepsy.</li>
                    </ul>
                    <h2>Textures on the glass</h2>
                    <p>Noise and cells from the sky lessons work on screen coordinates too: cells make raindrops, noise makes frost and
                    grime. Scale <code>screen</code> up, around 10 to 30, for detail that matches the size of the window.</p>
                    <h2>Fading in</h2>
                    <p><code>clamp(time / 2.0, 0.0, 1.0)</code> grows from 0 to 1 over two seconds. Multiply your alpha by it so the
                    overlay eases in instead of snapping on.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Returning a color with alpha 1 everywhere covers the whole world.</li>
                    <li>Forgetting the aspect fix stretches every circle into an oval.</li>
                    </ul>
<h2>Working through the tasks</h2>
                    <p>An overlay covers the screen. The first tasks darken the edges, keep the shape round on a wide window, pulse it,
                    tint it and pin a spot to the world. The new tasks fade the whole effect in when it starts and add raindrops on the
                    glass, made from cells on <code>screen</code>.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Snow drifting past, placed with <code>dir</code> so it stays in the world.</li>
                    <li>Raindrops on the glass, made from cells on <code>screen</code>.</li>
                    </ul>
                    """, """
                    // your first overlay
                    // screen goes 0,0 bottom left to 1,1 top right. alpha is how much of the world you cover,
                    // so this one covers nothing yet

                    vec4 overlay(vec2 screen, vec3 dir, float time) {
                        return vec4(0.0);
                    }
                    """, List.of(
                    step("Darken the edges", "length(screen - 0.5) is the distance from the middle. smoothstep turns it into an alpha that's 0 in the middle and rises toward the corners.",
                            "float edge = length(screen - 0.5);\nreturn vec4(0.0, 0.0, 0.0, smoothstep(0.3, 0.8, edge));", has("length\\s*\\(\\s*\\(?\\s*screen")),
                    step("Keep it round", "Stretch the x of the position by 1.8 before measuring. The dark ring stops being an oval on a wide window.",
                            "vec2 p = (screen - 0.5) * vec2(1.8, 1.0);\nfloat edge = length(p);", has("\\*\\s*vec2\\s*\\(\\s*\\d*\\.\\d+\\s*,\\s*1\\.0\\s*\\)|\\.x\\s*\\*=\\s*\\d")),
                    step("Pulse like a heartbeat", "pow of a sin wave with a big power makes short sharp beats with rests between. Multiply the alpha by it.",
                            "float beat = pow(0.5 + 0.5 * sin(time * 6.0), 8.0);\nreturn vec4(0.0, 0.0, 0.0, smoothstep(0.6, 1.2, edge) * (0.6 + 0.4 * beat));", has("sin\\s*\\(").and(uses("time"))),
                    step("Tint it red", "Give the overlay a color instead of black.",
                            "return vec4(0.6, 0.0, 0.05, smoothstep(0.6, 1.2, edge) * (0.6 + 0.4 * beat));", has("return\\s+vec4\\s*\\(\\s*(0?\\.0*[1-9]|[1-9])")),
                    step("Pin something to the world", "dot(dir, d) is close to 1 when you look toward d. A glowing spot made from it stays in one place in the world while the vignette follows your view. Add it to the alpha.",
                            "float spot = pow(max(dot(dir, normalize(vec3(0.0, 0.2, -1.0))), 0.0), 300.0);\nfloat alpha = smoothstep(0.6, 1.2, edge) * (0.6 + 0.4 * beat) + spot;\nreturn vec4(0.6, 0.0, 0.05, alpha);",
                            has("dot\\s*\\(\\s*dir\\b|dot\\s*\\([^;]*,\\s*dir\\s*\\)")),
                    step("Fade the whole effect in", "clamp(time / 2.0, 0.0, 1.0) grows from 0 to 1 over two seconds. Multiply the alpha by it so the effect eases in.",
                            "float fade = clamp(time / 2.0, 0.0, 1.0);", has("clamp\\s*\\(\\s*time")),
                    step("Raindrops from cells on the glass", "floor(screen * 12.0) cuts the window into cells. A hash of each cell decides whether a drop sits in it.",
                            "vec2 cell = floor(screen * 12.0);\nfloat drop = step(0.9, fract(sin(dot(cell, vec2(12.9898, 78.233))) * 43758.5453));", has("floor\\s*\\(\\s*screen"))));
}
