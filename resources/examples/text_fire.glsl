// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float fireNoise(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec4 text(vec2 p, vec2 size, float time) {
    // hot at the bottom of each letter, cooling to dark red at the top, with flicker that rises
    float height = 1.0 - p.y / size.y;
    float flicker = fireNoise(vec2(floor(p.x), floor(p.y - time * 6.0)));
    float heat = clamp(height * 0.8 + flicker * 0.3, 0.0, 1.0);
    vec3 col = mix(vec3(0.4, 0.0, 0.0), vec3(1.0, 0.9, 0.2), heat);
    return vec4(col, 1.0);
}
