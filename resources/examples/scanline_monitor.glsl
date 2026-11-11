// a green CRT panel: fine scanlines, a dark band rolling down the screen and a dim glow towards the edges
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    float lines = 0.8 + 0.2 * sin(uv.y * 320.0);
    float roll = smoothstep(0.0, 0.25, abs(fract(uv.y - time * 0.12) - 0.5) * 2.0);
    float edge = smoothstep(0.0, 0.3, uv.x) * smoothstep(0.0, 0.3, 1.0 - uv.x)
               * smoothstep(0.0, 0.3, uv.y) * smoothstep(0.0, 0.3, 1.0 - uv.y);
    float flicker = 0.97 + 0.03 * sin(time * 47.0);
    vec3 phosphor = vec3(0.25, 1.0, 0.45);
    vec3 color = phosphor * lines * (0.55 + 0.45 * roll) * (0.35 + 0.65 * edge) * flicker;
    return vec4(color, 1.0);
}
