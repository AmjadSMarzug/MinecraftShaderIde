// recovered from the Bliss SMP Pack 6 (26.3 item shader). the build had scrambled the names, so
// helper and value names here are generated. the numbers and the logic are restored
const vec3 helper4=vec3(1.0,0.855,0.455);
uint helper3(uint Q22Q0z) {
    Q22Q0z ^=Q22Q0z>>16u;
    Q22Q0z*=2146121005u;
    Q22Q0z ^=Q22Q0z>>15u;
    Q22Q0z*=2221713035u;
    Q22Q0z ^=Q22Q0z>>16u;
    return Q22Q0z;
}
float helper2(vec3 value19) {
    uvec3 value1=uvec3(ivec3(value19));
    return float(helper3(value1 . x ^ helper3(value1 . y ^ helper3(value1 . z))))/4.294967e+09;
}
float helper1(vec3 value8) {
    vec3 value19=floor(value8);
    vec3 value10=fract(value8);
    value10=value10*value10*(3.0-2.0*value10);
    float value17=mix(mix(mix(helper2(value19),helper2(value19+vec3(1.0,0.0,0.0)),value10 . x),mix(helper2(value19+vec3(0.0,1.0,0.0)),helper2(value19+vec3(1.0,1.0,0.0)),value10 . x),value10 . y),mix(mix(helper2(value19+vec3(0.0,0.0,1.0)),helper2(value19+vec3(1.0,0.0,1.0)),value10 . x),mix(helper2(value19+vec3(0.0,1.0,1.0)),helper2(value19+vec3(1.0,1.0,1.0)),value10 . x),value10 . y),value10 . z);
    return value17*value17;
}
vec3 helper5(vec3 value1) {
    vec3 value8=abs(fract(value1 . xxx+vec3(1.0,2.0/3.0,1.0/3.0))*6.0-3.0);
    return value1 . z*mix(vec3(1.0),clamp(value8-1.0,0.0,1.0),value1 . y);
}
vec4 object(vec3 origin,vec3 dir,float time) {
    float value2=time/1200.0;
    float value4=0.27-sin(value2*2000.0)*0.006;
    vec3 value11=origin*0.5;
    float value15=0.0;
    float value6=length(value11+dir*max(-dot(value11,dir),0.0))-value4;
    vec3 value8=value11;
    bool value3=false;
    for(int value13=0;value13<80;value13++) {
        value8=value11+dir*value15;
        float value7=length(value8)-value4;
        value15+=max(0.0,value7);
        if(value15>100.0)break;
        if(value7<0.006) {
            value3=true;
            break;
        }
    }
    vec4 value16;
    if(value3) {
        vec3 value17=normalize(value8);
        float value14=helper1(value17*3.0+1000.0+value2*6000.0+helper1(value17*6.0+1000.0-value2*3500.0));
        value16=vec4(helper5(vec3(0.14-value14*0.2,0.5,0.6+value14*2.0)),1.0);
    }
    else {
        float value5=pow(clamp(1.0-value6/0.8,0.0,1.0),20.0)*20.0;
        value5*=1.0-smoothstep(0.4,0.49,value6+value4);
        value16=1.0-exp(-vec4(helper4*value5,value5)*0.5);
    }
    return value16;
}
