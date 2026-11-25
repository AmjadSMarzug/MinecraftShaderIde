// Plasma Orb: a pulsing plasma sphere with a gold glow.
// recovered from the Bliss SMP Pack 6 (26.3). the build had scrambled the names; the names here were chosen from what the code does.
const vec3 glowColour=vec3(1.0,0.855,0.455);
uint hashUint(uint h) {
    h ^=h>>16u;
    h*=2146121005u;
    h ^=h>>15u;
    h*=2221713035u;
    h ^=h>>16u;
    return h;
}
float hashCell(vec3 p) {
    uvec3 cellBits=uvec3(ivec3(p));
    return float(hashUint(cellBits . x ^ hashUint(cellBits . y ^ hashUint(cellBits . z))))/4.294967e+09;
}
float noiseSquared(vec3 p) {
    vec3 cell=floor(p);
    vec3 fade=fract(p);
    fade=fade*fade*(3.0-2.0*fade);
    float n=mix(mix(mix(hashCell(cell),hashCell(cell+vec3(1.0,0.0,0.0)),fade . x),mix(hashCell(cell+vec3(0.0,1.0,0.0)),hashCell(cell+vec3(1.0,1.0,0.0)),fade . x),fade . y),mix(mix(hashCell(cell+vec3(0.0,0.0,1.0)),hashCell(cell+vec3(1.0,0.0,1.0)),fade . x),mix(hashCell(cell+vec3(0.0,1.0,1.0)),hashCell(cell+vec3(1.0,1.0,1.0)),fade . x),fade . y),fade . z);
    return n*n;
}
vec3 hsvToRgb(vec3 hsv) {
    vec3 rgb=abs(fract(hsv . xxx+vec3(1.0,2.0/3.0,1.0/3.0))*6.0-3.0);
    return hsv . z*mix(vec3(1.0),clamp(rgb-1.0,0.0,1.0),hsv . y);
}
vec4 object(vec3 origin,vec3 dir,float time) {
    float scaledTime=time/1200.0;
    float radius=0.27-sin(scaledTime*2000.0)*0.006;
    vec3 scaledOrigin=origin*0.5;
    float travel=0.0;
    float closestDist=length(scaledOrigin+dir*max(-dot(scaledOrigin,dir),0.0))-radius;
    vec3 hitPoint=scaledOrigin;
    bool hit=false;
    for(int step=0;step<80;step++) {
        hitPoint=scaledOrigin+dir*travel;
        float dist=length(hitPoint)-radius;
        travel+=max(0.0,dist);
        if(travel>100.0)break;
        if(dist<0.006) {
            hit=true;
            break;
        }
    }
    vec4 colour;
    if(hit) {
        vec3 normal=normalize(hitPoint);
        float noise=noiseSquared(normal*3.0+1000.0+scaledTime*6000.0+noiseSquared(normal*6.0+1000.0-scaledTime*3500.0));
        colour=vec4(hsvToRgb(vec3(0.14-noise*0.2,0.5,0.6+noise*2.0)),1.0);
    }
    else {
        float glow=pow(clamp(1.0-closestDist/0.8,0.0,1.0),20.0)*20.0;
        glow*=1.0-smoothstep(0.4,0.49,closestDist+radius);
        colour=1.0-exp(-vec4(glowColour*glow,glow)*0.5);
    }
    return colour;
}
