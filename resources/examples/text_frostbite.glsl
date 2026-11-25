// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float frHash(vec2 p) { return fract(sin(dot(p, vec2(41.3, 289.1))) * 9371.7); }
vec4 text(vec2 p, vec2 size, float time) {
    float edge = min(min(p.x, size.x - p.x), min(p.y, size.y - p.y));
    float spread = clamp(time * 0.4 - edge * 0.2, 0.0, 1.0);
    float crack = step(0.8, frHash(floor(p))) * spread;
    vec3 col = mix(vec3(0.7, 0.9, 1.0), vec3(1.0), crack);
    return vec4(col, 1.0);
}
