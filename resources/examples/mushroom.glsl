// a mushroom: a round red cap with white spots on a pale stem
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

float muCap(vec3 p) {
    return length((p - vec3(0.0, 0.15, 0.0)) * vec3(1.0, 1.6, 1.0)) - 0.4;
}

float muStem(vec3 p) {
    vec3 q = p - vec3(0.0, -0.25, 0.0);
    return max(length(q.xz) - 0.13, abs(q.y) - 0.3);
}

float muDist(vec3 p) {
    return min(muCap(p), muStem(p));
}

vec4 object(vec3 origin, vec3 dir, float time) {
    float t = 0.0;
    for (int i = 0; i < 80; i++) {
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float d = muDist(p);
        if (d < 0.002) {
            vec3 e = vec3(0.002, 0.0, 0.0);
            vec3 n = normalize(vec3(muDist(p + e.xyy) - d, muDist(p + e.yxy) - d, muDist(p + e.yyx) - d));
            float light = 0.35 + 0.65 * max(dot(n, normalize(vec3(0.5, 0.8, 0.3))), 0.0);
            vec3 col = p.y > 0.0 ? vec3(0.85, 0.12, 0.1) : vec3(0.9, 0.85, 0.75);
            float spot = step(0.8, fract(sin(dot(floor(p * 7.0), vec3(12.9898, 78.233, 37.719))) * 43758.5453));
            if (p.y > 0.0) col = mix(col, vec3(1.0), spot * 0.9);
            return vec4(col * light, 1.0);
        }
        t += d * 0.8;
        if (t > 3.0) break;
    }
    return vec4(0.0);
}
