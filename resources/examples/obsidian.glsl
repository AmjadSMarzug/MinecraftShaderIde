// the item turns into polished black glass: a dark body, a rim that catches the light and a sheen sliding across it
// texel is the item's own pixel colour, uv goes 0 to 1 across the sprite, normal faces out, view is where you look

vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time) {
    float lum = dot(texel.rgb, vec3(0.333));
    vec3 body = vec3(0.03, 0.03, 0.04) + texel.rgb * 0.12;
    float fres = pow(1.0 - abs(dot(normalize(normal), normalize(view))), 3.0);
    float sheen = smoothstep(0.08, 0.0, abs(fract(uv.x * 0.7 + uv.y * 0.4 - time * 0.25) - 0.5));
    vec3 rim = vec3(0.6, 0.7, 0.9) * fres * 0.5;
    vec3 glint = vec3(0.9, 0.95, 1.0) * sheen * (0.4 + 0.6 * lum);
    return vec4(body + rim + glint, texel.a);
}
