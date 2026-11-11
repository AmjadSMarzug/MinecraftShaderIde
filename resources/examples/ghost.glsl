// the item turns ghostly: pale and see-through, with a wave of transparency drifting slowly up through it
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    float wave = 0.5 + 0.5 * sin(uv.y * 20.0 - time * 2.0);
    vec3 pale = mix(texel.rgb, vec3(0.75, 0.9, 1.0), 0.6);
    float alpha = texel.a * (0.35 + 0.3 * wave);
    float rim = pow(1.0 - abs(dot(normal, view)), 2.0);
    return vec4(pale + vec3(0.3) * rim, alpha);
}
