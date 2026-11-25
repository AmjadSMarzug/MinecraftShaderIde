// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    vec2 c = p - size * 0.5;
    float angle = atan(c.y, c.x) + time * 2.0;
    vec3 col = 0.5 + 0.5 * cos(vec3(0.0, 2.0, 4.0) + angle * 2.0);
    return vec4(col, 1.0);
}
