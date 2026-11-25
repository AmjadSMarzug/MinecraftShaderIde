// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float band = floor(p.x / 2.0) + floor(time * 4.0);
    vec3 col = 0.5 + 0.5 * cos(vec3(0.0, 2.1, 4.2) + band * 1.3);
    return vec4(col, 1.0);
}
