package me.thesnowbound.shaderIDE.preview;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL33.*;

// draws a line of text the way the game's chat does: an 8 by 8 pixel letter per glyph, on a dark box, with a text
// effect's text(p, size, time) colouring each pixel of every letter
public final class TextCanvas extends AWTGLCanvas {
    static final int COLS = 16, CELL = 8, PAD = 2;
    static final String HEAD = """
            #version 330
            uniform sampler2D uAtlas;
            uniform sampler2D uLayout;
            uniform float uTime;
            uniform vec2 uCanvas;
            uniform float uScale;
            uniform vec2 uOrigin;
            uniform float uCount;
            out vec4 fragColor;
            """;
    static final String MAIN = """
            void main() {
                vec2 px = vec2(gl_FragCoord.x, uCanvas.y - gl_FragCoord.y) / uScale;
                vec3 box = vec3(0.09, 0.09, 0.11);
                vec2 lp = px - uOrigin;
                vec3 colour = box;
                float i = floor(lp.x / %d.0);
                if (lp.x >= 0.0 && lp.y >= 0.0 && lp.y < %d.0 && i < uCount) {
                    vec2 inside = vec2(mod(lp.x, %d.0), lp.y);
                    float index = texelFetch(uLayout, ivec2(int(i), 0), 0).r;
                    int g = int(index + 0.5);
                    ivec2 texel = ivec2((g %% %d) * %d + int(inside.x), (g / %d) * %d + int(inside.y));
                    float mask = texelFetch(uAtlas, texel, 0).a;
                    vec4 letter = text(inside, vec2(%d.0), uTime);
                    colour = mix(box, letter.rgb, mask * clamp(letter.a, 0.0, 1.0));
                }
                fragColor = vec4(colour, 1.0);
            }
            """;

    private final Consumer<String> errors;
    private final long started = System.nanoTime();
    private volatile String pendingSource;
    private volatile String pendingText = "Shader IDE";
    private String source;
    private String text = "Shader IDE";
    private int program, vao, atlas, layout;
    private int uTime, uCanvas, uScale, uOrigin, uCount;
    private boolean ready;
    private BufferedImage glyphs;
    private volatile Consumer<BufferedImage> shot;

    public TextCanvas(Consumer<String> errors) {
        super(Preview.settings());
        this.errors = errors;
    }

    public void setSource(String effect) {
        pendingSource = effect;
    }

    public void setText(String value) {
        pendingText = value;
    }

    // the next frame that draws is saved to a picture, for the thumbnails
    public void shoot(Consumer<BufferedImage> done) {
        shot = done;
    }

    // the letters come from the font the chat panel gave us, see TextFont
    public void setGlyphs(BufferedImage image) {
        glyphs = image;
        ready = false;
    }

    @Override
    public void initGL() {
        GL.createCapabilities();
        vao = glGenVertexArrays();
        atlas = glGenTextures();
        layout = glGenTextures();
        ready = false;
    }

    private void build() {
        String effect = pendingSource;
        if (effect != null) {
            pendingSource = null;
            source = effect;
            if (program != 0) glDeleteProgram(program);
            program = 0;
        }
        if (source == null) return;
        String fragment = HEAD + source + "\n" + String.format(MAIN, CELL, CELL, CELL, COLS, CELL, COLS, CELL, CELL);
        List<Problem> found = new ArrayList<>();
        int headLines = HEAD.split("\n", -1).length - 1;
        int vs = Preview.shader(GL_VERTEX_SHADER, VERTEX, 0, found);
        int fs = Preview.shader(GL_FRAGMENT_SHADER, fragment, headLines, found);
        if (found.isEmpty()) {
            int p = glCreateProgram();
            glAttachShader(p, vs);
            glAttachShader(p, fs);
            glLinkProgram(p);
            if (glGetProgrami(p, GL_LINK_STATUS) == GL_TRUE) {
                program = p;
                uTime = glGetUniformLocation(p, "uTime");
                uCanvas = glGetUniformLocation(p, "uCanvas");
                uScale = glGetUniformLocation(p, "uScale");
                uOrigin = glGetUniformLocation(p, "uOrigin");
                uCount = glGetUniformLocation(p, "uCount");
            } else {
                found.add(new Problem(0, glGetProgramInfoLog(p).strip()));
                glDeleteProgram(p);
            }
        }
        glDeleteShader(vs);
        glDeleteShader(fs);
        errors.accept(found.isEmpty() ? "" : found.get(0).message());
    }

