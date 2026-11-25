// a storm sky: heavy dark clouds rolling overhead, with lightning flaring inside them every few seconds

// Random value for a 2D point, used by the noise below.
float scHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

// Smooth 2D value noise: blends the hashes of a grid cell's corners.
float scNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = mix(scHash(i), scHash(i + vec2(1.0, 0.0)), f.x);
    float b = mix(scHash(i + vec2(0.0, 1.0)), scHash(i + vec2(1.0, 1.0)), f.x);
    return mix(a, b, f.y);
}

// Cloud shape: three noise layers at different scales, added together.
float scFbm(vec2 p) {
    return scNoise(p) * 0.5 + scNoise(p * 2.1 + 3.7) * 0.3 + scNoise(p * 4.3 + 9.1) * 0.2;
}

vec3 sky(vec3 dir, float time) {
    // Dark sky, lit a little more toward the horizon.
    vec3 air = mix(vec3(0.15, 0.17, 0.22), vec3(0.04, 0.05, 0.08), smoothstep(-0.1, 0.7, dir.y));
    vec2 plane = dir.xz / max(dir.y + 0.2, 0.2);
    // Clouds: noise on a plane that the view projects onto, drifting slowly over time.
    float clouds = scFbm(plane * 1.5 + vec2(time * 0.05, 0.0));
    float cover = smoothstep(0.35, 0.8, clouds);
    vec3 cloudColour = mix(vec3(0.12, 0.13, 0.16), vec3(0.3, 0.32, 0.36), clouds);
    vec3 color = mix(air, cloudColour, cover);
    // Lightning: a short flash every few seconds, only visible inside the cloud cover.
    float bolt = pow(max(sin(time * 1.7), 0.0), 16.0);
    color += vec3(0.6, 0.7, 1.0) * bolt * cover * 0.8;
    return color;
}
