// a ball of lightning: blue arcs threading through a glowing sphere, crawling around as time passes
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

float loHash(vec3 p) {
    return fract(sin(dot(p, vec3(12.9898, 78.233, 37.719))) * 43758.5453);
}

float loNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = mix(mix(loHash(i), loHash(i + vec3(1.0, 0.0, 0.0)), f.x),
                  mix(loHash(i + vec3(0.0, 1.0, 0.0)), loHash(i + vec3(1.0, 1.0, 0.0)), f.x), f.y);
    float b = mix(mix(loHash(i + vec3(0.0, 0.0, 1.0)), loHash(i + vec3(1.0, 0.0, 1.0)), f.x),
                  mix(loHash(i + vec3(0.0, 1.0, 1.0)), loHash(i + vec3(1.0, 1.0, 1.0)), f.x), f.y);
    return mix(a, b, f.z);
}

vec4 object(vec3 origin, vec3 dir, float time) {
    vec3 light = vec3(0.0);
    float alpha = 0.0;
    for (int i = 0; i < 60; i++) {
        float t = float(i) * 0.05;
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float r = length(p);
        if (r < 0.7) {
            float n = loNoise(p * 5.0 + vec3(time * 2.0));
            float bolt = exp(-abs(n - 0.5) * 40.0);
            vec3 c = vec3(0.6, 0.8, 1.0) * bolt * 0.5 + vec3(0.1, 0.15, 0.3) * 0.05;
            light += c * (1.0 - alpha);
            alpha += bolt * 0.1 * (1.0 - alpha);
        }
        if (alpha > 0.95) break;
    }
    return vec4(light, clamp(alpha, 0.0, 1.0));
}
