//@each colour: hue_00 = vec3(1.000, 0.050, 0.050); hue_01 = vec3(1.000, 0.431, 0.300); hue_02 = vec3(1.000, 0.406, 0.050); hue_03 = vec3(1.000, 0.694, 0.300); hue_04 = vec3(1.000, 0.762, 0.050); hue_05 = vec3(1.000, 0.956, 0.300); hue_06 = vec3(0.881, 1.000, 0.050); hue_07 = vec3(0.781, 1.000, 0.300); hue_08 = vec3(0.420, 0.800, 0.040); hue_09 = vec3(0.415, 0.800, 0.240); hue_10 = vec3(0.135, 0.800, 0.040); hue_11 = vec3(0.240, 0.800, 0.275); hue_12 = vec3(0.040, 0.800, 0.230); hue_13 = vec3(0.240, 0.800, 0.485); hue_14 = vec3(0.040, 0.800, 0.515); hue_15 = vec3(0.240, 0.800, 0.695); hue_16 = vec3(0.050, 1.000, 1.000); hue_17 = vec3(0.300, 0.869, 1.000); hue_18 = vec3(0.050, 0.644, 1.000); hue_19 = vec3(0.300, 0.606, 1.000); hue_20 = vec3(0.050, 0.288, 1.000); hue_21 = vec3(0.300, 0.344, 1.000); hue_22 = vec3(0.169, 0.050, 1.000); hue_23 = vec3(0.519, 0.300, 1.000); hue_24 = vec3(0.420, 0.040, 0.800); hue_25 = vec3(0.625, 0.240, 0.800); hue_26 = vec3(0.705, 0.040, 0.800); hue_27 = vec3(0.800, 0.240, 0.765); hue_28 = vec3(0.800, 0.040, 0.610); hue_29 = vec3(0.800, 0.240, 0.555); hue_30 = vec3(0.800, 0.040, 0.325); hue_31 = vec3(0.800, 0.240, 0.345)
//@name swirl_{colour.name}
// BulkEffect Example: a swirling vignette in 32 colours. the template makes one effect per colour, named swirl_hue_00 to swirl_hue_31.
// swirling vignette (cyan): the edges of the screen swirl with noise that drifts and wobbles, like a colour washing in from the sides
// ported from the Smp Shaders pack's coloured vignettes. screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float svcRandom(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

float svcNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(svcRandom(i), svcRandom(i + vec2(1.0, 0.0)), u.x),
               mix(svcRandom(i + vec2(0.0, 1.0)), svcRandom(i + vec2(1.0, 1.0)), u.x), u.y);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    vec2 c = (screen - 0.5) * vec2(1.8, 1.0);
    float edge = smoothstep(0.35, 0.8, length(c));
    vec2 point = screen * vec2(180.0, 100.0);
    float swell = sin(point.x * 0.08 + time * 1.6) * 1.4 + sin(point.y * 0.10 - time * 1.2) * 1.4;
    float chop = sin(point.x * 0.35 - time * 4.5) * 0.7 + sin(point.y * 0.30 + time * 5.2) * 0.7;
    vec2 wobbled = point + vec2(swell + chop, (swell - chop) * 0.6);
    float n = svcNoise(wobbled * 0.05 + vec2(time * -1.8, 0.0));
    float n2 = svcNoise(wobbled * 0.07 + vec2(time * 1.1, time * -0.9));
    float mixv = clamp(n * n * 0.7 + n2 * 0.3, 0.0, 1.0);
    vec3 dark = {colour} * 0.13;
    vec3 base = {colour};
    vec3 color = mix(dark, base, mixv);
    color = mix(color, vec3(0.9, 1.0, 1.0), smoothstep(0.8, 1.0, mixv) * 0.5);
    return vec4(color, edge * 0.85);
}
