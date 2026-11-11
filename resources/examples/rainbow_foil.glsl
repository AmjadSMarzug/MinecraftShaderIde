// the item looks like holographic foil: its colour shifts with the angle you look at it, like a rainbow sticker
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec3 rfHue(float h) {
    return clamp(abs(mod(h * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
}

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    float lum = dot(texel.rgb, vec3(0.333));
    float angle = dot(normalize(normal), normalize(view));
    float h = fract(angle * 0.8 + uv.x * 0.3 + uv.y * 0.2 + time * 0.05);
    vec3 foil = rfHue(h);
    vec3 color = mix(texel.rgb, foil, 0.55) * (0.5 + 0.7 * lum);
    return vec4(color, texel.a);
}
