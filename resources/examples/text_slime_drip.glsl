// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float dripHash(float n) { return fract(sin(n * 53.7) * 43758.5); }
vec4 text(vec2 p, vec2 size, float time) {
    // each column has its own slime length, so the letters drip to different depths
    float column = floor(p.x);
    float length = 0.4 + dripHash(column) * 0.6 + 0.05 * sin(time * 2.0 + column);
    float fill = step(p.y / size.y, length);
    vec3 slime = mix(vec3(0.3, 0.9, 0.2), vec3(0.6, 1.0, 0.4), p.y / size.y);
    return vec4(slime, clamp(fill, 0.0, 1.0));
}
