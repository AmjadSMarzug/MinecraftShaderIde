// web sky (purple): a sky of glowing Voronoi cells, like a web of light drifting past, with fog and stars behind it
// ported from the Smp Shaders pack. the pack's clock runs in game ticks, here time is seconds, so the speed is tuned to match

float wspHash(vec3 p) {
    p = fract(p * 0.3183099 + vec3(0.1, 0.2, 0.3));
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

vec3 wspVoronoi(vec3 p) {
    vec3 g = floor(p);
    vec3 pf = p - g;
    float f1 = 100.0;
    float f2 = 100.0;
    float cellId = 0.0;
    for (int z = -1; z <= 1; z++) {
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                vec3 off = vec3(float(x), float(y), float(z));
                float h = wspHash(g + off);
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

float wspNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = p - i;
    f = f * f * (3.0 - 2.0 * f);
    float n000 = wspHash(i);
    float n100 = wspHash(i + vec3(1.0, 0.0, 0.0));
    float n010 = wspHash(i + vec3(0.0, 1.0, 0.0));
    float n110 = wspHash(i + vec3(1.0, 1.0, 0.0));
    float n001 = wspHash(i + vec3(0.0, 0.0, 1.0));
    float n101 = wspHash(i + vec3(1.0, 0.0, 1.0));
    float n011 = wspHash(i + vec3(0.0, 1.0, 1.0));
    float n111 = wspHash(i + vec3(1.0, 1.0, 1.0));
    return mix(mix(mix(n000, n100, f.x), mix(n010, n110, f.x), f.y),
               mix(mix(n001, n101, f.x), mix(n011, n111, f.x), f.y), f.z);
}

float wspStars(vec3 dir) {
    vec3 sp = dir * 88.0;
    vec3 si = floor(sp);
    vec3 sf = sp - si;
    if (wspHash(si + vec3(11.0)) < 0.935) return 0.0;
    vec3 c = vec3(wspHash(si + vec3(3.0)), wspHash(si + vec3(5.0)), wspHash(si + vec3(9.0)));
    float d = length(sf - c);
    return smoothstep(0.18, 0.0, d) * (0.45 + 0.55 * wspHash(si + vec3(17.0)));
}

vec3 sky(vec3 dir, float time) {
    vec3 base = vec3(0.816, 0.0, 1.0);
    vec3 cellCore = base * 0.42;
    vec3 cellRim = mix(base, vec3(1.0), 0.22);
    vec3 bloomCol = mix(base, vec3(1.0), 0.62);
    vec3 hot = mix(base, vec3(1.0), 0.90);

    float t = time * 1.2;
    vec3 p = dir * 3.5 + vec3(t * 0.26, t * 0.06, t * 0.20);
    vec3 v = wspVoronoi(p);
    float w = v.x;

    vec3 color = mix(cellCore, cellRim, clamp(v.z * 1.35, 0.0, 1.0));
    color *= 0.88 + 0.26 * v.y;

    float fogm = wspNoise(p * 2.2 + vec3(t * 0.18, 0.0, t * 0.05)) * 0.65
               + wspNoise(p * 5.4 - vec3(0.0, t * 0.12, 0.0)) * 0.35;
    color *= 0.70 + 0.58 * fogm;

    // the web's boundaries glow in and out slowly, about every ten seconds, so the web breathes
    float breathe = mix(0.35, 1.0, 0.5 + 0.5 * sin(time * 0.6));
    float bloom = pow(1.0 - smoothstep(0.0, 0.16, w), 2.2) * breathe;
    float core = (1.0 - smoothstep(0.02, 0.06, w)) * breathe;
    color += bloomCol * bloom * 0.50;
    color += hot * core * 0.80;
    color += mix(base, vec3(1.0), 0.8) * wspStars(dir) * 1.2;
    return color;
}
