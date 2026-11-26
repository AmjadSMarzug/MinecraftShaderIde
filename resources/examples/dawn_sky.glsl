// dawn: a cool blue sky at the top fading into pink and gold along the horizon, with the sun just coming up
// dir is the direction the pixel looks in, time counts seconds

vec3 sky(vec3 dir, float time) {
    float h = clamp(dir.y, -0.1, 1.0);
    vec3 top = vec3(0.25, 0.4, 0.7);
    vec3 pink = vec3(0.95, 0.55, 0.65);
    vec3 gold = vec3(1.0, 0.85, 0.5);
    vec3 color = mix(gold, pink, smoothstep(0.0, 0.12, h));
    color = mix(color, top, smoothstep(0.12, 0.6, h));
    vec3 sunDir = normalize(vec3(0.4, 0.05 + time * 0.005, -1.0));
    float s = max(dot(normalize(dir), sunDir), 0.0);
    color += vec3(1.0, 0.8, 0.5) * (pow(s, 300.0) * 2.0 + pow(s, 12.0) * 0.4);
    return color;
}
