// a tornado: a funnel of swirling dust that spins faster near the bottom and drifts up the sides
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

vec4 object(vec3 origin, vec3 dir, float time) {
    vec3 dust = vec3(0.0);
    float alpha = 0.0;
    for (int i = 0; i < 70; i++) {
        float t = float(i) * 0.04;
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float funnel = 0.08 + (p.y + 1.0) * 0.22;
        float ang = atan(p.z, p.x) + p.y * 6.0 - time * 4.0;
        float swirl = 0.5 + 0.5 * sin(ang * 3.0);
        float band = exp(-abs(length(p.xz) - funnel) * 35.0);
        float density = band * swirl * 0.25;
        vec3 color = mix(vec3(0.45, 0.4, 0.35), vec3(0.8, 0.75, 0.65), swirl);
        dust += color * density * (1.0 - alpha);
        alpha += density * (1.0 - alpha);
        if (alpha > 0.95) break;
    }
    return vec4(dust, clamp(alpha, 0.0, 1.0));
}
