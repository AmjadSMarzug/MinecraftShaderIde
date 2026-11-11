// the item turns into a cut emerald: deep green facets with a bright sparkle that moves as you turn it
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    vec2 facet = abs(fract(uv * 4.0) - 0.5);
    float facetLine = smoothstep(0.42, 0.5, max(facet.x, facet.y));
    float lum = dot(texel.rgb, vec3(0.333));
    vec3 green = vec3(0.05, 0.6, 0.3) * (0.4 + 0.8 * lum);
    vec3 color = green * (1.0 - facetLine * 0.3);
    float sparkle = pow(max(dot(reflect(view, normal), normalize(vec3(0.5, 0.7, 0.5))), 0.0), 24.0);
    color += vec3(0.7, 1.0, 0.8) * sparkle * (0.6 + 0.4 * sin(time * 2.0));
    return vec4(color, texel.a);
}
