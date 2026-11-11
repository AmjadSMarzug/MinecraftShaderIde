// a foggy morning: soft grey-blue air that thickens toward the ground, with a pale sun hidden behind the haze
// dir is the direction the pixel looks in, time counts seconds

float fmHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float fmNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = mix(fmHash(i), fmHash(i + vec2(1.0, 0.0)), f.x);
    float b = mix(fmHash(i + vec2(0.0, 1.0)), fmHash(i + vec2(1.0, 1.0)), f.x);
    return mix(a, b, f.y);
}

vec3 sky(vec3 dir, float time) {
    vec3 air = mix(vec3(0.6, 0.66, 0.72), vec3(0.45, 0.55, 0.68), smoothstep(-0.1, 0.5, dir.y));
    float wisp = fmNoise(dir.xz / max(dir.y + 0.3, 0.2) * 2.0 + vec2(time * 0.05, 0.0));
    float haze = smoothstep(0.3, 0.9, wisp) * smoothstep(0.5, -0.05, dir.y);
    vec3 color = mix(air, vec3(0.85, 0.87, 0.9), haze * 0.6);
    float sun = max(dot(normalize(dir), normalize(vec3(0.2, 0.25, -1.0))), 0.0);
    color += vec3(1.0, 0.95, 0.85) * pow(sun, 40.0) * 0.35;
    return color;
}
