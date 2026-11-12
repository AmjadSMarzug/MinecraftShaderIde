// eclipse: a dim violet sky with a dark moon drifting across the sun's corona and a few bright stars showing through
// ported from the Smp Shaders pack's eclipse sky. the pack's clock runs in game ticks, here time is seconds

float ecpHash(vec3 p) {
    p = fract(p * 0.3183099 + vec3(0.1, 0.2, 0.3));
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float ecpNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = p - i;
    f = f * f * (3.0 - 2.0 * f);
    float n000 = ecpHash(i);
    float n100 = ecpHash(i + vec3(1.0, 0.0, 0.0));
    float n010 = ecpHash(i + vec3(0.0, 1.0, 0.0));
    float n110 = ecpHash(i + vec3(1.0, 1.0, 0.0));
    float n001 = ecpHash(i + vec3(0.0, 0.0, 1.0));
    float n101 = ecpHash(i + vec3(1.0, 0.0, 1.0));
    float n011 = ecpHash(i + vec3(0.0, 1.0, 1.0));
    float n111 = ecpHash(i + vec3(1.0, 1.0, 1.0));
    return mix(mix(mix(n000, n100, f.x), mix(n010, n110, f.x), f.y),
               mix(mix(n001, n101, f.x), mix(n011, n111, f.x), f.y), f.z);
}

float ecpStars(vec3 dir) {
    vec3 sp = dir * 88.0;
    vec3 si = floor(sp);
    vec3 sf = sp - si;
    if (ecpHash(si + vec3(11.0)) < 0.935) return 0.0;
    vec3 c = vec3(ecpHash(si + vec3(3.0)), ecpHash(si + vec3(5.0)), ecpHash(si + vec3(9.0)));
    return smoothstep(0.18, 0.0, length(sf - c)) * (0.45 + 0.55 * ecpHash(si + vec3(17.0)));
}

vec3 sky(vec3 dir, float time) {
    vec3 eclipseSky = vec3(0.15, 0.1, 0.2);
    vec3 nightSky = vec3(0.05, 0.03, 0.1);
    float t = time * 0.5;
    float moonX = sin(t * 0.3) * 0.2;
    float moonY = 0.35;

    vec2 toMoon = dir.xy - vec2(moonX, moonY);
    float distToMoon = length(toMoon);
    float moonDisk = smoothstep(0.32, 0.28, distToMoon);
    float shadowEdge = smoothstep(0.31, 0.26, distToMoon);

    vec3 lunarColor = vec3(0.2, 0.1, 0.08);
    vec3 shadowedMoon = vec3(0.3, 0.15, 0.1);
    vec3 moonColor = mix(lunarColor, shadowedMoon, shadowEdge * 0.7);
    moonColor = mix(moonColor, vec3(0.4, 0.2, 0.15), smoothstep(0.3, 0.26, distToMoon) * 0.8);

    float umbra = smoothstep(0.28, 0.25, distToMoon);
    float penumbra = smoothstep(0.32, 0.28, distToMoon) * (1.0 - umbra);

    vec3 coronaCore = vec3(0.95, 0.85, 0.7) * smoothstep(0.35, 0.32, distToMoon) * (1.0 - moonDisk) * 2.0;
    vec3 coronaWide = vec3(0.8, 0.7, 0.6) * smoothstep(0.45, 0.35, distToMoon) * (1.0 - moonDisk) * 0.8;

    vec3 color = mix(eclipseSky, nightSky, penumbra * 0.6);
    color = mix(color, moonColor, moonDisk);
    color += coronaCore + coronaWide;

    float brightStars = ecpStars(dir) * (0.7 + umbra * 0.3);
    color += vec3(0.95, 0.92, 1.0) * brightStars * 0.9;
    return color;
}
