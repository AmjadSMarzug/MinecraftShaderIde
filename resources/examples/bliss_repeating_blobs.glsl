// Repeating Blobs: raymarched blobs repeating through the sky, lit from above.
// recovered from the Bliss SMP Pack 6 (26.3). the build had scrambled the names; the names here were chosen from what the code does.
#define cellSize 11.0
#define maxSteps 90
#define maxDistance 260.0
float valueNoise3(vec3 p) {
    vec3 cell=floor(p);
    vec3 fade=fract(p);
    fade=fade*fade*(3.0-2.0*fade);
    return mix(mix(mix(float((fract(sin(dot(vec3(cell+vec3(0,0,0)),vec3(127.1,311.7,74.7)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,0,0)),vec3(127.1,311.7,74.7)))*43758.55))),fade . x),mix(float((fract(sin(dot(vec3(cell+vec3(0,1,0)),vec3(127.1,311.7,74.7)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,1,0)),vec3(127.1,311.7,74.7)))*43758.54))),fade . x),fade . y),mix(mix(float((fract(sin(dot(vec3(cell+vec3(0,0,1)),vec3(127.1,311.7,74.7)))*43758.54))),float((fract(sin(dot(vec3(cell+vec3(1,0,1)),vec3(127.1,311.7,74.7)))*43758.55))),fade . x),mix(float((fract(sin(dot(vec3(cell+vec3(0,1,1)),vec3(127.1,311.7,74.7)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,1,1)),vec3(127.1,311.7,74.7)))*43758.55))),fade . x),fade . y),fade . z);
}
mat2 rot2d(float angle) {
    float c=cos(angle),s=sin(angle);
    return mat2(c,s,-s,c);
}
float smoothMin(float a,float b,float k) {
    float h=clamp(0.5+0.5*(b-a)/k,0.0,1.0);
    return mix(b,a,h)-k*h*(1.0-h);
}
float sdCylinder(vec2 p2,float height,float radius,float halfHeight) {
    vec2 d=vec2(length(p2)-radius,abs(height)-halfHeight);
    return min(max(d . x,d . y),0.0)+length(max(d,0.0));
}
float sceneDistance(vec3 p,float time) {
    vec3 q=mod(p,cellSize)-cellSize*0.5;
    float ringA=sdCylinder(q . yz,q . x,0.55,cellSize*0.5)+float((0.27*sin(float(p . x)*18.0+float(time)*3.0)));
    float ringB=sdCylinder(q . xz,q . y,0.55,cellSize*0.5)+float((0.27*sin(float(p . y)*18.0+float(time)*3.0)));
    float ringC=sdCylinder(q . xy,q . z,0.55,cellSize*0.5)+float((0.27*sin(float(p . z)*18.0+float(time)*3.0)));
    float blend=smoothMin(smoothMin(ringB,ringC,2.6),ringA,2.6);
    return smoothMin(float((length(vec3(q))-float(2.3))),blend,3.6);
}
vec3 sceneNormal(vec3 p,float time) {
    vec2 eps=vec2(0.01,0.0);
    return normalize(vec3(sceneDistance(p+eps . xyy,time)-sceneDistance(p-eps . xyy,time),sceneDistance(p+eps . yxy,time)-sceneDistance(p-eps . yxy,time),sceneDistance(p+eps . yyx,time)-sceneDistance(p-eps . yyx,time)));
}
float raymarch(vec3 origin,float time) {
    float t=0.0;
    for(int step=0;step<maxSteps;step++) {
        float d=sceneDistance(origin*t,time);
        if(d<0.01)return t;
        t+=d;
        if(t>maxDistance)break;
    }
    return -1.0;
}
float shadeBlob(vec3 p,vec3 normal,float time) {
    vec3 drift=vec3(0.0,time*0.05,0.0);
    float light=clamp(0.5+0.5*dot(normal,normalize(vec3(0.0,35.0,0.0)-p)),0.05,1.0);
    float lit=light*(0.55+0.45*valueNoise3(p*vec3(0.8,0.3,0.8)+drift));
    float pattern=valueNoise3(p*3.2+valueNoise3(p*1.4+drift)*3.0);
    float ridge=clamp(1.0-abs(pattern-0.5)/0.05,0.0,1.0);
    float edge=clamp((0.38-pattern)/0.12,0.0,1.0);
    return lit*(1.0-0.45*ridge)*(1.0-0.22*edge);
}
vec3 sky(vec3 dir,float time) {
    vec3 rayDir=dir;
    rayDir . xz*=rot2d(time*0.036);
    rayDir . xy*=rot2d(time*0.019);
    rayDir . yz*=rot2d(time*0.031);
    float baseLight=clamp(0.42+0.9*dir . y,0.0,1.2);
    float colour=baseLight;
    float hitDistance=raymarch(rayDir,time);
    if(hitDistance>0.0) {
        vec3 hitPos=rayDir*hitDistance;
        colour=mix(baseLight,shadeBlob(hitPos,sceneNormal(hitPos,time),time),exp(-hitDistance*0.02));
    }
    colour*=clamp(0.55+0.6*dir . y,0.08,1.15);
    colour*=0.05+0.95*smoothstep(-0.6,0.08,dir . y);
    return vec3(clamp(colour,0.0,1.0));
}
