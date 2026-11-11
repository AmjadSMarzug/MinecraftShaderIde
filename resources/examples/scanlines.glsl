// fine dark scanlines across the whole view, with one brighter line rolling slowly down the screen
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

vec4 overlay(vec2 screen, vec3 dir, float time) {
    float lines = 0.5 + 0.5 * sin(screen.y * 540.0);
    float roll = smoothstep(0.06, 0.0, abs(fract(screen.y - time * 0.15) - 0.5) * 2.0 - 0.5);
    float darkness = (1.0 - lines) * 0.35;
    vec3 color = vec3(0.0, 0.02, 0.0) + vec3(0.6, 1.0, 0.6) * roll * 0.5;
    return vec4(color, darkness + roll * 0.2);
}
