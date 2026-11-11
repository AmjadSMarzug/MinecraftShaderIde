// the item dissolves in grains: noise eats the sprite away over time, and the edges of each hole burn orange
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

float dzHash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    float grain = dzHash(floor(uv * 12.0));
    float amount = 0.5 + 0.5 * sin(time * 0.7);
    float edge = smoothstep(0.1, 0.0, abs(grain - amount));
    float alpha = texel.a * step(amount * 0.8 + 0.1, grain);
    vec3 burn = vec3(1.0, 0.5, 0.1) * edge * 2.0;
    return vec4(texel.rgb + burn, alpha);
}
