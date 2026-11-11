// a riveted steel plate: rust creeping through the paint in patches, and round rivets at the corner of every panel
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

float rpHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float rpNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = mix(rpHash(i), rpHash(i + vec2(1.0, 0.0)), f.x);
    float b = mix(rpHash(i + vec2(0.0, 1.0)), rpHash(i + vec2(1.0, 1.0)), f.x);
    return mix(a, b, f.y);
}

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    float n = rpNoise(uv * 9.0) * 0.6 + rpNoise(uv * 23.0) * 0.4;
    vec3 steel = vec3(0.35, 0.37, 0.4);
    vec3 rust = vec3(0.45, 0.22, 0.08);
    vec3 color = mix(steel, rust, smoothstep(0.55, 0.7, n));
    vec2 f = fract(uv * vec2(3.0, 2.0)) - 0.5;
    float rivet = smoothstep(0.035, 0.028, length(abs(f) - vec2(0.42)));
    float light = pow(max(dot(normalize(normal), normalize(vec3(0.3, 0.6, 0.7))), 0.0), 8.0);
    color = color * (0.6 + 0.4 * light) + rivet * vec3(0.7, 0.72, 0.75);
    return vec4(color, 1.0);
}
