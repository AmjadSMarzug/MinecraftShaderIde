// the item sinks into deep violet shadow and glows out where it's bright, as if lit from within
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    float lum = dot(texel.rgb, vec3(0.299, 0.587, 0.114));
    vec3 shadow = texel.rgb * vec3(0.5, 0.3, 0.9);
    vec3 bright = texel.rgb * vec3(1.4, 1.2, 1.1);
    vec3 color = mix(shadow, bright, smoothstep(0.3, 0.8, lum));
    float pulse = 0.9 + 0.1 * sin(time * 2.0);
    return vec4(color * pulse, texel.a);
}
