//@each colour: hue_00 = vec3(1.000, 0.050, 0.050); hue_01 = vec3(1.000, 0.431, 0.300); hue_02 = vec3(1.000, 0.406, 0.050); hue_03 = vec3(1.000, 0.694, 0.300); hue_04 = vec3(1.000, 0.762, 0.050); hue_05 = vec3(1.000, 0.956, 0.300); hue_06 = vec3(0.881, 1.000, 0.050); hue_07 = vec3(0.781, 1.000, 0.300); hue_08 = vec3(0.420, 0.800, 0.040); hue_09 = vec3(0.415, 0.800, 0.240); hue_10 = vec3(0.135, 0.800, 0.040); hue_11 = vec3(0.240, 0.800, 0.275); hue_12 = vec3(0.040, 0.800, 0.230); hue_13 = vec3(0.240, 0.800, 0.485); hue_14 = vec3(0.040, 0.800, 0.515); hue_15 = vec3(0.240, 0.800, 0.695); hue_16 = vec3(0.050, 1.000, 1.000); hue_17 = vec3(0.300, 0.869, 1.000); hue_18 = vec3(0.050, 0.644, 1.000); hue_19 = vec3(0.300, 0.606, 1.000); hue_20 = vec3(0.050, 0.288, 1.000); hue_21 = vec3(0.300, 0.344, 1.000); hue_22 = vec3(0.169, 0.050, 1.000); hue_23 = vec3(0.519, 0.300, 1.000); hue_24 = vec3(0.420, 0.040, 0.800); hue_25 = vec3(0.625, 0.240, 0.800); hue_26 = vec3(0.705, 0.040, 0.800); hue_27 = vec3(0.800, 0.240, 0.765); hue_28 = vec3(0.800, 0.040, 0.610); hue_29 = vec3(0.800, 0.240, 0.555); hue_30 = vec3(0.800, 0.040, 0.325); hue_31 = vec3(0.800, 0.240, 0.345)
//@name web_sky_{colour.name}
// BulkEffect Example: a web sky in 32 colours. the template makes one effect per colour, named web_sky_hue_00 to web_sky_hue_31
// ported from the Smp Shaders pack. the pack's clock runs in game ticks, here time is seconds, so the speed is tuned to match

float wsaHash(vec3 p) {
    p = fract(p * 0.3183099 + vec3(0.1, 0.2, 0.3));
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

vec3 wsaVoronoi(vec3 p) {
    vec3 g = floor(p);
    vec3 pf = p - g;
    float f1 = 100.0;
    float f2 = 100.0;
    float cellId = 0.0;
    for (int z = -1; z <= 1; z++) {
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                vec3 off = vec3(float(x), float(y), float(z));
                float h = wsaHash(g + off);
                vec3 o = off + h - pf;
                float d2 = dot(o, o);
                if (d2 < f1) {
                    f2 = f1;
                    f1 = d2;
                    cellId = h;
                } else if (d2 < f2) {
                    f2 = d2;
                }
            }
        }
    }
    float d1 = sqrt(f1);
    return vec3(clamp((sqrt(f2) - d1) / 1.4142, 0.0, 1.0), cellId, d1);
}

float wsaNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = p - i;
    f = f * f * (3.0 - 2.0 * f);
    float n000 = wsaHash(i);
    float n100 = wsaHash(i + vec3(1.0, 0.0, 0.0));
    float n010 = wsaHash(i + vec3(0.0, 1.0, 0.0));
    float n110 = wsaHash(i + vec3(1.0, 1.0, 0.0));
    float n001 = wsaHash(i + vec3(0.0, 0.0, 1.0));
    float n101 = wsaHash(i + vec3(1.0, 0.0, 1.0));
    float n011 = wsaHash(i + vec3(0.0, 1.0, 1.0));
    float n111 = wsaHash(i + vec3(1.0, 1.0, 1.0));
    return mix(mix(mix(n000, n100, f.x), mix(n010, n110, f.x), f.y),
               mix(mix(n001, n101, f.x), mix(n011, n111, f.x), f.y), f.z);
}

float wsaStars(vec3 dir) {
    vec3 sp = dir * 88.0;
    vec3 si = floor(sp);
    vec3 sf = sp - si;
    if (wsaHash(si + vec3(11.0)) < 0.935) return 0.0;
    vec3 c = vec3(wsaHash(si + vec3(3.0)), wsaHash(si + vec3(5.0)), wsaHash(si + vec3(9.0)));
    float d = length(sf - c);
    return smoothstep(0.18, 0.0, d) * (0.45 + 0.55 * wsaHash(si + vec3(17.0)));
}

vec3 sky(vec3 dir, float time) {
    vec3 base = {colour};
    vec3 cellCore = base * 0.42;
    vec3 cellRim = mix(base, vec3(1.0), 0.22);
    vec3 bloomCol = mix(base, vec3(1.0), 0.62);
    vec3 hot = mix(base, vec3(1.0), 0.90);

    float t = time * 1.2;
    vec3 p = dir * 3.5 + vec3(t * 0.26, t * 0.06, t * 0.20);
    vec3 v = wsaVoronoi(p);
    float w = v.x;

    vec3 color = mix(cellCore, cellRim, clamp(v.z * 1.35, 0.0, 1.0));
    color *= 0.88 + 0.26 * v.y;

    float fogm = wsaNoise(p * 2.2 + vec3(t * 0.18, 0.0, t * 0.05)) * 0.65
               + wsaNoise(p * 5.4 - vec3(0.0, t * 0.12, 0.0)) * 0.35;
    color *= 0.70 + 0.58 * fogm;

    float bloom = pow(1.0 - smoothstep(0.0, 0.16, w), 2.2);
    float core = 1.0 - smoothstep(0.02, 0.06, w);
    color += bloomCol * bloom * 0.50;
    color += hot * core * 0.80;
    color += mix(base, vec3(1.0), 0.8) * wsaStars(dir) * 1.2;
    return color;
}
