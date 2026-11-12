// fiery sky: the heavens on fire, with flames rolling through dark red, orange and yellow and a white hot core that pulses
// ported from the Smp Shaders pack's fiery sky. the pack's clock runs in game ticks, here time is seconds

float fyHash(vec3 p) {
    p = fract(p * 0.3183099 + vec3(0.1, 0.2, 0.3));
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float fyNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = p - i;
    f = f * f * (3.0 - 2.0 * f);
    float n000 = fyHash(i);
    float n100 = fyHash(i + vec3(1.0, 0.0, 0.0));
    float n010 = fyHash(i + vec3(0.0, 1.0, 0.0));
    float n110 = fyHash(i + vec3(1.0, 1.0, 0.0));
    float n001 = fyHash(i + vec3(0.0, 0.0, 1.0));
    float n101 = fyHash(i + vec3(1.0, 0.0, 1.0));
    float n011 = fyHash(i + vec3(0.0, 1.0, 1.0));
    float n111 = fyHash(i + vec3(1.0, 1.0, 1.0));
    return mix(mix(mix(n000, n100, f.x), mix(n010, n110, f.x), f.y),
               mix(mix(n001, n101, f.x), mix(n011, n111, f.x), f.y), f.z);
}

vec3 sky(vec3 dir, float time) {
    vec3 dark = vec3(0.15, 0.05, 0.0);
    vec3 red = vec3(1.0, 0.1, 0.0);
    vec3 orange = vec3(1.0, 0.5, 0.0);
    vec3 yellow = vec3(1.0, 1.0, 0.2);
    vec3 white = vec3(1.0, 1.0, 1.0);

    float t = time * 0.7;
    vec3 p = dir * 4.0 + vec3(t * 0.1, t * 0.12, t * 0.06);
    float fire1 = fyNoise(p * 1.5 + vec3(t * 0.15, -t * 0.08, 0.0));
    float fire2 = fyNoise(p * 2.8 + vec3(-t * 0.1, t * 0.12, 0.0));
    float fire3 = fyNoise(p * 5.5 - vec3(0.0, t * 0.08, t * 0.06));
    float fire4 = fyNoise(p * 0.8 + vec3(t * 0.06, t * 0.04, 0.0));

    float flames = fire1 * 0.35 + fire2 * 0.35 + fire3 * 0.2 + fire4 * 0.1;
    flames = smoothstep(0.15, 0.95, flames);

    vec3 fireColor = mix(dark, red, smoothstep(0.0, 0.4, flames));
    fireColor = mix(fireColor, orange, smoothstep(0.35, 0.6, flames));
    fireColor = mix(fireColor, yellow, smoothstep(0.55, 0.8, flames));
    fireColor = mix(fireColor, white, smoothstep(0.75, 1.0, flames) * 0.7);

    float coreGlow = pow(flames, 2.0) * 0.8;
    fireColor += yellow * coreGlow * 0.6;
    fireColor += white * pow(flames, 3.0) * 0.3;

    float turbulence = fyNoise(p * 2.2 + vec3(t * 0.2, 0.0, 0.0));
    fireColor *= 0.85 + turbulence * 0.3;
    fireColor *= abs(sin(t * 0.4)) * 0.3 + 0.7;
    fireColor += orange * pow(flames, 1.5) * 0.4;
    return fireColor;
}
