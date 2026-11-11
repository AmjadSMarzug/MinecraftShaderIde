// yellow and black hazard stripes, slowly sliding along the diagonal
#pragma vessel shape panel

// uv goes 0 to 1 across each face, pos is the same point in the -1 to 1 box, normal points out, view is where you look

vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time) {
    float s = fract((uv.x + uv.y) * 6.0 - time * 0.3);
    float band = step(0.5, s);
    vec3 color = mix(vec3(0.05), vec3(1.0, 0.8, 0.0), band);
    return vec4(color, 1.0);
}
