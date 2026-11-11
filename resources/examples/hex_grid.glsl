// a glowing honeycomb: hexagon outlines that shimmer in a wave running across the panel
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    vec2 r = vec2(1.0, 1.732);
    vec2 h = r * 0.5;
    vec2 g = uv * vec2(10.0, 10.0);
    vec2 a = mod(g, r) - h;
    vec2 b = mod(g - h, r) - h;
    vec2 cell = dot(a, a) < dot(b, b) ? a : b;
    float hexd = max(abs(cell.x) * 0.866 + abs(cell.y) * 0.5, abs(cell.y));
    float line = smoothstep(0.42, 0.5, hexd);
    float wave = 0.5 + 0.5 * sin(time * 2.0 + hexd * 10.0 + uv.x * 3.0);
    vec3 color = mix(vec3(0.02, 0.05, 0.08), vec3(0.2, 0.9, 1.0) * wave, line);
    return vec4(color, 1.0);
}
