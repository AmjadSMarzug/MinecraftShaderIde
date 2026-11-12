package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonStars {
    private LessonStars() {
    }

    public static final Lesson LESSON = new Lesson("lesson_stars", "Stars", "cells, floor and fract", 10, """
                    <p>Stars are a different kind of random: a few sharp points scattered across the sky. The trick is to cut
                    space into <b>cells</b>. <code>floor(dir * 80.0)</code> gives every little patch of sky its own whole number
                    id, and <code>fract(dir * 80.0)</code> tells you where inside that patch the pixel sits.</p>
                    <p>Run the id through a <b>hash</b>, a function that turns any input into a random looking number between
                    0 and 1, and only light the cells whose number lands above 0.97. About 3 cells in 100 get a star.</p>
                    <p>The same hash is handy for anything that should differ star by star: brightness, color, twinkle speed.</p>
                    <h2>Why cells?</h2>
                    <p>Every pixel runs on its own and can't see the others. Checking a list of a thousand stars for every pixel would be
                    far too slow. Cells solve it: each pixel works out which cell it's in, asks that one cell whether it has a star, and
                    is done.</p>
                    <h2>Size and density</h2>
                    <p>The number in <code>floor(dir * 80.0)</code> sets how big the cells are. A bigger number means smaller cells, so
                    smaller and more numerous stars. The threshold sets how many cells get a star: 0.97 lights about 3 in 100, 0.99
                    about 1 in 100.</p>
                    <h2>Hiding the grid</h2>
                    <p>A star always sitting in the middle of its cell lines up in rows you can spot. Move it to a random spot inside the
                    cell with three more hashes:</p>
                    <pre>vec3 jitter = vec3(hash(cell + 1.0),
                        hash(cell + 2.0), hash(cell + 3.0)) - 0.5;</pre>
                    <p>Then measure the distance from the cell's middle plus <code>jitter * 0.6</code> instead of from the middle
                    itself.</p>
                    <h2>One hash per property</h2>
                    <p>Use a different offset for each thing a star needs: whether it exists, how bright it is, its color, how fast it
                    twinkles. Reusing the same hash links them, so for example only the brightest cells would ever get stars.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Stars that look like a grid: add jitter.</li>
                    <li>Stars that blink together: each one needs its own phase from the hash.</li>
                    </ul>
<h2>Working through the tasks</h2>
                    <p>Each star is decided by its own cell, so the tasks are about changing that decision: which cells get a star, how
                    big it is, what color, and whether it twinkles. The new tasks fade the stars toward the horizon and add a rare set of
                    big, bright ones.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>A milky way: multiply the chance of a star by a band, like <code>exp(-abs(dir.z) * 4.0)</code>.</li>
                    <li>Hide the stars wherever a sun from lesson 3 would be up.</li>
                    </ul>
                    """, """
                    // stars
                    // floor() cuts space into numbered cells and fract() tells you where you are inside one.
                    // give every cell a random number and only some of them get a star

                    float hash(vec3 p) {
                        return fract(sin(dot(p, vec3(12.9898, 78.233, 37.719))) * 43758.5453);
                    }

                    vec3 sky(vec3 dir, float time) {
                        vec3 night = vec3(0.01, 0.01, 0.04);
                        return night;
                    }
                    """, List.of(
                    step("Split the sky into cells", "floor gives every patch of sky its own id.", "vec3 cell = floor(dir * 80.0);", has("floor\\s*\\(")),
                    step("Pick a few cells to hold a star", "step(0.97, x) is 1 only when x is above 0.97. Add the star onto night in the return.",
                            "vec3 cell = floor(dir * 80.0);\nfloat star = step(0.97, hash(cell));\nreturn night + vec3(star);", has("step\\s*\\(|>\\s*0\\.9")),
                    step("Make them round", "Distance from the middle of the cell, run through smoothstep, gives a soft dot instead of a square. It has to change star before the return uses it.",
                            "float star = step(0.97, hash(cell));\nstar *= 1.0 - smoothstep(0.0, 0.5, length(fract(dir * 80.0) - 0.5));\nreturn night + vec3(star);", has("fract\\s*\\(").and(has("length\\s*\\("))),
                    step("Make them twinkle", "sin with a different speed per star, picked by the hash, so they don't blink together. Put it on the line before the return.",
                            "star *= 0.6 + 0.4 * sin(time * 3.0 + hash(cell) * 50.0);\nreturn night + vec3(star);", has("sin\\s*\\(").and(uses("time"))),
                    step("Give each star its own color", "Hash the cell again with an offset and use it to pick between blue white and warm white, then multiply the star by that tint in the return.",
                            "vec3 tint = mix(vec3(0.7, 0.8, 1.0), vec3(1.0, 0.85, 0.7), hash(cell + 7.0));\nreturn night + tint * star;", count("\\bhash\\s*\\(", 3)),
                    step("Add a second, finer layer", "Do it all again at a higher cell count and half the brightness for faint background stars, and add them in the return too.",
                            "vec3 fine = floor(dir * 200.0);\nfloat faint = step(0.98, hash(fine)) * (1.0 - smoothstep(0.0, 0.5, length(fract(dir * 200.0) - 0.5)));\nreturn night + tint * star + vec3(faint * 0.5);", count("floor\\s*\\(", 2)),
                    step("Keep stars off the horizon", "Multiply the star by a smoothstep on dir.y, so stars fade out as you look down toward the ground.",
                            "star *= smoothstep(0.0, 0.3, dir.y);", has("star\\s*\\*=\\s*smoothstep")),
                    step("Make a few stars big and bright", "Only about one cell in a thousand gets this rare, bigger star. A higher threshold means fewer cells qualify.",
                            "float big = step(0.999, hash(cell + 11.0)) * 3.0;", has("step\\s*\\(\\s*0\\.99[89]"))));
}
