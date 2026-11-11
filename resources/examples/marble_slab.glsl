// a polished marble slab: pale stone with grey and gold veins that wind through it
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

float maHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float maNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = mix(maHash(i), maHash(i + vec2(1.0, 0.0)), f.x);
    float b = mix(maHash(i + vec2(0.0, 1.0)), maHash(i + vec2(1.0, 1.0)), f.x);
    return mix(a, b, f.y);
}

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    float warp = maNoise(uv * 4.0) * 3.0 + maNoise(uv * 9.0);
    float vein = smoothstep(0.06, 0.0, abs(sin(uv.x * 6.0 + warp) * 0.5));
    vec3 stone = vec3(0.92, 0.9, 0.88) - 0.05 * maNoise(uv * 30.0);
    vec3 gold = vec3(0.9, 0.7, 0.3);
    vec3 color = mix(stone, gold, vein * 0.8);
    return vec4(color, 1.0);
}
