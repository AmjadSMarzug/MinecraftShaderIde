package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonMaterial {
    private LessonMaterial() {
    }

    public static final Lesson LESSON = new Lesson("lesson_material", "Your first material", "repainting an item's pixels", 10, """
                    <p>A material keeps the item's flat sprite and repaints each of its pixels. <code>texel</code> is the colour of that pixel from the texture, so you can keep the outline and shading and change everything else. <code>uv</code> goes from 0 to 1 across the sprite.</p>
                    <p>Item sprites are 16 by 16 pixels. Smooth effects can look out of place next to them, so it's common to snap <code>uv</code> to the pixel grid. <code>(floor(uv * 16.0) + 0.5) / 16.0</code> gives the centre of whichever pixel you're in, and anything drawn with it looks blocky, like the game's own art.</p>
                    <p>The preview uses a plain gem if you don't have your own image. To use your own, put a PNG with the same name beside the effect file.</p>
                    <h2>What runs through it</h2>
                    <p>Only the item's visible pixels go through the material. Empty pixels around the sprite stay see-through as long as you pass <code>texel.a</code> along as the alpha. If you return alpha 1 everywhere, those empty pixels turn into solid squares.</p>
                    <h2>Brightness that looks right</h2>
                    <p>Eyes see green as much brighter than blue. To get a brightness that matches what you see, weight the channels: <code>dot(texel.rgb, vec3(0.299, 0.587, 0.114))</code>. Use it to keep the sprite's shading, or as a mask.</p>
                    <h2>Masks</h2>
                    <p>A mask decides where the effect goes. <code>smoothstep(0.6, 0.8, brightness)</code> picks only the bright pixels, so a sword's blade can glow while its dark handle stays the same. <code>uv.y</code> can be a mask too, for an effect that only covers the top of the item.</p>
                    <h2>Staying blocky</h2>
                    <p>Snapping to the 16 by 16 grid moves the effect one whole pixel at a time, like the game's own textures. You can also snap time with <code>floor(time * 8.0) / 8.0</code> for choppy animation, like the game's animated blocks.</p>
                    <h2>Depth as you turn</h2>
                    <p>Offsetting uv by <code>view.xz * depth</code> moves a layer as the item turns. Several layers at different depths slide past each other and give the item depth, like the end portal.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>Returning alpha 1 everywhere turns the transparent area into a solid square.</li>
                    <li>Smooth, unsnapped effects look out of place next to blocky vanilla items.</li>
                    </ul>
                    <h2>Working through the tasks</h2>
                    <p>A material keeps the item's own pixels and repaints them. The first tasks tint, snap, animate and shade the pixels, then add
                    a glint. The new tasks use the sprite's brightness as a mask, so only the bright pixels glow, and sweep a glint diagonally
                    across the item.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Lava only on the bright parts, using a brightness mask.</li>
                    <li>An enchantment style glint: a diagonal band sweeping across with time.</li>
                    </ul>
                    """, """
                    // your first material
                    // texel is the item's own pixel color and uv goes 0 to 1 across the sprite.
                    // right now every pixel is given back unchanged

                    vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
                        return texel;
                    }
                    """, List.of(
                    step("Tint it", "Multiply the texel's color by a color of your own. Keep texel.a as the alpha so see-through pixels stay see-through.",
                            "return vec4(texel.rgb * vec3(1.0, 0.5, 0.2), texel.a);", has("texel\\.rgb\\s*\\*|\\*\\s*texel\\.rgb")),
                    step("Snap to the sprite's pixels", "floor counts which of the 16 pixels you're in, the + 0.5 moves to its middle.",
                            "vec2 px = (floor(uv * 16.0) + 0.5) / 16.0;", has("floor\\s*\\(\\s*uv\\s*\\*\\s*16")),
                    step("Run a wave across it", "A sin of the snapped position minus time moves across the item one pixel step at a time.",
                            "float wave = 0.5 + 0.5 * sin(px.x * 10.0 + px.y * 6.0 - time * 3.0);\nvec3 color = mix(vec3(0.6, 0.1, 0.0), vec3(1.0, 0.8, 0.2), wave);\nreturn vec4(color, texel.a);",
                            has("sin\\s*\\(").and(uses("time"))),
                    step("Keep the sprite's shading", "Multiply by how bright the texel is, so the dark outline and shading still show through your colors.",
                            "float shade = dot(texel.rgb, vec3(0.333));\nvec3 color = mix(vec3(0.6, 0.1, 0.0), vec3(1.0, 0.8, 0.2), wave) * shade * 1.6;\nreturn vec4(color, texel.a);",
                            has("dot\\s*\\(\\s*texel|texel\\.[rgb]\\b(?!g)")),
                    step("Make it glint as you turn", "Reflect the view off the item and see how close it bounces toward a light. Add that as a highlight before the return.",
                            "float glint = pow(max(dot(reflect(view, normal), normalize(vec3(0.4, 0.8, 0.4))), 0.0), 16.0);\ncolor += vec3(1.0) * glint;\nreturn vec4(color, texel.a);",
                            has("reflect\\s*\\(|dot\\s*\\([^;]*\\bview\\b")),
                    step("Only glow on the bright parts", "The brightness of the texel is a mask. smoothstep picks out the bright pixels, so the dark parts keep their own color.",
                            "float mask = smoothstep(0.6, 0.8, dot(texel.rgb, vec3(0.299, 0.587, 0.114)));", has("smoothstep\\s*\\([^;]*texel")),
                    step("Sweep a glint across diagonally", "uv.x + uv.y is the same along every diagonal, so a band across it that moves with time sweeps across the item.",
                            "float band = smoothstep(0.08, 0.0, abs(uv.x + uv.y - fract(time * 0.3) * 2.0));", has("abs\\s*\\(\\s*uv\\.x\\s*\\+\\s*uv\\.y").and(uses("time")))));
}
