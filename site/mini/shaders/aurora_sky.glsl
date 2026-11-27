// a night sky with stars and a green and violet aurora rippling across the north

// Random value for a 2D point. Used to decide which grid cells hold a star.
float auHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec3 sky(vec3 dir, float time) {
    // Background: a deep night blue, slightly lighter toward the horizon.
    vec3 color = mix(vec3(0.01, 0.02, 0.06), vec3(0.02, 0.05, 0.12), smoothstep(-0.2, 0.8, dir.y));

    // Stars: split the sky into a grid and light about one cell in sixty as a small dot.
    vec2 s = dir.xz * 90.0;
    vec2 cell = floor(s);
    float star = step(0.985, auHash(cell)) * smoothstep(0.45, 0.0, length(fract(s) - 0.5));
    color += vec3(0.9, 0.95, 1.0) * star * smoothstep(0.0, 0.3, dir.y);

    // The aurora: two slow waves make a ribbon that brightens in places and fades in others.
    float wave = sin(dir.x * 5.0 + time * 0.4) * 0.5 + sin(dir.z * 7.0 - time * 0.3) * 0.5;
    float curtain = smoothstep(0.0, 0.15, dir.y) * smoothstep(0.7, 0.2, dir.y);
    float ribbon = smoothstep(0.4, 1.0, wave * 0.5 + 0.5);
    vec3 aurora = mix(vec3(0.2, 1.0, 0.5), vec3(0.6, 0.3, 1.0), smoothstep(-0.3, 0.6, dir.y + wave * 0.2));
    color += aurora * curtain * ribbon * 0.9;
    return color;
}
