// a blood moon: a deep red-black sky with a huge dim red moon and a soft halo around it
// dir is the direction the pixel looks in, time counts seconds

vec3 sky(vec3 dir, float time) {
    vec3 color = mix(vec3(0.03, 0.0, 0.01), vec3(0.08, 0.01, 0.02), smoothstep(-0.1, 0.8, dir.y));
    vec3 moonDir = normalize(vec3(-0.2, 0.35, -1.0));
    float s = max(dot(normalize(dir), moonDir), 0.0);
    float disc = smoothstep(0.9965, 0.9975, s);
    float halo = pow(s, 14.0) * 0.5;
    float shade = 0.7 + 0.3 * sin(time * 0.4 + dir.x * 8.0);
    color += vec3(0.5, 0.05, 0.02) * (disc * shade + halo);
    return color;
}
