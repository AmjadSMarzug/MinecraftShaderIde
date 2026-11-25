// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec3 hueColour(float h) {
    vec3 k = mod(vec3(0.0, 4.0, 2.0) + h * 6.0, 6.0);
    return clamp(abs(k - 3.0) - 1.0, 0.0, 1.0);
}

vec4 text(vec2 p, vec2 size, float time) {
    // the colour runs along the word and slides with time, so the rainbow appears to flow through the letters
    float h = fract(p.x / size.x * 0.5 + p.y / size.y * 0.1 - time * 0.3);
    return vec4(hueColour(h), 1.0);
}
