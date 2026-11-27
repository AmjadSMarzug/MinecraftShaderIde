// an ender pearl: a teal sphere with purple bands that drift around it, lit from one side
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

vec4 object(vec3 origin, vec3 dir, float time) {
    float t = 0.0;
    for (int i = 0; i < 80; i++) {
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float d = length(p) - 0.5;
        if (d < 0.002) {
            vec3 n = normalize(p);
            float light = max(dot(n, normalize(vec3(0.6, 0.7, 0.3))), 0.0);
            float band = 0.5 + 0.5 * sin(n.y * 9.0 + time * 1.5 + sin(n.x * 4.0));
            vec3 teal = vec3(0.1, 0.7, 0.6);
            vec3 purple = vec3(0.4, 0.1, 0.6);
            vec3 base = mix(teal, purple, band * 0.6);
            float spec = pow(max(dot(reflect(dir, n), normalize(vec3(0.6, 0.7, 0.3))), 0.0), 30.0);
            return vec4(base * (0.15 + light) + vec3(spec), 1.0);
        }
        t += d * 0.8;
        if (t > 3.0) break;
    }
    return vec4(0.0);
}
