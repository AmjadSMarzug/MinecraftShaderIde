// orb (purple): a glowing purple ball with swirling bands of light moving across it and a bright rim
// ported from the Smp Shaders pack's orbs. origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space
// the pack's clock runs in game ticks, here time is seconds, so the speed is tuned to match

float orbHash(vec3 p) {
    p = fract(p * 0.3183099 + vec3(0.1, 0.2, 0.3));
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float orbNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = p - i;
    f = f * f * (3.0 - 2.0 * f);
    float n000 = orbHash(i);
    float n100 = orbHash(i + vec3(1.0, 0.0, 0.0));
    float n010 = orbHash(i + vec3(0.0, 1.0, 0.0));
    float n110 = orbHash(i + vec3(1.0, 1.0, 0.0));
    float n001 = orbHash(i + vec3(0.0, 0.0, 1.0));
    float n101 = orbHash(i + vec3(1.0, 0.0, 1.0));
    float n011 = orbHash(i + vec3(0.0, 1.0, 1.0));
    float n111 = orbHash(i + vec3(1.0, 1.0, 1.0));
    return mix(mix(mix(n000, n100, f.x), mix(n010, n110, f.x), f.y),
               mix(mix(n001, n101, f.x), mix(n011, n111, f.x), f.y), f.z);
}

vec4 object(vec3 origin, vec3 dir, float time) {
    vec3 base = vec3(0.816, 0.000, 1.000);
    vec3 glow = mix(base, vec3(1.0), 0.45);
    vec3 hot = mix(base, vec3(1.0), 0.88);
    vec3 dark = base * 0.18;

    float t = 0.0;
    for (int i = 0; i < 80; i++) {
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float d = length(p) - 0.55;
        if (d < 0.002) {
            vec3 n = normalize(p);
            float flow = time * 0.6;
            float g = clamp(orbNoise(n * 2.4 + vec3(flow * 0.14, flow * -0.11, flow * 0.5)) * 0.62
                          + orbNoise(n * 4.8 - vec3(flow * 0.18, 0.0, flow * 0.3)) * 0.34
                          + orbNoise(n * 9.0 + vec3(0.0, flow * 0.2, flow * 0.8)) * 0.24, 0.0, 1.0);
            vec3 body = mix(dark, base, smoothstep(0.12, 0.34, g));
            body = mix(body, glow, smoothstep(0.42, 0.64, g));
            body = mix(body, hot, smoothstep(0.72, 0.92, g));
            float facing = abs(dot(n, -dir));
            body = mix(body, hot, (1.0 - smoothstep(0.0, 0.40, facing)) * 0.45);
            float rim = pow(1.0 - facing, 3.0);
            return vec4(body + glow * rim * 0.55, 1.0);
        }
        t += d * 0.8;
        if (t > 3.0) break;
    }
    return vec4(0.0);
}
