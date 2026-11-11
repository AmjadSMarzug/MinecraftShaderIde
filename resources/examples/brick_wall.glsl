// an old brick wall with a warm light sweeping across it, the grout catching the glow and every brick slightly different
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    vec2 g = uv * vec2(6.0, 10.0);
    if (fract(g.y * 0.5) > 0.5) g.x += 0.5;
    vec2 cell = floor(g);
    vec2 f = fract(g);
    float mortar = clamp(step(f.x, 0.06) + step(0.94, f.x) + step(f.y, 0.08) + step(0.92, f.y), 0.0, 1.0);
    float shade = 0.7 + 0.3 * fract(sin(dot(cell, vec2(12.9898, 78.233))) * 43758.5453);
    vec3 brick = vec3(0.55, 0.22, 0.15) * shade;
    vec3 grout = vec3(0.6, 0.6, 0.58);
    vec3 color = mix(brick, grout, mortar);
    float sweep = smoothstep(0.25, 0.0, abs(uv.x - fract(time * 0.1)));
    color *= 0.6 + 0.8 * sweep;
    return vec4(color, 1.0);
}
