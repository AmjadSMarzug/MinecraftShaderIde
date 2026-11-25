// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float stHash(vec2 p) { return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5); }
vec4 text(vec2 p, vec2 size, float time) {
    float tw = 0.5 + 0.5 * sin(time * 3.0 + stHash(floor(p)) * 30.0);
    float star = step(0.85, stHash(floor(p) + floor(time)));
    vec3 col = mix(vec3(0.2, 0.3, 0.6), vec3(1.0, 0.95, 0.7), star * tw);
    return vec4(col, 1.0);
}
