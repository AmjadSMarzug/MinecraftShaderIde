// an old film look over the view: warm sepia tint, moving grain and darker corners
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float sfHash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    float grain = sfHash(screen * 900.0 + vec2(time * 53.0, time * 17.0));
    float corner = smoothstep(0.35, 0.9, length((screen - 0.5) * vec2(1.6, 1.0)));
    vec3 sepia = vec3(0.6, 0.45, 0.25) * (0.85 + 0.15 * grain);
    return vec4(sepia, 0.2 + corner * 0.5);
}
