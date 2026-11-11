// an old TV that lost its signal: grey static that changes every frame, with a dark horizontal roll bar
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

float tvHash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    float frame = floor(time * 30.0);
    float noise = tvHash(uv * vec2(400.0, 300.0) + frame);
    float roll = smoothstep(0.0, 0.05, abs(fract(uv.y - time * 0.2) - 0.5) - 0.44);
    float rows = 0.9 + 0.1 * sin(uv.y * 600.0);
    vec3 color = vec3(noise) * roll * rows;
    return vec4(color, 1.0);
}
