// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float edge = min(min(p.x, size.x - p.x), min(p.y, size.y - p.y));
    vec3 outline = vec3(1.0);
    vec3 fill = mix(vec3(1.0, 0.4, 0.5), vec3(1.0, 0.7, 0.3), 0.5 + 0.5 * sin(time));
    vec3 col = edge < 1.0 ? outline : fill;
    return vec4(col, 1.0);
}
