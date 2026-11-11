// the item takes on soft pastel bands that roll slowly from one side to the other
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    float t = uv.x * 0.8 + uv.y * 0.2 + time * 0.1;
    vec3 pastel = 0.6 + 0.4 * cos(6.28318 * (vec3(t) + vec3(0.0, 0.33, 0.67)));
    vec3 color = mix(texel.rgb, pastel, 0.55);
    return vec4(color, texel.a);
}
