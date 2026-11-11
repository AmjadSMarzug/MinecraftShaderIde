// a golden pyramid relic: a square-based pyramid with bright gold edges and a slow shine across the faces
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

float pyDist(vec3 p) {
    float sides = abs(p.x) + abs(p.z) - (0.45 - p.y) * 0.9;
    float caps = max(-(p.y + 0.35), p.y - 0.45);
    return max(sides, caps);
}

vec4 object(vec3 origin, vec3 dir, float time) {
    float t = 0.0;
    for (int i = 0; i < 80; i++) {
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float d = pyDist(p);
        if (d < 0.002) {
            vec3 e = vec3(0.002, 0.0, 0.0);
            vec3 n = normalize(vec3(pyDist(p + e.xyy) - d, pyDist(p + e.yxy) - d, pyDist(p + e.yyx) - d));
            float light = 0.3 + 0.7 * max(dot(n, normalize(vec3(0.5, 0.9, 0.4))), 0.0);
            float edge = smoothstep(0.02, 0.0, abs(abs(p.x) - abs(p.z)));
            float shine = pow(max(dot(reflect(-normalize(p), n), normalize(vec3(-0.6, 0.6, 0.2))), 0.0), 20.0);
            vec3 gold = vec3(0.85, 0.65, 0.2) * light + vec3(1.0, 0.9, 0.5) * (edge * 0.6 + shine * 0.4 * (0.5 + 0.5 * sin(time)));
            return vec4(gold, 1.0);
        }
        t += d * 0.8;
        if (t > 3.0) break;
    }
    return vec4(0.0);
}
