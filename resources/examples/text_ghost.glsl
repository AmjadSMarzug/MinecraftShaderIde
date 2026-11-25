// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float drift = sin(time * 1.5 + p.y * 0.4) * 0.4;
    float fade = 0.5 + 0.5 * sin(time * 2.0 + drift);
    vec3 col = mix(vec3(0.7, 0.8, 1.0), vec3(1.0), p.y / size.y);
    return vec4(col, 0.4 + 0.6 * fade);
}
