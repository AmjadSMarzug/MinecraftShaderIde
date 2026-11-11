// a glitching view: a few random horizontal bands jump sideways and flash in odd colours, then vanish
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float glHash(float p) {
    return fract(sin(p * 91.7) * 43758.5453);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    float slot = floor(screen.y * 24.0);
    float tick = floor(time * 6.0);
    float pick = glHash(slot + tick * 7.0);
    float band = step(0.82, pick);
    float shift = (glHash(slot + tick * 3.0) - 0.5) * 0.2;
    float wobble = step(0.5, glHash(tick + slot * 0.1)) * shift;
    vec3 colour = mix(vec3(1.0, 0.1, 0.6), vec3(0.1, 1.0, 1.0), glHash(slot + tick));
    return vec4(colour, band * 0.55 * (0.5 + 0.5 * abs(wobble) * 10.0));
}
