// a faceted ice shard floating in the item, turning slowly with its edges catching a cold light
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

float ikOcta(vec3 p, float s) {
    return (abs(p.x) + abs(p.y) + abs(p.z) - s) * 0.57735;
}

vec3 ikRotY(vec3 p, float a) {
    float c = cos(a);
    float s = sin(a);
    return vec3(c * p.x + s * p.z, p.y, -s * p.x + c * p.z);
}

float ikSdf(vec3 p, float time) {
    vec3 q = ikRotY(p, time * 0.6);
    q.y *= 1.5;
    return ikOcta(q, 0.7);
}

vec4 object(vec3 origin, vec3 dir, float time) {
    float t = 0.0;
    for (int i = 0; i < 64; i++) {
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float d = ikSdf(p, time);
        if (d < 0.002) {
            vec3 n = normalize(vec3(ikSdf(p + vec3(0.003, 0.0, 0.0), time) - d,
                                    ikSdf(p + vec3(0.0, 0.003, 0.0), time) - d,
                                    ikSdf(p + vec3(0.0, 0.0, 0.003), time) - d));
            float fres = pow(1.0 - abs(dot(n, dir)), 2.0);
            vec3 ice = mix(vec3(0.35, 0.7, 1.0), vec3(0.9, 1.0, 1.0), fres);
            float shade = 0.4 + 0.6 * max(dot(n, normalize(vec3(0.4, 0.8, 0.5))), 0.0);
            return vec4(ice * shade, 1.0);
        }
        t += max(d, 0.01);
        if (t > 3.0) break;
    }
    return vec4(0.0);
}
