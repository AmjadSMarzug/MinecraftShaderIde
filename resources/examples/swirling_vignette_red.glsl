// swirling vignette (red): the edges of the screen swirl with noise that drifts and wobbles, like a colour washing in from the sides
// ported from the Smp Shaders pack's coloured vignettes. screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float svrRandom(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

float svrNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(svrRandom(i), svrRandom(i + vec2(1.0, 0.0)), u.x),
               mix(svrRandom(i + vec2(0.0, 1.0)), svrRandom(i + vec2(1.0, 1.0)), u.x), u.y);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    vec2 c = (screen - 0.5) * vec2(1.8, 1.0);
    float edge = smoothstep(0.35, 0.8, length(c));
    vec2 point = screen * vec2(180.0, 100.0);
    float swell = sin(point.x * 0.08 + time * 1.6) * 1.4 + sin(point.y * 0.10 - time * 1.2) * 1.4;
    float chop = sin(point.x * 0.35 - time * 4.5) * 0.7 + sin(point.y * 0.30 + time * 5.2) * 0.7;
    vec2 wobbled = point + vec2(swell + chop, (swell - chop) * 0.6);
    float n = svrNoise(wobbled * 0.05 + vec2(time * -1.8, 0.0));
    float n2 = svrNoise(wobbled * 0.07 + vec2(time * 1.1, time * -0.9));
    float mixv = clamp(n * n * 0.7 + n2 * 0.3, 0.0, 1.0);
    vec3 dark = vec3(0.15, 0.002, 0.002);
    vec3 base = vec3(0.945, 0.012, 0.012);
    vec3 color = mix(dark, base, mixv);
    color = mix(color, vec3(0.9, 1.0, 1.0), smoothstep(0.8, 1.0, mixv) * 0.5);
    return vec4(color, edge * 0.85);
}
