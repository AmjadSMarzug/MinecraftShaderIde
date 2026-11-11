// red embers rising past the view: three layers of soft sparks drifting upward at different speeds
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float edHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    float alpha = 0.0;
    vec3 col = vec3(0.0);
    for (int layer = 0; layer < 3; layer++) {
        float scale = 6.0 + float(layer) * 4.0;
        float speed = 0.05 + float(layer) * 0.03;
        vec2 p = vec2(screen.x * scale, (screen.y + time * speed) * scale);
        vec2 cell = floor(p);
        vec2 local = fract(p) - 0.5;
        float seed = edHash(cell + float(layer) * 17.0);
        vec2 offset = vec2(seed - 0.5, edHash(cell.yx) - 0.5) * 0.6;
        float spark = smoothstep(0.12, 0.0, length(local - offset)) * step(0.6, seed);
        alpha += spark * 0.7;
        col += vec3(1.0, 0.35 + 0.3 * seed, 0.1) * spark;
    }
    return vec4(col, clamp(alpha, 0.0, 0.85));
}
