// a bead of water floating in the item: a clear drop that reflects a bright highlight off its top and shades blue at the edges
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

float wdDist(vec3 p) {
    return (length(p / vec3(0.5, 0.65, 0.5)) - 1.0) * 0.5;
}

vec4 object(vec3 origin, vec3 dir, float time) {
    float t = 0.0;
    for (int i = 0; i < 64; i++) {
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        vec3 c = p + vec3(0.0, sin(time) * 0.05, 0.0);
        float d = wdDist(c);
        if (d < 0.002) {
            vec3 n = normalize(vec3(wdDist(c + vec3(0.003, 0.0, 0.0)) - d,
                                    wdDist(c + vec3(0.0, 0.003, 0.0)) - d,
                                    wdDist(c + vec3(0.0, 0.0, 0.003)) - d));
            vec3 light = normalize(vec3(0.5, 0.8, 0.4));
            float spec = pow(max(dot(reflect(dir, n), light), 0.0), 40.0);
            float fres = pow(1.0 - abs(dot(n, dir)), 2.0);
            vec3 body = mix(vec3(0.1, 0.45, 0.9), vec3(0.6, 0.9, 1.0), fres);
            return vec4(body + vec3(1.0) * spec, 1.0);
        }
        t += max(d, 0.01);
        if (t > 3.0) break;
    }
    return vec4(0.0);
}
