// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    vec2 c = size * 0.5;
    vec2 orbit = c + vec2(cos(time * 2.0), sin(time * 2.0)) * 3.0;
    float glow = 1.0 - smoothstep(0.0, 3.0, length(p - orbit));
    vec3 col = mix(vec3(0.5, 0.1, 0.8), vec3(0.9, 0.5, 1.0), glow);
    return vec4(col, 1.0);
}
