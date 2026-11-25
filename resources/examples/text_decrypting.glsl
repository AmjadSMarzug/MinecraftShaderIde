// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float dcHash(vec2 p) { return fract(sin(dot(p, vec2(12.9, 78.2))) * 43758.5); }
vec4 text(vec2 p, vec2 size, float time) {
    float settle = clamp(time * 0.5, 0.0, 1.0);
    float noise = dcHash(floor(p) + floor(time * 10.0));
    float shown = step(settle, noise);
    vec3 col = mix(vec3(0.0, 1.0, 0.3), vec3(0.2, 0.9, 0.5), shown);
    return vec4(col, 1.0);
}
