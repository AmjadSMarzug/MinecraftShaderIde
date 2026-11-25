// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
float dsHash(vec2 p) { return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5); }
vec4 text(vec2 p, vec2 size, float time) {
    float life = 0.5 + 0.5 * sin(time);
    float grain = dsHash(floor(p));
    float keep = step(grain, life);
    vec3 col = mix(vec3(1.0, 0.6, 0.1), vec3(1.0, 0.2, 0.0), grain);
    return vec4(col, keep);
}
