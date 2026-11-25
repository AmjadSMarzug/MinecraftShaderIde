// Cloud Drift: warped clouds drifting over a dusk gradient.
// recovered from the Bliss SMP Pack 6 (26.3). the build had scrambled the names; the names here were chosen from what the code does.
#define cloudScale 20.0
const vec3 shadowTone=vec3(0.28,0.26,0.22);
const vec3 highlightTone=vec3(1.0,0.93,0.8);
const vec3 horizonTone=vec3(0.97,0.9,0.73);
float valueNoise3(vec3 p) {
    vec3 cell=floor(p);
    vec3 fade=fract(p);
    fade=fade*fade*(3.0-2.0*fade);
    float n=mix(mix(mix(float((fract(sin(dot(vec3(cell),vec3(12.9898,78.233,37.719)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,0,0)),vec3(12.9898,78.233,37.719)))*43758.55))),fade . x),mix(float((fract(sin(dot(vec3(cell+vec3(0,1,0)),vec3(12.9898,78.233,37.719)))*43758.54))),float((fract(sin(dot(vec3(cell+vec3(1,1,0)),vec3(12.9898,78.233,37.719)))*43758.55))),fade . x),fade . y),mix(mix(float((fract(sin(dot(vec3(cell+vec3(0,0,1)),vec3(12.9898,78.233,37.719)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,0,1)),vec3(12.9898,78.233,37.719)))*43758.55))),fade . x),mix(float((fract(sin(dot(vec3(cell+vec3(0,1,1)),vec3(12.9898,78.233,37.719)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,1,1)),vec3(12.9898,78.23301,37.719)))*43758.55))),fade . x),fade . y),fade . z);
    return n*n;
}
float cloudDensity(vec3 dir,float time) {
    vec3 scaledDir=dir*cloudScale;
    float warpA=valueNoise3(-scaledDir*0.25+vec3(0.0,0.0,time*2.5))*10.0;
    float warpB=valueNoise3(scaledDir+vec3(0.0,time*2.0,0.0)-warpA);
    return valueNoise3(scaledDir-vec3(0.0,time*2.0,0.0)+warpB);
}
vec3 sky(vec3 dir,float time) {
    vec3 cloudColour=mix(shadowTone,highlightTone,cloudDensity(dir,time));
    vec3 baseColour=-horizonTone-sin(time*0.29);
    return clamp(mix(baseColour,cloudColour,dir . y),0.0,1.0);
}
