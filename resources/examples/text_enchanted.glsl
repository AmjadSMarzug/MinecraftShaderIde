// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float enchantHash(vec2 p) { return fract(sin(dot(p, vec2(41.3, 289.1))) * 9371.7); }
vec4 text(vec2 p, vec2 size, float time) {
    // purple runes swirling around the letter, with a bright glint sliding across it
    vec2 c = p - size * 0.5;
    float swirl = sin(atan(c.y, c.x) * 2.0 + length(c) * 1.5 - time * 2.0);
    vec3 base = mix(vec3(0.4, 0.15, 0.8), vec3(0.75, 0.45, 1.0), 0.5 + 0.5 * swirl);
    float glint = smoothstep(0.0, 0.1, fract(p.x / size.x - time * 0.6)) * (1.0 - smoothstep(0.1, 0.3, fract(p.x / size.x - time * 0.6)));
    float rune = step(0.9, enchantHash(floor(p) + floor(time * 2.0)));
    return vec4(mix(base, vec3(0.95, 0.85, 1.0), glint + rune * 0.5), 1.0);
}
