package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonLighting {
    private LessonLighting() {
    }

    public static final Lesson LESSON = new Lesson("lesson_lighting", "Lighting objects", "diffuse, specular, rim and occlusion", 10, """
                    <p>The <b>normal</b> is the direction a surface faces. You can find it for any shape by checking how the distance changes when you nudge the point slightly along each axis.</p>
                    <p>With a normal you can light anything. <b>Diffuse</b> is the matte brightness: a surface is bright where it faces the light, found with <code>dot(normal, light)</code>. <b>Specular</b> is the shiny highlight. A <b>rim</b> light brightens the edges that face away from you, so the shape stands out from the background.</p>
                    <p>The number of steps the ray took is a rough guide to <b>ambient occlusion</b>, which darkens creases. A ray that passes close to a crease takes more steps, so the step count shows where those spots are.</p>
                    <h2>How the normal is found</h2>
                    <p>Nudge the point a tiny bit along x and measure the distance on both sides. The difference tells you how fast the distance grows that way. Do the same for y and z, and you get the direction pointing straight out of the surface. The nudge matters: too small and the normal gets noisy, too big and sharp edges go soft.</p>
                    <h2>Diffuse</h2>
                    <p>A surface turned away from the light gets less of it. <code>dot(n, light)</code> gives that: 1 when the surface faces the light, 0 when it's sideways, and below 0 when it faces away.</p>
                    <h2>Specular</h2>
                    <p>Bounce the view ray off the surface with <code>reflect(dir, n)</code>, then check how close the bounce points to the light. The power sets the shininess: 8 looks like rough plastic, 64 like polished metal. Another common method uses the <b>half vector</b>, <code>normalize(light - dir)</code>.</p>
                    <h2>Fresnel</h2>
                    <p>Almost every surface reflects more at a shallow angle. Glass looks clear when you face it and like a mirror when you look at it from the side. A good approximation is <code>f0 + (1.0 - f0) * pow(1.0 - cosTheta, 5.0)</code>, with f0 about 0.04 for most materials.</p>
                    <h2>Shadows</h2>
                    <p>From the point you hit, send a second ray toward the light. If it hits something, the point is in shadow. For soft shadows, keep the smallest <code>k * d / t</code> you pass along the way and use it as the amount of light.</p>
                    <h2>Common mistakes</h2>
                    <ul>
                    <li>A shadow ray that starts exactly on the surface hits that surface straight away and shadows everything. Start it a little outside: <code>p + n * 0.01</code>.</li>
                    <li>Forgetting ambient light makes the dark side pure black.</li>
                    </ul>
                    <h2>Working through the tasks</h2>
                    <p>Lighting is layers added together: diffuse for the main light, ambient so nothing is pure black, specular for the
                    shine, rim for the edges and a fake occlusion to darken creases. The new tasks add a warm light bouncing up from the
                    ground and a tighter shine for polished metal.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Add a hard shadow on the ring from the ball.</li>
                    <li>A second, blue light from the other side.</li>
                    </ul>
                    """, """
                    // lighting objects
                    // the shape and its normal are done, right now it's lit flat. light it properly

                    float shape(vec3 p) {
                        float ball = length(p) - 0.45;
                        vec2 q = vec2(length(p.xz) - 0.7, p.y);
                        return min(ball, length(q) - 0.08);
                    }

                    vec4 object(vec3 origin, vec3 dir, float time) {
                        vec3 light = normalize(vec3(0.6, 0.7, 0.4));
                        float t = 0.0;
                        for (int i = 0; i < 80; i++) {
                            vec3 p = origin + dir * t;
                            float d = shape(p);
                            if (d < 0.001) {
                                vec2 e = vec2(0.001, 0.0);
                                vec3 n = normalize(vec3(shape(p + e.xyy) - shape(p - e.xyy),
                                                        shape(p + e.yxy) - shape(p - e.yxy),
                                                        shape(p + e.yyx) - shape(p - e.yyx)));
                                vec3 color = vec3(0.8);
                                return vec4(color, 1.0);
                            }
                            t += d;
                            if (t > 3.5) break;
                        }
                        return vec4(0.0);
                    }
                    """, List.of(
                    step("Add diffuse light", "dot of the normal with the light direction, kept above zero.",
                            "float diffuse = max(dot(n, light), 0.0);\nvec3 color = vec3(0.8) * diffuse;   // replaces vec3 color = vec3(0.8);", has("dot\\s*\\(\\s*(n\\s*,|light\\s*,\\s*n\\b)")),
                    step("Add some ambient light", "Without it the dark side is pure black. Add a small amount of light that reaches everywhere.",
                            "float ambient = 0.15;\nvec3 color = vec3(0.8) * (ambient + diffuse);", has("\\bambient\\b|\\d\\.\\d+\\s*\\+\\s*diffuse|diffuse\\s*\\+\\s*\\d\\.\\d+")),
                    step("Add a specular highlight", "reflect the view ray off the surface and pow how close it lands to the light.",
                            "float spec = pow(max(dot(reflect(dir, n), light), 0.0), 40.0);\ncolor += vec3(1.0) * spec;", has("reflect\\s*\\(")),
                    step("Add a rim light", "Edges that face away from the camera get brighter.",
                            "float rim = pow(1.0 - max(dot(n, -dir), 0.0), 3.0);\ncolor += vec3(0.4, 0.6, 1.0) * rim * 0.6;", has("pow\\s*\\(\\s*1\\.0\\s*-|1\\.0\\s*-\\s*(max|abs|clamp)\\s*\\(\\s*dot")),
                    step("Fake ambient occlusion", "Scale everything by how many steps the ray took to get here.",
                            "float occlusion = 1.0 - float(i) / 80.0;\ncolor *= occlusion;", has("float\\s*\\(\\s*\\w+\\s*\\)\\s*/")),
                    step("Add a warm bounce from below", "dot with -light lights the underside. Sunlight bounces off the ground, so give that light a warm color.",
                            "float bounce = max(dot(n, -light), 0.0);\ncolor += vec3(0.6, 0.4, 0.2) * bounce * 0.3;", has("dot\\s*\\(\\s*n\\s*,\\s*-\\s*light")),
                    step("Make the shine tighter", "A bigger power on the specular term makes the highlight smaller and sharper, like polished metal.",
                            "float tight = pow(max(dot(reflect(dir, n), light), 0.0), 64.0);\ncolor += vec3(1.0) * tight;", has("pow\\s*\\([^;]*,\\s*64\\.0"))));
}
