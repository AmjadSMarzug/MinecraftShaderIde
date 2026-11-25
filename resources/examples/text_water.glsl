// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float r = sin(p.x * 1.1 + time * 2.0) + sin(p.y * 1.3 - time * 1.7);
    vec3 deep = vec3(0.0, 0.2, 0.5);
    vec3 shallow = vec3(0.2, 0.7, 1.0);
    vec3 col = mix(deep, shallow, 0.5 + 0.25 * r);
    col += smoothstep(0.9, 1.0, abs(sin(r * 2.0))) * 0.3;
    return vec4(col, 1.0);
}
