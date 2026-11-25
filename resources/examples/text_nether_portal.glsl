// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    vec2 c = p - size * 0.5;
    float swirl = sin(atan(c.y, c.x) * 3.0 + length(c) * 1.2 - time * 3.0);
    vec3 col = mix(vec3(0.05, 0.0, 0.1), vec3(0.6, 0.1, 0.9), 0.5 + 0.5 * swirl);
    return vec4(col, 1.0);
}
