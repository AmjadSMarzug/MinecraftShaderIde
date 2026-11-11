// looking up through water: a blue tint with moving light caustics that ripple across the view
// screen goes from 0,0 bottom left to 1,1 top right, dir is where you look

vec4 overlay(vec2 screen, vec3 dir, float time) {
    vec2 p = screen * 9.0;
    float c1 = sin(p.x + sin(p.y * 1.3 + time) * 1.5 + time);
    float c2 = sin(p.y * 1.7 - time * 1.3 + sin(p.x * 0.9));
    float caustic = pow(0.5 + 0.5 * (c1 * c2), 4.0);
    vec3 tint = vec3(0.1, 0.4, 0.6) + vec3(0.6, 0.9, 1.0) * caustic * 0.4;
    float alpha = 0.25 + caustic * 0.2;
    return vec4(tint, alpha);
}
