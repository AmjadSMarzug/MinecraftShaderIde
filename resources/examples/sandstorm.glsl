// a sandstorm rushing past: tan streaks of grit blowing sideways across the view, thicker towards the edges
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float sdHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    vec2 p = vec2(screen.x * 6.0 - time * 3.0, screen.y * 60.0);
    float streak = step(0.6, sdHash(vec2(floor(p.y), floor(p.x * 0.2))));
    float grit = streak * (0.5 + 0.5 * sdHash(floor(p * 2.0)));
    float edge = smoothstep(0.2, 0.7, length(screen - 0.5));
    return vec4(vec3(0.85, 0.7, 0.45), grit * (0.15 + edge * 0.5));
}
