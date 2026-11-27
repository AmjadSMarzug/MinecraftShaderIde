// snow falling past the view: soft white flakes in three layers, each drifting down at its own speed
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float snHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    float alpha = 0.0;
    for (int layer = 0; layer < 3; layer++) {
        float scale = 8.0 + float(layer) * 6.0;
        float speed = 0.1 + float(layer) * 0.07;
        vec2 p = vec2(screen.x * scale, (screen.y + time * speed) * scale);
        vec2 cell = floor(p);
        vec2 local = fract(p) - 0.5;
        float seed = snHash(cell + float(layer) * 11.0);
        vec2 offset = vec2(seed - 0.5, snHash(cell.yx) - 0.5) * 0.5;
        float flake = smoothstep(0.14, 0.0, length(local - offset)) * step(0.55, seed);
        alpha += flake * 0.6;
    }
    return vec4(vec3(0.95, 0.97, 1.0), clamp(alpha, 0.0, 0.8));
}
