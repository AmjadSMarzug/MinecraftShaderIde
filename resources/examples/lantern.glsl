// a small lantern: a frame of thin walls with a warm flame flickering in the middle
// origin is where the ray enters the item's box (-1 to 1 on every axis), return alpha 0 for empty space

float ltBox(vec3 p, vec3 b) {
    vec3 q = abs(p) - b;
    return length(max(q, 0.0)) + min(max(q.x, max(q.y, q.z)), 0.0);
}

vec4 object(vec3 origin, vec3 dir, float time) {
    vec3 glow = vec3(0.0);
    float t = 0.0;
    for (int i = 0; i < 72; i++) {
        vec3 p = origin + dir * t;
        if (max(abs(p.x), max(abs(p.y), abs(p.z))) > 1.0) break;
        float frame = abs(ltBox(p, vec3(0.42, 0.5, 0.42))) - 0.03;
        if (frame < 0.01) return vec4(vec3(0.35, 0.3, 0.25), 1.0);
        vec3 f = p - vec3(0.0, -0.2 + sin(time * 9.0) * 0.02, 0.0);
        float flame = exp(-length(f * vec3(1.0, 0.5, 1.0)) * 6.0);
        glow += vec3(1.0, 0.6, 0.2) * flame * 0.04;
        t += 0.02;
        if (t > 3.0) break;
    }
    return vec4(glow, clamp(length(glow), 0.0, 1.0));
}
