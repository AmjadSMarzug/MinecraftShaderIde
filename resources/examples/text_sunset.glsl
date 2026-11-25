// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float t = clamp(p.y / size.y + 0.05 * sin(time), 0.0, 1.0);
    vec3 top = vec3(0.4, 0.2, 0.6);
    vec3 mid = vec3(1.0, 0.4, 0.3);
    vec3 low = vec3(1.0, 0.8, 0.3);
    vec3 col = t < 0.5 ? mix(top, mid, t * 2.0) : mix(mid, low, (t - 0.5) * 2.0);
    return vec4(col, 1.0);
}
