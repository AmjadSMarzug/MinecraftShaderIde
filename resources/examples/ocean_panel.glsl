// a painted ocean on a panel: rolling waves, lighter foam on the crests and a darker trough between them
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    float wave = sin(uv.x * 20.0 + time * 2.0) * 0.5 + sin(uv.y * 15.0 - time * 1.5 + uv.x * 5.0) * 0.5;
    float crest = smoothstep(0.5, 1.0, wave * 0.5 + 0.5);
    vec3 deep = vec3(0.02, 0.2, 0.35);
    vec3 foam = vec3(0.7, 0.9, 1.0);
    vec3 color = mix(deep, foam, crest * 0.6);
    return vec4(color, 1.0);
}