    static final String VERTEX = """
            #version 330
            void main() {
                vec2 corner = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);
                gl_Position = vec4(corner * 2.0 - 1.0, 0.0, 1.0);
            }
            """;

    private void uploadAtlas() {
        BufferedImage img = glyphs != null ? glyphs : TextFont.generated();
        int w = img.getWidth(), h = img.getHeight();
        ByteBuffer px = BufferUtils.createByteBuffer(w * h * 4);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = img.getRGB(x, y);
                px.put((byte) 255).put((byte) 255).put((byte) 255).put((byte) ((argb >>> 24) & 255));
            }
        }
        px.flip();
        glBindTexture(GL_TEXTURE_2D, atlas);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, px);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        ready = true;
    }

    private int uploadLayout(String line) {
        int n = Math.max(1, line.length());
        FloatBufferHolder holder = new FloatBufferHolder(n);
        for (int i = 0; i < n; i++) {
            int c = i < line.length() ? line.charAt(i) & 0xff : ' ';
            holder.put(c);
        }
        glBindTexture(GL_TEXTURE_2D, layout);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R32F, n, 1, 0, GL_RED, GL_FLOAT, holder.buffer());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        return n;
    }

    @Override
    public void paintGL() {
        if (!ready) uploadAtlas();
        build();
        String line = pendingText;
        if (!line.equals(text)) {
            text = line;
        }
        int count = uploadLayout(text);
        glViewport(0, 0, getFramebufferWidth(), getFramebufferHeight());
        glClearColor(0.09f, 0.09f, 0.11f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);
        if (program != 0) {
            glUseProgram(program);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, atlas);
            glUniform1i(glGetUniformLocation(program, "uAtlas"), 0);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, layout);
            glUniform1i(glGetUniformLocation(program, "uLayout"), 1);
            float t = (System.nanoTime() - started) / 1e9f;
            glUniform1f(uTime, pendingTime(t));
            glUniform2f(uCanvas, getFramebufferWidth(), getFramebufferHeight());
            // the box keeps its shape and the letters sit in the middle of it
            float fbw = getFramebufferWidth(), fbh = getFramebufferHeight();
            float sc = Math.min(fbw / (count * CELL + PAD * 2f), fbh / (CELL + PAD * 2f));
            glUniform1f(uScale, sc);
            glUniform2f(uOrigin, (fbw / sc - count * CELL) / 2f, (fbh / sc - CELL) / 2f);
            glUniform1f(uCount, count);
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 3);
        }
        Consumer<BufferedImage> done = shot;
        if (done != null) {
            shot = null;
            done.accept(readBack(getFramebufferWidth(), getFramebufferHeight()));
        }
        swapBuffers();
    }

    // a fixed time for the thumbnails, so the same effect always gives the same picture
    private volatile Float frozen;

    public void freeze(float seconds) {
        frozen = seconds;
    }

    private float pendingTime(float live) {
        Float f = frozen;
        return f != null ? f : live;
    }

    private BufferedImage readBack(int w, int h) {
        ByteBuffer px = BufferUtils.createByteBuffer(w * h * 4);
        glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, px);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = (y * w + x) * 4;
                img.setRGB(x, h - 1 - y, (px.get(i) & 255) << 16 | (px.get(i + 1) & 255) << 8 | px.get(i + 2) & 255);
            }
        }
        return img;
    }

    static final class FloatBufferHolder {
        private final java.nio.FloatBuffer buf;

        FloatBufferHolder(int n) {
            buf = BufferUtils.createFloatBuffer(n);
        }

        void put(int code) {
            buf.put(code);
        }

        java.nio.FloatBuffer buffer() {
            buf.flip();
            return buf;
        }
    }
}
