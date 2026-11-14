package me.thesnowbound.shaderIDE.learn.lessons;

import me.thesnowbound.shaderIDE.learn.Lesson;

import java.util.List;
import java.util.function.Predicate;
import static me.thesnowbound.shaderIDE.learn.Checks.*;

// one lesson: its explanation, starter code and the tasks that check it
public final class LessonBasics {
    private LessonBasics() {
    }

    public static final Lesson LESSON = new Lesson("lesson_basics", "GLSL basics", "semicolons, floats, vectors and functions", 6, """
                    <p>Shaders are written in <b>GLSL</b>. It looks a bit like Java or C. Every line that does something ends with a <code>;</code>. If one is missing, the shader stops working, and the error shows under the preview.</p>
                    <p>There are two kinds of number. <code>1</code> is a whole number. <code>1.0</code> is a decimal. Shaders usually need decimals, and GLSL won't change one into the other for you. <code>float x = 1;</code> is an error, so write <code>float x = 1.0;</code>. Also, <code>1 / 2</code> gives 0, because whole-number division throws away the part after the point.</p>
                    <p><code>vec2</code>, <code>vec3</code> and <code>vec4</code> hold 2, 3 or 4 numbers together. Read the parts with <code>.x .y .z .w</code> or <code>.r .g .b .a</code>. You can also reorder them: <code>color.bgr</code> is the same colour with red and blue swapped. This is called <b>swizzling</b>.</p>
                    <p>A <b>function</b> takes some values and gives one back. <code>sky</code> is a function, and you can write your own above it to keep the code tidy.</p>
                    <h2>Reading an error</h2>
                    <p>The error under the preview names a line and gives a short message. The real mistake is often on the line above, because GLSL only notices a missing <code>;</code> when it reaches the next line. So check the named line and the one above it.</p>
                    <table>
                    <tr><td><code>syntax error</code></td><td>a missing <code>;</code>, bracket or comma</td></tr>
                    <tr><td><code>undeclared identifier</code></td><td>a name spelled differently from where it was made, or used before it was made</td></tr>
                    <tr><td><code>cannot convert</code></td><td>mixing an int with a float, or a vec2 with a vec3</td></tr>
                    <tr><td><code>no matching function</code></td><td>a function was given the wrong number or types of values</td></tr>
                    </table>
                    <h2>The types you'll use</h2>
                    <table>
                    <tr><td><code>float</code></td><td>a decimal number, like <code>0.5</code></td></tr>
                    <tr><td><code>int</code></td><td>a whole number, mostly for loop counters</td></tr>
                    <tr><td><code>bool</code></td><td><code>true</code> or <code>false</code></td></tr>
                    <tr><td><code>vec2 vec3 vec4</code></td><td>2, 3 or 4 floats together: positions, directions and colours</td></tr>
                    </table>
                    <h2>Maths on whole vectors</h2>
                    <p>Most maths works on all the parts of a vector at once. <code>vec3(1.0, 0.5, 0.2) * 2.0</code> doubles all three parts. Adding two vec3s adds them part by part. Functions like <code>sin</code>, <code>max</code> and <code>mix</code> also work part by part. The two sides must be the same size, so you can't add a vec2 to a vec3.</p>
                    <h2>Working through the tasks</h2>
                    <p>The first tasks fix a broken line and then use the value that was worked out but never used. Next you make a
                    float of your own and a vector from parts. The last two cover <code>if</code>, which only runs code when a
                    condition is true, and writing your own function. Each one builds on the last, so keep the shader working as you go.</p>
                    <h2>Try this</h2>
                    <ul>
                    <li>Delete a <code>;</code> on purpose and read the message it gives.</li>
                    <li>Return <code>dir</code> itself as the color. Every direction gets its own color, which is a quick way to see what a value looks like.</li>
                    <li>Return <code>vec3(dir.y)</code> to see height as gray, black at the horizon and white overhead.</li>
                    </ul>
                    """, """
                    // glsl basics
                    // this sky has a mistake in it on purpose, the error under the preview says which line.
                    // fix that first, then work through the tasks

                    vec3 sky(vec3 dir, float time) {
                        float height = dir.y
                        vec3 color = vec3(0.2, 0.4, 0.8);
                        return color;
                    }
                    """, List.of(
                    step("Fix the missing semicolon", "One line is missing its ; at the end, so GLSL reads it and the next line as one broken line.",
                            "float height = dir.y;", has("float\\s+height\\s*=[^;\\n]*;")),
                    step("Use the variable", "height is worked out but never used. Make the color brighter the higher you look by multiplying with it.",
                            "vec3 color = vec3(0.2, 0.4, 0.8) * (0.5 + height);", count("\\bheight\\b", 2)),
                    step("Make a float of your own", "Make a variable that holds a decimal number and use it to change the color. Remember the .0, a plain 2 won't work for a float.",
                            "float brightness = 1.5;\nreturn color * brightness;", has("float\\s+\\w+\\s*=\\s*(\\d+\\.\\d*|\\.\\d+)\\s*;")),
                    step("Swizzle a vector", "Pick the parts of color in a different order, like .bgr or .grb, and return that. The sky changes color.",
                            "return color.bgr * brightness;", has("\\w\\.([rgba]{2,4}|[xyzw]{2,4})\\b")),
                    step("Write your own function", "Above sky, write a small function that takes a color and gives a changed one back, then use it in the return.",
                            "vec3 warm(vec3 c) {\n    return c * vec3(1.2, 0.9, 0.7);\n}\n\n// and in sky():\nreturn warm(color.bgr * brightness);",
                            has("(vec[234]|float)\\s+(?!sky\\b)\\w+\\s*\\([^)]*\\)\\s*\\{")),
                    step("Check a value with if", "An if runs its block only when the condition is true. Here the color only goes bright above the horizon.",
                            "if (height > 0.5) {\n    color = vec3(1.0, 0.95, 0.8);\n}", has("if\\s*\\(")),
                    step("Build a new vector from parts", "Swizzling also works on the left side of a vector: vec3(color.b, color.g, color.r) is the same color with red and blue swapped, written out in full.",
                            "vec3 flipped = vec3(color.b, color.g, color.r);", has("vec3\\s*\\(\\s*\\w+\\.b\\s*,\\s*\\w+\\.g"))));
}
