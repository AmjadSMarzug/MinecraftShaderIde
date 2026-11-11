// green code rain: columns of glyph blocks falling at different speeds, with a bright head at the front of each column
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

float mrHash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    float columns = 28.0;
    float col = floor(uv.x * columns);
    float speed = 0.2 + mrHash(vec2(col, 1.0)) * 0.6;
    float y = fract(uv.y + mrHash(vec2(col, 2.0)) - time * speed);
    float glyph = step(0.35, mrHash(vec2(col, floor(y * 16.0))));
    float head = smoothstep(0.92, 1.0, y);
    float fade = pow(y, 3.0);
    vec3 color = vec3(0.0, 0.5 * fade, 0.1 * fade) * glyph + vec3(0.8, 1.0, 0.8) * head * glyph;
    return vec4(color, 1.0);
}
