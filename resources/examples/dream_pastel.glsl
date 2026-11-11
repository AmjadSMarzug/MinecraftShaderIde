// a dreamy pastel sky: soft peach and lilac gradients with slow, puffy clouds drifting by
// dir is the direction the pixel looks in, time counts seconds

float dpHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float dpNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = mix(dpHash(i), dpHash(i + vec2(1.0, 0.0)), f.x);
    float b = mix(dpHash(i + vec2(0.0, 1.0)), dpHash(i + vec2(1.0, 1.0)), f.x);
    return mix(a, b, f.y);
}

vec3 sky(vec3 dir, float time) {
    vec3 peach = vec3(1.0, 0.8, 0.7);
    vec3 lilac = vec3(0.75, 0.7, 1.0);
    vec3 color = mix(peach, lilac, smoothstep(0.0, 0.8, dir.y));
    vec2 p = dir.xz / max(dir.y + 0.25, 0.25) * 1.2;
    float puff = dpNoise(p + vec2(time * 0.03, 0.0)) * 0.6 + dpNoise(p * 2.5) * 0.4;
    float cloud = smoothstep(0.5, 0.75, puff) * smoothstep(0.0, 0.2, dir.y);
    color = mix(color, vec3(1.0, 0.97, 0.98), cloud * 0.8);
    return color;
}
