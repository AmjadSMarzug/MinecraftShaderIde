// the item is printed with a glowing circuit: thin traces across the sprite, and light pulses running along them
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    vec2 g = uv * 8.0;
    vec2 f = abs(fract(g) - 0.5);
    float trace = smoothstep(0.46, 0.5, max(f.x, f.y));
    float pulse = smoothstep(0.2, 0.0, abs(fract(g.x * 0.3 + g.y * 0.1 - time * 0.5) - 0.5));
    float lum = dot(texel.rgb, vec3(0.333));
    vec3 base = texel.rgb * (0.4 + 0.6 * lum);
    vec3 color = base + vec3(0.1, 0.9, 1.0) * trace * 0.6 + vec3(0.8, 1.0, 1.0) * pulse * trace;
    return vec4(color, texel.a);
}
