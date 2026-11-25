// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float drip = 0.5 + 0.5 * sin(p.x * 1.3 + time * 2.0);
    float heat = clamp(p.y / size.y + 0.2 * drip, 0.0, 1.0);
    vec3 toxic = mix(vec3(0.6, 1.0, 0.1), vec3(0.1, 0.5, 0.0), heat);
    // bubbles pop at random pixels, changing a few times a second
    vec2 cell = floor(p);
    float bubble = step(0.92, fract(sin(dot(cell, vec2(12.9, 78.2)) + floor(time * 3.0)) * 43758.5));
    return vec4(toxic + bubble * 0.4, 1.0);
}
