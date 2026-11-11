// a green circuit board: a grid of traces with pads, and light pulses running along the horizontal ones
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

float cbLine(float v) {
    float d = abs(fract(v) - 0.5);
    return smoothstep(0.04, 0.0, d);
}

float cbHash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    vec2 g = uv * 6.0;
    vec2 cell = floor(g);
    float horizontal = cbLine(g.y) * step(0.4, cbHash(cell));
    float vertical = cbLine(g.x) * step(0.5, cbHash(cell + 7.0));
    float trace = max(horizontal, vertical);
    float pad = step(0.9, cbHash(floor(g * 2.0) + 3.0)) * smoothstep(0.2, 0.1, length(fract(g * 2.0) - 0.5));
    float pulse = horizontal * smoothstep(0.25, 0.0, abs(fract(g.x * 0.12 - time * 0.3) - 0.5));
    vec3 color = vec3(0.02, 0.08, 0.04) + trace * vec3(0.1, 0.4, 0.2) + pad * vec3(0.3, 0.9, 0.5) + pulse * vec3(0.4, 1.0, 0.7);
    return vec4(color, 1.0);
}
