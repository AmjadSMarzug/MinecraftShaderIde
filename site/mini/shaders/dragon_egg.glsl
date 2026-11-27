// a dragon egg: a speckled green shell with scales, and glowing cracks that pulse with time
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

float deHash(vec3 p) {
    return fract(sin(dot(p, vec3(12.9898, 78.233, 37.719))) * 43758.5453);
}

float deDist(vec3 p) {
    return (length(p / vec3(0.42, 0.56, 0.42)) - 1.0) * 0.42;
}

vec4 object(vec3 origin, vec3 dir, float time) {
    float t = 0.0;
    for (int i = 0; i < 80; i++) {
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float d = deDist(p);
        if (d < 0.002) {
            vec3 e = vec3(0.002, 0.0, 0.0);
            vec3 n = normalize(vec3(deDist(p + e.xyy) - d, deDist(p + e.yxy) - d, deDist(p + e.yyx) - d));
            float light = 0.3 + 0.7 * max(dot(n, normalize(vec3(0.4, 0.8, 0.5))), 0.0);
            float scale = deHash(floor(p * 12.0));
            vec3 shell = vec3(0.2, 0.5, 0.3) * (0.7 + 0.3 * scale);
            float crack = step(0.97, deHash(floor(p * 4.0)));
            vec3 glowing = vec3(1.0, 0.5, 0.1) * crack * (0.6 + 0.4 * sin(time * 4.0));
            return vec4(shell * light + glowing, 1.0);
        }
        t += d * 0.8;
        if (t > 3.0) break;
    }
    return vec4(0.0);
}
