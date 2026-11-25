// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float textHash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 text(vec2 p, vec2 size, float time) {
    float d = length(p - size * 0.5) / length(size);
    float pulse = 0.75 + 0.25 * sin(time * 4.0);
    vec3 neon = mix(vec3(1.0, 0.2, 0.8), vec3(0.3, 0.9, 1.0), 0.5 + 0.5 * sin(time));
    vec3 glow = neon * pulse * (1.0 - smoothstep(0.1, 0.6, d));
    return vec4(glow + neon * 0.3, 1.0);
}
