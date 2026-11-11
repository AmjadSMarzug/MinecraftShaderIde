// the item is coated in frost: pale blue ice with a web of cracks running through it, catching a glint as it turns
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

float fiHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float fiCrack(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    float d1 = 8.0;
    float d2 = 8.0;
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 g = vec2(float(x), float(y));
            vec2 o = vec2(fiHash(i + g), fiHash(i + g + 19.0));
            float d = length(g + o - f);
            if (d < d1) {
                d2 = d1;
                d1 = d;
            } else if (d < d2) {
                d2 = d;
            }
        }
    }
    return d2 - d1;
}

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    float lum = dot(texel.rgb, vec3(0.333));
    float cracks = smoothstep(0.08, 0.0, fiCrack(uv * 5.0));
    vec3 ice = vec3(0.7, 0.88, 1.0) * (0.4 + 0.6 * lum);
    vec3 color = mix(texel.rgb * 0.5 + vec3(0.25, 0.35, 0.45), ice, 0.6);
    float glint = pow(max(dot(normalize(normal), normalize(vec3(0.4, 0.6, 0.7))), 0.0), 12.0);
    color += vec3(0.8, 0.95, 1.0) * cracks * 0.6 + vec3(0.6, 0.8, 1.0) * glint * 0.3;
    return vec4(color, texel.a);
}
