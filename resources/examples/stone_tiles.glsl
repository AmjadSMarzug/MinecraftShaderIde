// a floor of old stone tiles, each one a slightly different tone with dark grout lines between them
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

float stHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    vec2 g = uv * 5.0;
    vec2 cell = floor(g);
    vec2 f = fract(g);
    float edge = smoothstep(0.0, 0.05, min(min(f.x, 1.0 - f.x), min(f.y, 1.0 - f.y)));
    float tone = 0.4 + 0.3 * stHash(cell);
    vec3 stone = vec3(tone * 0.9, tone * 0.85, tone * 0.75);
    vec3 color = mix(vec3(0.05, 0.04, 0.04), stone, edge);
    return vec4(color, 1.0);
}
