// a red alarm vignette that pulses in from the edges of the view, like a warning light
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

vec4 overlay(vec2 screen, vec3 dir, float time) {
    vec2 c = screen - vec2(0.5);
    float edge = smoothstep(0.2, 0.75, length(c) * 1.4);
    float pulse = 0.55 + 0.45 * sin(time * 4.0);
    return vec4(vec3(0.9, 0.05, 0.05), edge * pulse * 0.85);
}
