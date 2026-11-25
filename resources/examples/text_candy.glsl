// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float stripe = step(0.5, fract((p.x + p.y + time * 3.0) / 3.0));
    vec3 col = mix(vec3(1.0, 0.4, 0.6), vec3(1.0, 1.0, 1.0), stripe);
    float gloss = smoothstep(0.6, 1.0, p.y / size.y);
    return vec4(col * (1.0 - 0.2 * gloss), 1.0);
}
