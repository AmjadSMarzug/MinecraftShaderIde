package me.thesnowbound.shaderIDE.learn;

import me.thesnowbound.shaderIDE.build.Skript;
import me.thesnowbound.shaderIDE.preview.Preview;
import me.thesnowbound.shaderIDE.project.Project;

import java.util.List;

// the reference pages in the Learn section, each with a practice list at the end
public final class Guides {
    private Guides() {
    }

    public static final List<Guide> GUIDES = List.of(
            new Guide("Getting started", "a tour of the window", """
                    <h1>Getting started</h1>
                    <p class="lead">Everything you need to go from an empty window to a pack you can load in Minecraft.</p>
                    <h2>The window</h2>
                    <table>
                    <tr><td><b>Files</b></td><td>the project on the left. Right click a file for its options, like effect settings, duplicate and copy give command.</td></tr>
                    <tr><td><b>Editor</b></td><td>the middle. Every shader is one tab. Lines with a vec3 color show a swatch next to the line number.</td></tr>
                    <tr><td><b>Preview</b></td><td>the right. It recompiles as you type. Drag to look around. The fps and resolution show under it.</td></tr>
                    <tr><td><b>Console</b></td><td>the bottom. Problems lists compile errors, click one to jump to the line. Log shows build output.</td></tr>
                    </table>
                    <h2>Your first pack in four steps</h2>
                    <p><b>1.</b> File &gt; New Project and answer the questions. <b>2.</b> Ctrl+N and pick an example from the gallery.
                    <b>3.</b> Change something and watch the preview. <b>4.</b> Ctrl+B builds the pack into the project's build folder.</p>
                    <h2>Shortcuts worth knowing</h2>
                    <table>
                    <tr><td><code>Ctrl+Shift+P</code></td><td>command palette, type part of any command</td></tr>
                    <tr><td><code>Ctrl+P</code></td><td>jump to a file by name</td></tr>
                    <tr><td><code>Ctrl+N</code></td><td>new effect from the gallery</td></tr>
                    <tr><td><code>Ctrl+B</code></td><td>build the pack</td></tr>
                    <tr><td><code>Ctrl+,</code></td><td>project settings</td></tr>
                    <tr><td><code>Ctrl+Shift+E / V, Ctrl+J</code></td><td>show or hide files, preview and console</td></tr>
                    <tr><td><code>F1</code></td><td>this window</td></tr>
                    </table>
                    <h2>Practice</h2>
                    <ol><li>Open a new project, then pick the Glow lesson's example from the gallery and build the pack with Ctrl+B. Find the .zip in the build folder.</li><li>Turn the preview off with Ctrl+Shift+V, then on again, and check the window still shows the same effect.</li><li>Use the command palette to find Save All, run it, then close the window and reopen the project.</li></ol>
                    """),
            new Guide("How packs work", "alpha codes, items and versions", """
                    <h1>How packs work</h1>
                    <p class="lead">What actually happens when you press Build.</p>
                    <h2>One shader for everything</h2>
                    <p>Minecraft draws items with a core shader. The build takes every effect in your project, compiles them into
                    that one shader, and adds a small dispatcher in front: for each pixel of an item it checks the texture's
                    alpha, and if it matches one of your effects it runs that effect instead of drawing the texture.</p>
                    <h2>Alpha codes</h2>
                    <p>Every effect has an alpha code from 1 to 254, set in Effect Settings. The build makes an item for each
                    effect whose texture is filled with exactly that alpha, so the item and the effect find each other. Two
                    effects can't share a code. Normal items almost never use these exact values, so they're left alone.</p>
                    <div class="tip">Object textures also store each pixel's position in their colors. That's how an object effect
                    works out where the item's box is on screen.</div>
                    <h2>Versions</h2>
                    <p>The shader system changed several times between 1.21 and 26.3, so the pack carries a copy for each era
                    in overlays: 1.21 to 1.21.5, 1.21.6 to 1.21.11, 26.1, 26.2 and 26.3, plus a fix for 1.21 and 1.21.1. The
                    pack metadata lists formats 34 to 99 the way big multi version packs do, so the game picks the right copy.</p>
                    <h2>Checking and obfuscating</h2>
                    <p>With Check Shaders on, every copy is compiled with glslang before the zip is made, and 26.3 is also
                    compiled to Vulkan SPIR-V. Obfuscate renames every function and variable to random letters and adds decoy
                    code, so the shipped pack is hard to read. Your project files stay as you wrote them.</p>
                    <h2>Practice</h2>
                    <ol><li>Open an effect with <code>#pragma vessel shape cube</code> and change it to <code>panel</code>. Look at how the item changes in the preview.</li><li>Write a number between 0 and 255 as the effect's alpha in the project settings. Use the value to explain which effect wins when two share one.</li><li>Build the pack with the Versions setting on, then look inside the zip and find one file per Minecraft version.</li></ol>
                    """),
            new Guide("The sky function", "directions, projections and time", """
                    <h1>The sky function</h1>
                    <pre>vec3 sky(vec3 dir, float time)</pre>
                    <p class="lead">Runs once per pixel of the sky. Gets a direction, gives back a color.</p>
                    <h2>The direction</h2>
                    <p><code>dir</code> has length 1. <code>dir.y</code> is up, <code>dir.x</code> is to the side, and
                    <code>-dir.z</code> is straight ahead in the preview. In game the sky doesn't turn with the player, so
                    "ahead" is a fixed compass direction.</p>
                    <h2>Ways to lay things out</h2>
                    <table>
                    <tr><td><code>dir.y</code></td><td>height above the horizon, for gradients</td></tr>
                    <tr><td><code>dot(dir, d)</code></td><td>closeness to a direction, for suns, moons and spotlights</td></tr>
                    <tr><td><code>dir.xz / dir.y</code></td><td>a flat ceiling, for cloud layers and grids that stretch to the horizon</td></tr>
                    <tr><td><code>atan(dir.x, -dir.z)</code></td><td>the compass angle around you, for mountain lines and horizon bands</td></tr>
                    <tr><td><code>dir.xz / (1.0 + dir.y)</code></td><td>the dome flattened into a disk with the zenith in the middle, for patterns overhead</td></tr>
                    <tr><td><code>floor(dir * n)</code></td><td>cells for stars, sparks and particles</td></tr>
                    </table>
                    <div class="tip">The angle from atan jumps from +pi to -pi right behind you, which leaves a seam in any noise
                    fed with it. Feed noise <code>vec2(cos(angle), sin(angle))</code> instead and the seam is gone.</div>
                    <h2>Time</h2>
                    <p><code>time</code> counts seconds. In game it comes from the world clock, so it wraps around now and then.
                    Prefer <code>sin(time)</code> and <code>fract(time)</code> to huge multiples of it.</p>
                    <h2>Intro</h2>
                    <p><code>intro</code> goes from 0 to 1 over the first five seconds after the sky is turned on, then stays at 1.
                    Use it for an entrance: cracks that open and fade, a flash, colour bleeding in. The preview replays it
                    every 15 seconds. It needs the generated Skript's sky command, anywhere else it's just 1.</p>
                    <h2>Brightness</h2>
                    <p>Colors clip at 1. When you stack suns, glows and stars, end with <code>return 1.0 - exp(-color * 1.5);</code>
                    so bright parts roll off smoothly instead of flattening into white.</p>
                    <h2>Practice</h2>
                    <ol><li>Return a color that depends only on <code>dir.x</code>, then only on <code>dir.z</code>. What happens to the sky when you turn around?</li><li>Use <code>time</code> to make the horizon move up and down slowly.</li><li>Draw a fixed band of color at <code>dir.y</code> between 0.2 and 0.25 and check it stays put as you look around.</li></ol>
                    """),
            new Guide("Objects and raymarching", "distance functions, shapes and blends", """
                    <h1>Objects and raymarching</h1>
                    <pre>vec4 object(vec3 origin, vec3 dir, float time)</pre>
                    <p class="lead">Draws a 3D thing inside the item's box, which runs from -1 to 1 on every axis.</p>
                    <h2>The loop</h2>
                    <pre>float t = 0.0;
                    for (int i = 0; i &lt; 80; i++) {
                        vec3 p = origin + dir * t;
                        float d = shape(p);
                        if (d &lt; 0.001) return shade(p);
                        t += d;
                        if (t &gt; 3.5) break;
                    }
                    return vec4(0.0);</pre>
                    <p><code>shape(p)</code> returns the distance from p to the surface. Since nothing is closer than that, it's
                    always a safe step. 3.5 is a little over the longest line through the box, so the ray can stop there.</p>
                    <h2>Shapes</h2>
                    <table>
                    <tr><td>sphere</td><td><code>length(p) - r</code></td></tr>
                    <tr><td>box</td><td><code>length(max(abs(p) - size, 0.0))</code></td></tr>
                    <tr><td>rounded box</td><td><code>length(max(abs(p) - size, 0.0)) - radius</code></td></tr>
                    <tr><td>torus</td><td><code>length(vec2(length(p.xz) - R, p.y)) - r</code></td></tr>
                    <tr><td>cylinder</td><td><code>max(length(p.xz) - r, abs(p.y) - h)</code></td></tr>
                    </table>
                    <h2>Combining</h2>
                    <table>
                    <tr><td><code>min(a, b)</code></td><td>both shapes together</td></tr>
                    <tr><td><code>max(a, b)</code></td><td>only where they overlap</td></tr>
                    <tr><td><code>max(a, -b)</code></td><td>a with b carved out</td></tr>
                    <tr><td>smooth min</td><td>a and b melted together, see lesson 8</td></tr>
                    </table>
                    <h2>Moving the space</h2>
                    <p>To move, rotate or copy a shape, change <code>p</code> before measuring. <code>p - offset</code> moves it,
                    a mat2 on two of its axes rotates it, <code>abs(p)</code> mirrors it, and <code>mod(p, s) - s * 0.5</code>
                    repeats it forever.</p>
                    <h2>Alpha</h2>
                    <p>Return 1 for solid, 0 where there's nothing so the world shows through, and in between for glass and glow.
                    For glow, add up light along the ray instead of stopping at a surface, see lesson 10.</p>
                    <h2>Size</h2>
                    <p><code>#pragma vessel scale 3</code> near the top makes the item 3 blocks wide instead of 1. Your code
                    doesn't change, the -1 to 1 box just covers more of the world. 3 is the most an item model allows,
                    and the Skript's place and size commands still scale on top of it.</p>
                    <div class="tip">Objects stay upright to the world even when the item spins, because the box is worked
                    out in world space.</div>
                    <h2>Practice</h2>
                    <ol><li>Change the box size in a distance function and check the object still fits inside the item's box.</li><li>Make the object spin using a rotation of <code>p.xz</code> that depends on <code>time</code>.</li><li>Count how many steps the march took and return that as a grey color. Where does it get darkest?</li></ol>
                    """),
            new Guide("Surfaces", "painting the item's own faces", """
                    <h1>Surfaces</h1>
                    <pre>vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time)</pre>
                    <p class="lead">Paints the faces of the item itself, so the item becomes the screen, the block or the shield.</p>
                    <h2>What you get</h2>
                    <table>
                    <tr><td><code>uv</code></td><td>0 to 1 across the face, the same on every face of the cube</td></tr>
                    <tr><td><code>pos</code></td><td>the same point in the item's -1 to 1 box, handy for patterns that wrap around corners</td></tr>
                    <tr><td><code>normal</code></td><td>the direction the face points, outward</td></tr>
                    <tr><td><code>view</code></td><td>the direction you're looking</td></tr>
                    </table>
                    <h2>Shapes</h2>
                    <p>A surface can ask for the shape of the item it paints with a line near the top of the file:</p>
                    <pre>#pragma vessel shape panel</pre>
                    <table>
                    <tr><td><code>cube</code></td><td>a full block, the default</td></tr>
                    <tr><td><code>panel</code></td><td>a thin standing sheet, for screens, signs, portals and walls</td></tr>
                    <tr><td><code>pillar</code></td><td>tall and narrow, for columns and totems</td></tr>
                    <tr><td><code>floor</code></td><td>a thin tile lying flat, for magic circles, pools and markings</td></tr>
                    </table>
                    <p>Combine a shape with alpha cutouts to get anything flat: the Portal is a panel with everything outside a
                    circle cut away, and the Rune Circle is a floor tile with the same trick.</p>
                    <h2>Tricks</h2>
                    <p><b>Rim light.</b> <code>pow(1.0 - abs(dot(normal, view)), 2.0)</code> is strong at grazing angles and weak
                    face on, which is how the Force Field gets brighter at its edges.</p>
                    <p><b>Fake depth.</b> Sample something at <code>pos + view * depth</code> and it looks like it sits behind the
                    face. Stack a few of those at different depths and the item looks hollow and far deeper than it is.</p>
                    <p><b>Holes.</b> Return alpha 0 to cut a pixel out, or anything between 0 and 1 for glass and holograms.</p>
                    <div class="tip">Put a surface on a thin, flat item and it's a screen or a sign. The Skript's place command
                    with a size works for that, and so does any item display you scale yourself.</div>
                    <h2>Practice</h2>
                    <ol><li>Make a checkerboard: one grid of <code>fract(uv * 4.0)</code>, colored alternately by whether the two cell numbers add up to an even number.</li><li>Use <code>pos</code> instead of <code>uv</code> for a pattern and look at the corners of a cube. Does the pattern wrap?</li><li>Put a hole in a panel with alpha 0 in one corner.</li></ol>
                    """),
            new Guide("Materials", "animating an item's own pixels", """
                    <h1>Materials</h1>
                    <pre>vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time)</pre>
                    <p class="lead">Keeps the item's normal flat sprite and repaints every pixel of it, so a sword can run with lava
                    or a gem can hold a starfield.</p>
                    <h2>What you get</h2>
                    <table>
                    <tr><td><code>texel</code></td><td>the pixel's own colour from the texture, so you can keep the item's shading</td></tr>
                    <tr><td><code>uv</code></td><td>0 to 1 across the sprite, 0,0 is the top left corner</td></tr>
                    <tr><td><code>normal</code></td><td>the direction the item faces, worked out on the GPU</td></tr>
                    <tr><td><code>view</code></td><td>the direction you're looking</td></tr>
                    </table>
                    <h2>Your own texture</h2>
                    <p>Put a png with the same name next to the effect file, like <code>lava_flow.png</code> beside
                    <code>lava_flow.glsl</code>. Every pixel you can see in it runs through the material and see through pixels
                    stay see through. Without one you get a plain gem to try things on. Keep it 16 by 16, bigger textures
                    end up with the effect tiled across them.</p>
                    <h2>Tricks</h2>
                    <p><b>Stay blocky.</b> <code>(floor(uv * 16.0) + 0.5) / 16.0</code> snaps to the sprite's pixels, which makes
                    the effect sit nicely next to vanilla items. Cursed Flame does this.</p>
                    <p><b>Keep the shape.</b> Multiply by <code>texel.r</code> or the texel's brightness so the dark outline and
                    shading of the sprite still show through.</p>
                    <p><b>Depth.</b> Offset the uv by <code>view.xz</code> a little per layer and the layers slide past each other
                    as you turn, like the end portal. Star Cloth works this way.</p>
                    <div class="tip">Materials can't be placed or worn as a sky, use <code>/ns give</code> from the Skript or
                    any command that hands out the item.</div>
                    <h2>Practice</h2>
                    <ol><li>Recolor the texel so the item is entirely blue but keeps its shading. Use the texel's brightness.</li><li>Make the item blink on and off with <code>step(0.5, sin(time * 4.0))</code>.</li><li>Snap the uv to the 16 by 16 grid and check the effect moves in whole pixels.</li></ol>
                    """),
            new Guide("Overlays", "drawing over the player's view", """
                    <h1>Overlays</h1>
                    <pre>vec4 overlay(vec2 screen, vec3 dir, float time)</pre>
                    <p class="lead">Lays colour over everything the player sees: rain on the lens, frost, a low health pulse,
                    the tint of a domain.</p>
                    <h2>What you get</h2>
                    <table>
                    <tr><td><code>screen</code></td><td>0,0 in the bottom left of the window to 1,1 in the top right</td></tr>
                    <tr><td><code>dir</code></td><td>the direction that pixel looks into the world</td></tr>
                    </table>
                    <p>Anything tied to <code>screen</code> sticks to the glass and follows your head. Anything placed with
                    <code>dir</code> stays put in the world when you turn, which is how the embers in Domain Veil hang in the air.</p>
                    <h2>Alpha</h2>
                    <p>Alpha is how much of the world you cover. Keep it low in the middle so the player can still play,
                    and return 0 wherever there's nothing at all.</p>
                    <h2>Aspect</h2>
                    <p><code>screen</code> is squashed into a square, so circles come out stretched on a wide window.
                    Multiply x by about 1.8 before measuring distances to keep them round.</p>
                    <h2>In game</h2>
                    <p>The overlay is an item in the offhand. The hand is drawn after the world, so it covers everything, and
                    nobody else can see it. <code>/ns overlay name</code> puts it on and keeps the old offhand item safe,
                    <code>/ns overlay off</code> gives it back.</p>
                    <h2>Practice</h2>
                    <ol><li>Draw a dot exactly in the middle of the screen with alpha 0.1, then move it toward the edge.</li><li>Make a red flash that lasts half a second every five seconds using <code>mod(time, 5.0)</code>.</li><li>Check your overlay on a wide and a narrow window. Which fix from the aspect section does it need?</li></ol>
                    """),
            new Guide("Noise and randomness", "hash, fbm, warping and cells", """
                    <h1>Noise and randomness</h1>
                    <p class="lead">Shaders have no random function, so you make your own.</p>
                    <h2>Hash</h2>
                    <pre>float hash(vec3 p) {
                        return fract(sin(dot(p, vec3(12.9898, 78.233, 37.719))) * 43758.5453);
                    }</pre>
                    <p>Turns any position into a random looking number from 0 to 1. Same input, same output, which is what you
                    want: a star stays put frame after frame.</p>
                    <h2>Value noise</h2>
                    <p>Hash the corners of a grid and blend between them with a smooth curve. Neighbouring points get similar
                    values, which gives soft blobs instead of static. Every example in the gallery has one you can copy.</p>
                    <h2>Fbm</h2>
                    <p>Add several layers of noise, each at about twice the detail and half the strength. Five layers is plenty
                    for most things. Rotating or offsetting each layer a little hides the grid.</p>
                    <h2>Ridges</h2>
                    <p><code>1.0 - abs(noise * 2.0 - 1.0)</code> turns soft blobs into thin bright lines, which is how the lightning,
                    plasma threads and mountain ridges in the examples are made. Raise it to a power to make the lines thinner.</p>
                    <h2>Warping</h2>
                    <p>Use noise to shift the position you feed into more noise and you get swirls and tendrils. Lesson 5 walks through it.</p>
                    <h2>Cells</h2>
                    <p><code>floor(p)</code> numbers the cells of a grid and <code>fract(p)</code> is the position inside one.
                    Hash the cell number to decide what each cell holds. Stars, rain, embers and petals are all this.</p>
                    <h2>Practice</h2>
                    <ol><li>Write a hash that returns a different number for each whole-number cell, then show the cells as gray squares.</li><li>Build a value noise with octaves and look at 1, 3 and 6 octaves side by side in three rows.</li><li>Use <code>voronoi</code> to draw the edges of cells, where the distance to the nearest point is small.</li></ol>
                    """),
            new Guide("Color and light", "mixing, palettes and tone mapping", """
                    <h1>Color and light</h1>
                    <h2>Two ways to combine</h2>
                    <p><code>mix(a, b, t)</code> blends, like paint. <code>a + b</code> adds, like light. Skies are usually a mixed
                    base with light added on top: suns, glows, stars, lightning.</p>
                    <h2>Glow shapes</h2>
                    <table>
                    <tr><td><code>exp(-d * k)</code></td><td>soft glow, k sets how tight</td></tr>
                    <tr><td><code>pow(x, n)</code></td><td>sharpen a 0 to 1 value, big n for small hot spots</td></tr>
                    <tr><td><code>1.0 - smoothstep(0.0, w, d)</code></td><td>a crisp disc or line w wide</td></tr>
                    </table>
                    <p>A crisp core plus a wide soft halo, added together, reads as bright and hot.</p>
                    <h2>Palettes</h2>
                    <p><code>a + b * cos(6.28318 * (c * t + d))</code> with vec3 a, b, c and d turns one number into a smooth
                    run of colors. Lesson 6 has you write one.</p>
                    <h2>Tone mapping</h2>
                    <p>Anything over 1 clips. <code>1.0 - exp(-color * k)</code> squeezes everything back under 1 while keeping
                    the shape of highlights. Larger k makes everything brighter.</p>
                    <div class="tip">Keep one strong color idea per effect. The examples mostly use a dark base, one main
                    color and one accent.</div>
                    <h2>Practice</h2>
                    <ol><li>Mix two colors in linear space with a square root and compare the middle with a straight mix.</li><li>Build a rainbow from a cosine palette, then move it across the sky with <code>dir.x</code>.</li><li>Compare exposure 0.5, 1.5 and 4.0 on a bright sun and note where the core turns flat white.</li></ol>
                    """),
            new Guide("Animation", "time, cycles and phases", """
                    <h1>Animation</h1>
                    <table>
                    <tr><td><code>sin(time * speed)</code></td><td>smooth back and forth between -1 and 1</td></tr>
                    <tr><td><code>0.5 + 0.5 * sin(...)</code></td><td>the same, but between 0 and 1</td></tr>
                    <tr><td><code>fract(time * speed)</code></td><td>a ramp from 0 to 1 that starts over, for things that repeat</td></tr>
                    <tr><td><code>floor(time * speed)</code></td><td>a counter that ticks up, hash it for a new random choice each cycle</td></tr>
                    <tr><td><code>p + vec3(time, 0, 0)</code></td><td>slide a pattern, for drifting clouds</td></tr>
                    </table>
                    <h2>Out of step</h2>
                    <p>When many things move, give each its own phase: <code>sin(time * 3.0 + hash(cell) * 50.0)</code>. Without
                    that every star blinks at once and it looks mechanical.</p>
                    <h2>Events</h2>
                    <p>For things that happen now and then, like lightning or a hit on a shield, split time into beats
                    with floor, hash each beat to decide if something happens, and use <code>exp(-fract(...) * k)</code> for a
                    sharp flash that fades.</p>
                    <h2>Easing</h2>
                    <p><code>t * t * (3.0 - 2.0 * t)</code> on a 0 to 1 value starts and ends slowly, which makes movement feel
                    natural. That's what smoothstep does inside.</p>
                    <h2>Practice</h2>
                    <ol><li>Make a color cycle through red, green and blue every three seconds with <code>sin</code> offsets of one third of a cycle.</li><li>Stop an animation at a fixed point after five seconds with <code>min(time, 5.0)</code>.</li><li>Make two things move at different speeds and share a phase, so they stay in step every ten seconds.</li></ol>
                    """),
            new Guide("GLSL cheat sheet", "types, swizzles and built ins", """
                    <h1>GLSL cheat sheet</h1>
                    <h2>Types</h2>
                    <table>
                    <tr><td><code>float int bool</code></td><td>one value</td></tr>
                    <tr><td><code>vec2 vec3 vec4</code></td><td>2, 3 or 4 floats</td></tr>
                    <tr><td><code>mat2 mat3</code></td><td>rotations, <code>mat2(c, -s, s, c)</code> rotates a vec2</td></tr>
                    </table>
                    <h2>Swizzles</h2>
                    <p><code>v.x v.y v.z</code> read parts, <code>v.xz</code> makes a vec2 from two of them, <code>v.rgb</code> is the
                    same as <code>v.xyz</code>, and you can write to them: <code>p.xz = rotate * p.xz;</code></p>
                    <h2>Built ins</h2>
                    <table>
                    <tr><td><code>mix(a, b, t)</code></td><td>blend from a to b</td></tr>
                    <tr><td><code>smoothstep(e0, e1, x)</code></td><td>0 below e0, 1 above e1, smooth between. keep e0 smaller than e1</td></tr>
                    <tr><td><code>step(edge, x)</code></td><td>0 below edge, 1 above</td></tr>
                    <tr><td><code>clamp(x, lo, hi)</code></td><td>keep x in range</td></tr>
                    <tr><td><code>fract floor mod</code></td><td>part after the dot, whole number below, wrap around</td></tr>
                    <tr><td><code>abs sign min max</code></td><td>the usual</td></tr>
                    <tr><td><code>dot cross</code></td><td>agreement of two vectors, a vector at right angles to both</td></tr>
                    <tr><td><code>length distance normalize</code></td><td>size, gap between two points, same direction at length 1</td></tr>
                    <tr><td><code>reflect refract</code></td><td>bounce a ray off a surface, bend it through glass</td></tr>
                    <tr><td><code>pow exp sqrt</code></td><td>powers, the glow curve, square root</td></tr>
                    <tr><td><code>sin cos atan</code></td><td>waves and angles</td></tr>
                    </table>
                    <h2>Gotchas</h2>
                    <p>Write floats with a dot, <code>1.0</code> and not <code>1</code>. <code>pow</code> with a negative base gives
                    nothing useful, wrap the base in <code>max(x, 0.0)</code>. Loops need a fixed number of steps, use
                    <code>break</code> to leave early. Some words are reserved and can't be variable names, like
                    <code>flat</code>, <code>sample</code>, <code>half</code> and <code>input</code>.</p>
                    <h2>Practice</h2>
                    <ol><li>Write a line that uses <code>.zyx</code>, <code>.rgb</code> and <code>.xy</code> on one vec3, and check which parts come out.</li><li>Find which function is missing a bracket by reading the error the preview gives you.</li><li>Declare a <code>const float</code> at the top and use it in a loop bound.</li></ol>
                    """),
            new Guide("Making it fast", "what costs the most and how to cut it", """
                    <h1>Making it fast</h1>
                    <p class="lead">An effect runs for every pixel of the item, every frame, on every player's GPU.</p>
                    <h2>Watch the preview</h2>
                    <p>The preview shows its frame rate, and when an effect is slow it drops its own resolution and says so.
                    If you see the resolution go down, players on weaker hardware will feel it.</p>
                    <h2>What costs the most</h2>
                    <p>Loops multiply everything inside them. A 100 step march that calls 5 octaves of noise is 500 noise calls
                    per pixel. A sky that fills the screen at 1080p is two million pixels. Cut steps first, then octaves.</p>
                    <h2>Ways to cut</h2>
                    <table>
                    <tr><td>leave early</td><td><code>break</code> as soon as the answer is known: the ray hit, the cloud is solid, it left the box</td></tr>
                    <tr><td>skip empty space</td><td>don't march clouds below the horizon or noise where nothing shows</td></tr>
                    <tr><td>less detail far away</td><td>fewer octaves where the distance hides them anyway, like the far ocean</td></tr>
                    <tr><td>solve instead of march</td><td>spheres and flat planes have exact formulas, Ringed Planet uses no march at all</td></tr>
                    <tr><td>bigger steps</td><td>for glow you can step in fixed sizes and get away with fewer of them</td></tr>
                    </table>
                    <h2>Practice</h2>
                    <ol><li>Halve the number of march steps and compare the look and the frame rate in the preview.</li><li>Replace a <code>pow(x, 8.0)</code> with <code>x * x * x * x * x * x * x * x</code> and check the result is the same.</li><li>Count the noise calls in a shader and drop one octave where you can't see the difference.</li></ol>
                    """),
            new Guide("Debugging", "errors, black screens and odd colors", """
                    <h1>Debugging</h1>
                    <h2>Compile errors</h2>
                    <p>The preview compiles as you type. Errors show as red squiggles, a red dot by the line number and a list
                    in Problems. Click one to jump to it.</p>
                    <table>
                    <tr><td><b>undeclared identifier</b></td><td>a typo, or a variable used outside the braces it was made in</td></tr>
                    <tr><td><b>cannot convert from int to float</b></td><td>a number written without a dot, write 2.0 instead of 2</td></tr>
                    <tr><td><b>wrong operand types</b></td><td>mixing sizes, like a vec2 plus a vec3. check your swizzles</td></tr>
                    <tr><td><b>syntax error</b></td><td>usually a missing semicolon or bracket on the line above</td></tr>
                    <tr><td><b>needs one of ...</b></td><td>the file needs an entry function: <code>sky</code>, <code>object</code>, <code>surface</code>, <code>material</code> or <code>overlay</code></td></tr>
                    </table>
                    <h2>It compiles but looks wrong</h2>
                    <p><b>All black.</b> Something became 0 or negative, often a smoothstep with its edges the wrong way round, or a
                    pow of a negative number. <b>Flat white.</b> Colors over 1, add tone mapping. <b>Flickering black pixels.</b>
                    Usually a divide by zero or sqrt of a negative, guard them with max.</p>
                    <h2>See your numbers</h2>
                    <div class="tip">Return the value you're unsure about as a color: <code>return vec3(n);</code>. Black is 0, white
                    is 1. It's the fastest way to see what a noise, a mask or a distance is really doing.</div>
                    <h2>Practice</h2>
                    <ol><li>Return a single value, like <code>vec3(dir.y)</code>, to see whether a variable has the value you expect.</li><li>Find a black screen by checking the alpha you return from the object.</li><li>Break a shader on purpose with a missing semicolon, read the message, then fix it.</li></ol>
                    """),
            new Guide("Using it in game", "resource packs, commands and Skript", """
                    <h1>Using it in game</h1>
                    <h2>Loading the pack</h2>
                    <p>Build, then put the zip from the project's build folder into your resource packs folder, or send it from
                    a server. Any version from 1.21 to 26.3 can load it.</p>
                    <h2>Getting the items</h2>
                    <p>Right click an effect and choose <b>Give Command</b> to copy a /give for its item. A sky item has to be
                    huge and inside out around the player, so for skies the Skript is much easier.</p>
                    <h2>The Skript</h2>
                    <p>Turn on <b>Build &gt; Also Make a Skript</b> and every build also writes a .sk. Put it in
                    <code>plugins/Skript/scripts</code>. The command is named after your namespace:</p>
                    <table>
                    <tr><td><code>/ns sky &lt;name&gt; [player|all]</code></td><td>puts someone inside a sky that follows them</td></tr>
                    <tr><td><code>/ns off [player|all]</code></td><td>takes it away</td></tr>
                    <tr><td><code>/ns place &lt;name&gt; [size]</code></td><td>puts an object down in front of you</td></tr>
                    <tr><td><code>/ns size &lt;size&gt;</code></td><td>smoothly resizes the nearest object</td></tr>
                    <tr><td><code>/ns clear, give, list</code></td><td>remove nearby objects, get an item, see what's in the pack</td></tr>
                    </table>
                    <div class="tip">Tab suggestions need the SkBee addon. Rejoin after a reload so your client learns the new command.</div>
                    <h2>Practice</h2>
                    <ol><li>Build a pack, copy it into the resource packs folder and load it in Minecraft. Find your effect in the sky.</li><li>Use the copy give command from an effect's right click menu and paste it in the game chat.</li><li>Export the Skript file and load it on a test server to show your effect in game.</li></ol>
                    """));

}
