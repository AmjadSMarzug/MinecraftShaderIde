// polar day: the sun never sets, so it circles just above the horizon, lighting a pale icy sky
// dir is the direction the pixel looks in, time counts seconds

vec3 sky(vec3 dir, float time) {
    vec3 color = mix(vec3(0.75, 0.88, 1.0), vec3(0.3, 0.55, 0.9), smoothstep(0.0, 0.7, dir.y));
    float a = time * 0.1;
    vec3 sunDir = normalize(vec3(cos(a), 0.12, sin(a)));
    float s = max(dot(normalize(dir), sunDir), 0.0);
    color += vec3(1.0, 0.97, 0.85) * (pow(s, 400.0) * 3.0 + pow(s, 6.0) * 0.3);
    return color;
}
