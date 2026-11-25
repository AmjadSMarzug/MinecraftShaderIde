// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float glitchHash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 text(vec2 p, vec2 size, float time) {
    // every so often a band of rows jumps sideways, then snaps back
    float slot = floor(time * 12.0);
    float band = floor(p.y / 2.0);
    float jump = (glitchHash(vec2(band, slot)) - 0.5) * 3.0 * step(0.8, glitchHash(vec2(slot, 7.0)));
    float stripe = step(0.5, fract((p.x + jump) / 2.0));
    vec3 col = mix(vec3(1.0, 0.2, 0.2), vec3(0.2, 1.0, 1.0), stripe);
    return vec4(col, 1.0);
}
