// a night vision visor over the view: a green tint, moving grain, a faint scan line and a dark ring at the edge
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float nvHash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    vec2 c = screen - vec2(0.5);
    float ring = smoothstep(0.35, 0.75, length(c));
    float grain = nvHash(screen * 800.0 + vec2(time * 37.0, time * 11.0));
    float scan = 0.9 + 0.1 * sin(screen.y * 400.0 + time * 20.0);
    vec3 tint = vec3(0.25, 1.0, 0.35) * (0.6 + 0.4 * grain) * scan;
    float alpha = 0.22 + ring * 0.7;
    return vec4(tint, alpha);
}
