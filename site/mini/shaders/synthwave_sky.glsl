// a synthwave horizon: a striped neon sun sitting on the horizon, and a glowing grid running out to it across the ground

vec3 sky(vec3 dir, float time) {
    // Sky gradient: magenta at the horizon, fading to deep purple above.
    vec3 top = vec3(0.05, 0.0, 0.15);
    vec3 horizon = vec3(0.6, 0.1, 0.45);
    vec3 color = mix(horizon, top, smoothstep(0.0, 0.5, dir.y));

    // Above the horizon: a sun with horizontal stripes cut through it, fading near the horizon.
    if (dir.y >= 0.0) {
        float sunAmount = dot(normalize(dir), normalize(vec3(0.0, 0.08, -1.0)));
        float disc = smoothstep(0.985, 0.99, sunAmount);
        float stripe = step(0.5, fract(dir.y * 30.0));
        float fade = smoothstep(0.0, 0.12, dir.y);
        vec3 sunColour = mix(vec3(1.0, 0.8, 0.2), vec3(1.0, 0.2, 0.5), dir.y * 3.0);
        color += sunColour * disc * mix(0.3, 1.0, stripe * fade);
    } else {
        // Below the horizon: a perspective grid of glowing lines. Dividing by -dir.y makes the lines recede.
        vec2 g = dir.xz / -dir.y;
        g.y += time * 0.5;
        vec2 f = abs(fract(g) - 0.5);
        float line = smoothstep(0.47, 0.5, max(f.x, f.y));
        color = vec3(0.02, 0.0, 0.08) + vec3(0.0, 0.9, 1.0) * line * exp(dir.y * 4.0);
    }
    return color;
}
