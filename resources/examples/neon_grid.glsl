// the item gets a neon grid drawn over its sprite, with magenta and cyan lines pulsing across it
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    vec2 g = uv * 4.0;
    vec2 f = abs(fract(g) - 0.5);
    float grid = smoothstep(0.44, 0.5, max(f.x, f.y));
    float wave = 0.5 + 0.5 * sin(g.x * 2.0 + g.y * 2.0 - time * 3.0);
    vec3 neon = mix(vec3(1.0, 0.1, 0.8), vec3(0.1, 0.9, 1.0), wave);
    vec3 base = texel.rgb * 0.35 + vec3(0.02, 0.0, 0.05);
    vec3 color = base + neon * grid * 0.9;
    return vec4(color, texel.a);
}
