// a night sky with the milky way arching overhead: a dusty band of light full of faint stars
// dir is the direction the pixel looks in, time counts seconds

float mwHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec3 sky(vec3 dir, float time) {
    vec3 color = mix(vec3(0.005, 0.01, 0.03), vec3(0.01, 0.02, 0.06), smoothstep(-0.2, 0.9, dir.y));
    vec3 band = normalize(vec3(0.6, 0.0, -0.8));
    float across = abs(dot(dir, normalize(cross(band, vec3(0.0, 1.0, 0.0)))));
    float dust = exp(-across * 9.0) * (0.5 + 0.5 * mwHash(floor(dir.xz * 40.0)));
    vec2 s = dir.xz * 120.0;
    float star = step(0.992, mwHash(floor(s))) * smoothstep(0.4, 0.0, length(fract(s) - 0.5));
    color += vec3(0.5, 0.45, 0.6) * dust * 0.35;
    color += vec3(0.9, 0.9, 1.0) * star * smoothstep(-0.1, 0.3, dir.y);
    return color;
}
