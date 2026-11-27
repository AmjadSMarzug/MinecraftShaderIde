// a warm sunset: a sun sinking into a gradient of orange and purple, with soft clouds drifting past its glow

vec3 sky(vec3 dir, float time) {
    // The gradient: gold at the horizon, orange above it, and deep purple at the top.
    float h = clamp(dir.y, -0.2, 1.0);
    vec3 top = vec3(0.25, 0.12, 0.35);
    vec3 mid = vec3(0.95, 0.45, 0.25);
    vec3 low = vec3(1.0, 0.8, 0.45);
    vec3 color = mix(low, mid, smoothstep(0.0, 0.25, h));
    color = mix(color, top, smoothstep(0.25, 0.9, h));

    // The sun: a sharp disc and a soft halo around it, both pointing at sunDir.
    vec3 d = normalize(dir);
    vec3 sunDir = normalize(vec3(0.3, 0.12, -1.0));
    float sunAmount = dot(d, sunDir);
    float sun = smoothstep(0.985, 0.995, sunAmount);
    float halo = pow(max(sunAmount, 0.0), 24.0);
    color += vec3(1.0, 0.85, 0.5) * (sun * 2.0 + halo * 0.6);

    // Clouds: a few soft bands that drift slowly across the glow.
    float band = sin(dir.x * 6.0 + time * 0.1) * 0.5 + 0.5;
    float cloud = smoothstep(0.6, 0.9, band) * smoothstep(0.02, 0.2, dir.y) * smoothstep(0.5, 0.1, dir.y);
    color = mix(color, vec3(0.85, 0.4, 0.35), cloud * 0.4);
    return color;
}
