// glossy candy: bright swirled stripes running diagonally across the item, with a wet shine on top
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    float stripe = 0.5 + 0.5 * sin((uv.x + uv.y) * 14.0 + time);
    vec3 pink = vec3(1.0, 0.4, 0.7);
    vec3 mint = vec3(0.5, 1.0, 0.8);
    vec3 candy = mix(pink, mint, step(0.5, stripe));
    float lum = dot(texel.rgb, vec3(0.333));
    vec3 color = candy * (0.5 + 0.5 * lum);
    float gloss = pow(max(dot(reflect(view, normal), normalize(vec3(0.3, 0.8, 0.5))), 0.0), 40.0);
    color += vec3(1.0) * gloss * 0.8;
    return vec4(color, texel.a);
}
