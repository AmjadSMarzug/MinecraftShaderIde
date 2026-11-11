// a sun glare across the lens: a bright star-like flare that stays in one spot of the world as you turn, with rings trailing off it
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

vec4 overlay(vec2 screen, vec3 dir, float time) {
    vec3 sunDir = normalize(vec3(0.3, 0.35, -1.0));
    float glare = pow(max(dot(normalize(dir), sunDir), 0.0), 120.0);
    vec2 c = (screen - 0.5) * vec2(1.8, 1.0);
    float ring = smoothstep(0.02, 0.0, abs(length(c) - 0.3 - 0.05 * sin(time)));
    float halo = exp(-length(c) * 4.0) * 0.25;
    vec3 color = vec3(1.0, 0.85, 0.6) * (glare + halo) + vec3(0.5, 0.7, 1.0) * ring * 0.3;
    return vec4(color, clamp(glare + halo + ring * 0.2, 0.0, 0.8));
}
