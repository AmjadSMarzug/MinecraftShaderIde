// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float rainHash(float n) {
    return fract(sin(n * 12.9898) * 43758.5453);
}

vec4 text(vec2 p, vec2 size, float time) {
    // each column of pixels has a streak of green that falls down the letter at its own speed
    float column = floor(p.x);
    float speed = 0.5 + rainHash(column) * 1.5;
    float streak = fract(p.y / size.y - time * speed + rainHash(column + 3.0));
    float bright = smoothstep(0.0, 0.3, streak) * (1.0 - smoothstep(0.7, 1.0, streak));
    return vec4(vec3(0.1, 1.0, 0.3) * bright, 1.0);
}
