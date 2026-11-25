// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float bulge = sin(p.x * 0.9 + time * 2.0) * 0.5 + sin(time * 3.0) * 0.3;
    float inside = smoothstep(0.0, 1.0, (p.y / size.y) + bulge * 0.1);
    vec3 col = mix(vec3(0.1, 0.5, 0.1), vec3(0.5, 1.0, 0.3), inside);
    return vec4(col, 0.9);
}
