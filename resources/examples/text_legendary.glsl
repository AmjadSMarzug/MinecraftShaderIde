// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float sweep = fract(time * 0.25) * (size.x + size.y) * 1.5 - 4.0;
    float shine = 1.0 - smoothstep(0.0, 2.0, abs((p.x + p.y) - sweep));
    vec3 gold = mix(vec3(0.85, 0.6, 0.15), vec3(1.0, 0.95, 0.6), shine);
    return vec4(gold, 1.0);
}
