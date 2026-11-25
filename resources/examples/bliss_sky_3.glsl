// recovered from the Bliss SMP Pack 6 (26.3 item shader). the build had scrambled the names, so
// helper and value names here are generated. the numbers and the logic are restored
#define helper4 20.0
const vec3 helper6=vec3(0.28,0.26,0.22);
const vec3 helper1=vec3(1.0,0.93,0.8);
const vec3 helper2=vec3(0.97,0.9,0.73);
float helper5(vec3 value8) {
    vec3 value6=floor(value8);
    vec3 value3=fract(value8);
    value3=value3*value3*(3.0-2.0*value3);
    float value10=mix(mix(mix(float((fract(sin(dot(vec3(value6),vec3(12.9898,78.233,37.719)))*43758.55))),float((fract(sin(dot(vec3(value6+vec3(1,0,0)),vec3(12.9898,78.233,37.719)))*43758.55))),value3 . x),mix(float((fract(sin(dot(vec3(value6+vec3(0,1,0)),vec3(12.9898,78.233,37.719)))*43758.54))),float((fract(sin(dot(vec3(value6+vec3(1,1,0)),vec3(12.9898,78.233,37.719)))*43758.55))),value3 . x),value3 . y),mix(mix(float((fract(sin(dot(vec3(value6+vec3(0,0,1)),vec3(12.9898,78.233,37.719)))*43758.55))),float((fract(sin(dot(vec3(value6+vec3(1,0,1)),vec3(12.9898,78.233,37.719)))*43758.55))),value3 . x),mix(float((fract(sin(dot(vec3(value6+vec3(0,1,1)),vec3(12.9898,78.233,37.719)))*43758.55))),float((fract(sin(dot(vec3(value6+vec3(1,1,1)),vec3(12.9898,78.23301,37.719)))*43758.55))),value3 . x),value3 . y),value3 . z);
    return value10*value10;
}
float helper3(vec3 dir,float time) {
    vec3 value8=dir*helper4;
    float value5=helper5(-value8*0.25+vec3(0.0,0.0,time*2.5))*10.0;
    float value4=helper5(value8+vec3(0.0,time*2.0,0.0)-value5);
    return helper5(value8-vec3(0.0,time*2.0,0.0)+value4);
}
vec3 sky(vec3 dir,float time) {
    vec3 value9=mix(helper6,helper1,helper3(dir,time));
    vec3 value2=-helper2-sin(time*0.29);
    return clamp(mix(value2,value9,dir . y),0.0,1.0);
}
