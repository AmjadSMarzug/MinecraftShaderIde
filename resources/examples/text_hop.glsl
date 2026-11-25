// text effect: vec4 text(vec2 p, vec2 size, float time)
// p is the pixel inside the letter, 0 to size across and down (y goes down). size is the letter's own pixels, 8 for the game font.
// return the colour and alpha of that pixel. the game draws the letter's own shape over it, so only colour is set here
// time counts seconds
vec4 text(vec2 p, vec2 size, float time) {
    float column = floor(p.x / 2.0);
    float hop = max(sin(time * 5.0 - column * 0.9), 0.0);
    float lift = step(p.y, size.y - hop * 2.0);
    vec3 col = mix(vec3(0.3, 0.6, 1.0), vec3(1.0), hop);
    return vec4(col, lift);
}
