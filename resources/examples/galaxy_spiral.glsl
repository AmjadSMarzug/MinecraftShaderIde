// a spiral galaxy turning slowly inside the item: bright arms where the stars crowd together, a hot core and sparkling dust
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

float gxHash(vec3 p) {
    return fract(sin(dot(p, vec3(12.9898, 78.233, 37.719))) * 43758.5453);
}

vec4 object(vec3 origin, vec3 dir, float time) {
    vec3 light = vec3(0.0);
    float alpha = 0.0;
    for (int i = 0; i < 56; i++) {
        float t = float(i) * 0.045;
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float radius = length(p.xz);
        float ang = atan(p.z, p.x) + radius * 4.0 - time * 0.3;
        float arms = 0.5 + 0.5 * cos(2.0 * ang);
        float disc = exp(-abs(p.y) * 14.0) * exp(-radius * 2.2);
        float core = exp(-length(p) * 6.0);
        float sparkle = step(0.97, gxHash(floor(p * 60.0))) * disc;
        float density = disc * (0.25 + arms) * 0.5 + core * 0.4 + sparkle;
        vec3 color = mix(vec3(0.5, 0.6, 1.0), vec3(1.0, 0.85, 0.6), arms);
        light += color * density * (1.0 - alpha);
        alpha += density * (1.0 - alpha);
        if (alpha > 0.97) break;
    }
    return vec4(light, clamp(alpha, 0.0, 1.0));
}
