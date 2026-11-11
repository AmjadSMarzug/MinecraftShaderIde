// desert dusk: an orange sky going to gold at the horizon, with rolling dune silhouettes in dark amber
// dir is the direction the pixel looks in, time counts seconds

vec3 sky(vec3 dir, float time) {
    vec3 top = vec3(0.35, 0.2, 0.3);
    vec3 orange = vec3(0.95, 0.5, 0.2);
    vec3 gold = vec3(1.0, 0.8, 0.4);
    vec3 color = mix(gold, orange, smoothstep(0.0, 0.2, dir.y));
    color = mix(color, top, smoothstep(0.2, 0.8, dir.y));
    float dune = 0.02 + 0.03 * sin(dir.x * 5.0 + time * 0.05) + 0.02 * sin(dir.x * 13.0);
    if (dir.y < dune) color = mix(vec3(0.25, 0.12, 0.05), vec3(0.4, 0.2, 0.08), dir.y + 0.1);
    return color;
}
