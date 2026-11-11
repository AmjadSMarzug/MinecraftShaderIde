// an ink wash painting: pale rice paper with soft grey clouds of ink drifting across it
// dir is the direction the pixel looks in, time counts seconds

float iwHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float iwNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = mix(iwHash(i), iwHash(i + vec2(1.0, 0.0)), f.x);
    float b = mix(iwHash(i + vec2(0.0, 1.0)), iwHash(i + vec2(1.0, 1.0)), f.x);
    return mix(a, b, f.y);
}

vec3 sky(vec3 dir, float time) {
    vec3 paper = vec3(0.93, 0.92, 0.88);
    vec2 p = dir.xz / max(dir.y + 0.4, 0.3) * 1.5;
    float ink = iwNoise(p + vec2(time * 0.02, 0.0)) * 0.6 + iwNoise(p * 3.0) * 0.4;
    float wash = smoothstep(0.45, 0.8, ink);
    vec3 color = mix(paper, vec3(0.25, 0.27, 0.3), wash * 0.7);
    color *= 0.92 + 0.08 * smoothstep(-0.2, 0.6, dir.y);
    return color;
}
