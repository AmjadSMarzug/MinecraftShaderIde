// rain falling down the view: thin bright streaks in three layers, each falling at its own speed
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float rsHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    float alpha = 0.0;
    for (int layer = 0; layer < 3; layer++) {
        float columns = 40.0 + float(layer) * 25.0;
        float speed = 0.8 + float(layer) * 0.5;
        vec2 p = vec2(screen.x * columns, screen.y * 2.0 + time * speed);
        vec2 cell = floor(p);
        vec2 local = fract(p);
        float drop = rsHash(vec2(cell.x, float(layer)));
        float streak = smoothstep(0.06, 0.0, abs(local.x - 0.5)) * step(0.7, drop);
        streak *= smoothstep(0.0, 0.3, local.y) * smoothstep(1.0, 0.6, local.y);
        alpha += streak * 0.35;
    }
    return vec4(vec3(0.75, 0.85, 1.0), clamp(alpha, 0.0, 0.5));
}
