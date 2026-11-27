// flames licking in around the edges of the view, flickering and rising toward the top of the screen
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

float fbHash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float fbNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = mix(fbHash(i), fbHash(i + vec2(1.0, 0.0)), f.x);
    float b = mix(fbHash(i + vec2(0.0, 1.0)), fbHash(i + vec2(1.0, 1.0)), f.x);
    return mix(a, b, f.y);
}

vec4 overlay(vec2 screen, vec3 dir, float time) {
    vec2 c = screen - 0.5;
    float edge = smoothstep(0.35, 0.7, length(c * vec2(1.4, 1.0)));
    float flame = fbNoise(vec2(screen.x * 14.0, screen.y * 6.0 - time * 3.0));
    float tongue = smoothstep(0.4, 0.8, flame + screen.y * 0.3);
    vec3 fire = mix(vec3(1.0, 0.25, 0.05), vec3(1.0, 0.8, 0.2), tongue);
    return vec4(fire, edge * tongue * 0.8);
}
