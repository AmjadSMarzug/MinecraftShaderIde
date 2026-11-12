package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonSurface {
    private LessonSurface() {
    }

    public static final Lesson LESSON = new Lesson("lesson_surface", "Your first surface", "uv, grids, holes and edge glow", 10, """
                    <p>A surface paints the faces of the item itself, so the item becomes a screen, a sign, a portal or a wall.
                    Instead of a direction like a sky, every pixel gets <code>uv</code>: where it sits on the face, from 0 to 1
                    across and 0 to 1 up, like coordinates on a sheet of paper.</p>
                    <p>Patterns come from repeating uv. <code>fract(uv * 8.0)</code> counts from 0 to 1 eight times across the
                    face, so anything you draw with it repeats in an 8 by 8 grid.</p>
                    <p>Alpha cuts holes: return 0 and that pixel is gone, so a square panel can become a round portal.
                    The <code>#pragma vessel shape</code> line picks the item's shape: <code>cube</code>, <code>panel</code>,
                    <code>pillar</code> or <code>floor</code>.</p>
                    <h2>uv or pos?</h2>
                    <p><code>uv</code> starts over on every face, so a pattern drawn with it repeats the same way on all six sides.
                    <code>pos</code> is the point's place in the item's box, which carries on across the corners. Use uv for screens,
                    signs and anything that belongs on one face. Use pos for patterns that should wrap round the whole item without
                    seams.</p>
                    <h2>Drawing with distances</h2>
                    <p>Flat shapes work just like the 3D ones in the object lessons, only in 2D:</p>
                    <table>
                    <tr><td>circle</td><td><code>length(uv - 0.5) - r</code></td></tr>
                    <tr><td>ring</td><td><code>abs(length(uv - 0.5) - r) - thickness</code></td></tr>
                    <tr><td>box</td><td><code>max(abs(uv.x - 0.5), abs(uv.y - 0.5)) - size</code></td></tr>
                    </table>
                    <p>Turn a distance into a shape with <code>smoothstep</code>: negative inside, positive outside.</p>
                    <h2>Smooth edges</h2>
                    <p><code>step</code> makes jagged pixel stairs. <code>fwidth(d)</code> tells you how much d changes from one pixel to
                    the next, so <code>smoothstep(w, -w, d)</code> with <code>w = fwidth(d)</code> gives an edge exactly one pixel soft at
                    any distance from the camera.</p>
                    <h2>Fake depth</h2>
                    <p>Look something up at <code>pos + view * depth</code> and it seems to sit behind the face. Stack a few at different
                    depths and the item looks hollow, like a window into a room.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>A pattern flipped on some faces: that face's uv runs the other way, use pos for it instead.</li>
                    <li>On panel and floor shapes the thin edges are skipped, so don't put anything important there.</li>
                    </ul>
<h2>Working through the tasks</h2>
                    <p>A surface is a flat picture on the face of the item, drawn from <code>uv</code>. The first tasks draw a grid, scroll
                    it, cut the face into a circle, add an edge glow and make it see-through. The new tasks add ripples that spread from the
                    middle and a frame around the edge of the face.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>A screen with moving scanlines.</li>
                    <li>A swirl for a portal using <code>atan(uv.y - 0.5, uv.x - 0.5)</code>, the angle around the middle.</li>
                    </ul>
                    """, """
                    // your first surface
                    // uv goes 0 to 1 across each face of the item. right now it's shown as red and green so you can see it
                    #pragma vessel shape panel

                    vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
                        vec3 color = vec3(uv, 0.0);
                        return vec4(color, 1.0);
                    }
                    """, List.of(
                    step("Draw a grid", "fract repeats uv, and the distance from the middle of each cell is biggest at its border. step turns the border into a line.",
                            "vec2 cell = abs(fract(uv * 8.0) - 0.5);\nfloat line = step(0.45, max(cell.x, cell.y));\nvec3 color = mix(vec3(0.02, 0.05, 0.1), vec3(0.3, 0.9, 1.0), line);\nreturn vec4(color, 1.0);",
                            has("fract\\s*\\([^;]*\\buv\\b")),
                    step("Scroll it", "Add time to uv before the fract and the whole grid slides.",
                            "vec2 cell = abs(fract((uv + vec2(0.0, time * 0.2)) * 8.0) - 0.5);", uses("time")),
                    step("Cut it into a circle", "length(uv - 0.5) is the distance from the middle of the face. Return alpha 0 when it's past 0.5 and the corners disappear. Put it first in the function.",
                            "if (length(uv - 0.5) > 0.5) return vec4(0.0);", has("length\\s*\\(\\s*uv\\s*-")),
                    step("Glow at the edges", "dot(normal, view) is near 1 when you look straight at the face and near 0 when you see it side on. One minus that lights up the edges as you turn it.",
                            "float rim = pow(1.0 - abs(dot(normal, view)), 2.0);\ncolor += vec3(0.3, 0.9, 1.0) * rim;\nreturn vec4(color, 1.0);",
                            has("dot\\s*\\(\\s*(normal\\s*,\\s*view|view\\s*,\\s*normal)")),
                    step("Make it see-through", "Return an alpha under 1 and the world shows through, like a hologram.",
                            "return vec4(color, 0.75);", has("return\\s+vec4\\s*\\([^;]*,\\s*0?\\.\\d+\\s*\\)\\s*;")),
                    step("Ripples from the middle", "sin of the distance from the middle, minus time, sends rings outward. Mix them into the color as a faint pattern.",
                            "float ripple = 0.5 + 0.5 * sin(length(uv - 0.5) * 40.0 - time * 4.0);", has("sin\\s*\\(\\s*length\\s*\\(\\s*uv")),
                    step("Frame the face", "min(uv.x, 1.0 - uv.x) is small near the left and right edges, so the smallest of all four is small near any edge. A thin line there frames the panel.",
                            "float frame = smoothstep(0.02, 0.0, min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y)));", has("min\\s*\\(\\s*min\\s*\\(\\s*uv\\.x"))));
}
