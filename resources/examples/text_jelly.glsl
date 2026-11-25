// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float wobble = sin(time * 6.0 + p.y * 0.8) * 0.5;
    vec3 col = mix(vec3(1.0, 0.3, 0.6), vec3(1.0, 0.7, 0.9), 0.5 + 0.5 * sin(p.x + wobble));
    float shine = smoothstep(0.7, 1.0, 1.0 - length(p / size - vec2(0.3, 0.3)));
    return vec4(col + shine * 0.4, 0.85);
}
