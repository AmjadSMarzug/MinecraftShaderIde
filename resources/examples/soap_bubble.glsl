// a soap bubble: a thin shell that shimmers in rainbow colours, changing with the angle you look at it
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

vec3 sbRainbow(float h) {
    return 0.5 + 0.5 * cos(6.28318 * (vec3(1.0) * h + vec3(0.0, 0.33, 0.67)));
}

vec4 object(vec3 origin, vec3 dir, float time) {
    float t = 0.0;
    for (int i = 0; i < 90; i++) {
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float d = abs(length(p) - 0.6) - 0.012;
        if (d < 0.002) {
            vec3 n = normalize(p);
            float facing = abs(dot(n, dir));
            vec3 film = sbRainbow(fract(1.0 - facing + time * 0.05));
            float rim = pow(1.0 - facing, 2.0);
            return vec4(film * (0.6 + 0.6 * rim), 0.35 + 0.5 * rim);
        }
        t += max(d, 0.01);
        if (t > 3.0) break;
    }
    return vec4(0.0);
}
