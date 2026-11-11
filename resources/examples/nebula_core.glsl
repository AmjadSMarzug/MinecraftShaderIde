// a ball of glowing gas floating inside the item, swirling slowly and fading out towards its edge
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

float ncHash(vec3 p) {
    return fract(sin(dot(p, vec3(12.9898, 78.233, 37.719))) * 43758.5453);
}

float ncNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = mix(mix(ncHash(i), ncHash(i + vec3(1.0, 0.0, 0.0)), f.x),
                  mix(ncHash(i + vec3(0.0, 1.0, 0.0)), ncHash(i + vec3(1.0, 1.0, 0.0)), f.x), f.y);
    float b = mix(mix(ncHash(i + vec3(0.0, 0.0, 1.0)), ncHash(i + vec3(1.0, 0.0, 1.0)), f.x),
                  mix(ncHash(i + vec3(0.0, 1.0, 1.0)), ncHash(i + vec3(1.0, 1.0, 1.0)), f.x), f.y);
    return mix(a, b, f.z);
}

float ncFbm(vec3 p) {
    float sum = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 4; i++) {
        sum += amp * ncNoise(p);
        p = p * 2.03 + vec3(11.7, 3.1, 7.9);
        amp *= 0.5;
    }
    return sum;
}

vec4 object(vec3 origin, vec3 dir, float time) {
    vec3 light = vec3(0.0);
    float alpha = 0.0;
    for (int i = 0; i < 48; i++) {
        float t = float(i) * 0.05;
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float r = length(p);
        float falloff = smoothstep(0.85, 0.2, r);
        vec3 q = p * 2.2 + vec3(0.0, time * 0.25, time * 0.1);
        float density = falloff * ncFbm(q) * 0.22;
        vec3 hot = mix(vec3(1.0, 0.45, 0.15), vec3(0.6, 0.85, 1.0), smoothstep(0.2, 0.9, r));
        light += hot * density * (1.0 - alpha);
        alpha += density * (1.0 - alpha);
        if (alpha > 0.97) break;
    }
    return vec4(light, clamp(alpha, 0.0, 1.0));
}
