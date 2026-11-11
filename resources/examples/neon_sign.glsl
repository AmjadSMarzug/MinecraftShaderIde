// a neon sign: a glowing rounded frame in pink and blue, buzzing very slightly like a tube that isn't quite stable
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    vec2 p = uv - 0.5;
    vec2 box = vec2(0.36, 0.2);
    vec2 q = abs(p) - box;
    float d = length(max(q, 0.0)) + min(max(q.x, q.y), 0.0);
    float tube = exp(-abs(d) * 60.0);
    float buzz = 0.9 + 0.1 * sin(time * 40.0);
    vec3 pink = vec3(1.0, 0.2, 0.6);
    vec3 blue = vec3(0.2, 0.6, 1.0);
    vec3 neon = mix(pink, blue, uv.x);
    vec3 color = neon * tube * buzz + neon * exp(-abs(d) * 8.0) * 0.15;
    return vec4(color, 1.0);
}
