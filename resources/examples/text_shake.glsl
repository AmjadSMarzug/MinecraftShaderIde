// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float shHash(float n) { return fract(sin(n * 12.9) * 43758.5); }
vec4 text(vec2 p, vec2 size, float time) {
    float tremble = (shHash(floor(time * 25.0)) - 0.5) * 0.6;
    vec3 col = mix(vec3(1.0, 0.9, 0.3), vec3(1.0, 0.4, 0.1), clamp(tremble + 0.5, 0.0, 1.0));
    return vec4(col, 1.0);
}
