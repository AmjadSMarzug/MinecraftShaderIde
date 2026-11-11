// dark cooled rock with glowing ember veins: the darkest pixels of the item crack open to show molten light
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    float dark = 1.0 - dot(texel.rgb, vec3(0.333));
    float vein = smoothstep(0.08, 0.0, abs(sin(uv.x * 9.0 + uv.y * 5.0 + sin(uv.y * 13.0) * 1.5)));
    float heat = 0.7 + 0.3 * sin(time * 3.0 + uv.x * 6.0);
    vec3 rock = texel.rgb * 0.5 + vec3(0.03, 0.02, 0.02);
    vec3 magma = vec3(1.0, 0.45, 0.1) * heat;
    vec3 color = mix(rock, magma, vein * dark);
    return vec4(color, texel.a);
}
