// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float frostHash(vec2 p) {
    return fract(sin(dot(p, vec2(41.3, 289.1))) * 9371.7);
}

vec4 text(vec2 p, vec2 size, float time) {
    // icy blue letters with a sparkle that moves from pixel to pixel
    vec3 ice = mix(vec3(0.6, 0.85, 1.0), vec3(0.95, 1.0, 1.0), p.y / size.y);
    float sparkle = step(0.97, frostHash(floor(p) + floor(time * 4.0)));
    return vec4(ice + sparkle * vec3(1.0), 1.0);
}
