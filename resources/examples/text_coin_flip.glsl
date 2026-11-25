// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float flip = cos(time * 3.0 + p.x * 0.4);
    float face = step(0.0, flip);
    vec3 col = mix(vec3(0.7, 0.5, 0.1), vec3(1.0, 0.85, 0.3), face) * (0.6 + 0.4 * abs(flip));
    return vec4(col, 1.0);
}
