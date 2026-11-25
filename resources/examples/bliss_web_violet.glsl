// Web Violet: a violet nebula with plumes, orbs and a cell web.
// Unobfuscated from the Bliss SMP Pack 6 (26.3 item shader).
// Names were chosen from what the code does. Comments added by hand.

float cellEdgeStrength=1.0;
// Constants: the cell scale for the web, and the violet and lilac palette.
#define cellEdgeScale 7.5
const vec3 deepViolet=vec3(0.15,0.05,0.25);
const vec3 violet=vec3(0.30,0.08,0.50);
const vec3 brightViolet=vec3(0.47,0.10,1.00);
const vec3 softViolet=vec3(0.65,0.35,1.00);
const vec3 plumeViolet=vec3(0.6,0.2,1.0);
const vec3 lilac=vec3(0.9,0.7,1.0);
const vec3 magenta=vec3(0.8,0.4,1.0);
const vec3 paleLilac=vec3(0.95,0.86,1.0);
// Integer hash (a Wang-style bit mixer). Randomises cells without visible patterns.
uint hashMix(uint h) {
    h ^=h>>16u;
    h*=2146121005u;
    h ^=h>>15u;
    h*=2221713035u;
    h ^=h>>16u;
    return h;
}
// Hashes an integer cell to a value between 0 and 1.
float hashCell(vec3 p) {
    uvec3 cellBits=uvec3(ivec3(p));
    return float(hashMix(cellBits . x ^ hashMix(cellBits . y ^ hashMix(cellBits . z))))/4.294968e+09;
}
// Smooth 3D noise from the cell hashes. Squared so the contrast is stronger.
float smoothNoise(vec3 p) {
    vec3 cell=floor(p);
    vec3 fade=fract(p);
    fade=fade*fade*(3.0-2.0*fade);
    float n=mix(mix(mix(hashCell(cell),hashCell(cell+vec3(1.0,0.0,0.0)),fade . x),mix(hashCell(cell+vec3(0.0,1.0,0.0)),hashCell(cell+vec3(1.0,1.0,0.0)),fade . x),fade . y),mix(mix(hashCell(cell+vec3(0.0,0.0,1.0)),hashCell(cell+vec3(1.0,0.0,1.0)),fade . x),mix(hashCell(cell+vec3(0.0,1.0,1.0)),hashCell(cell+vec3(1.0,1.0,1.0)),fade . x),fade . y),fade . z);
    return n*n;
}
// Random vec3 from a vec3 position.
vec3 hash33(vec3 p) {
    return fract(sin(vec3(dot(p,vec3(127.1,311.7,74.7)),dot(p,vec3(269.5,183.3,246.1)),dot(p,vec3(113.5,271.9,124.6))))*43758.55);
}
// Sixty orbs moving across the sky. Each has a glow and a ring, and pulses at its own phase.
vec3 orbField(vec3 dir,float t) {
    vec3 glow=vec3(0.0);
    float phase=mod(t*15000.0,1000.0)*0.005;
    for(int i=0;i<60;i++) {
        float fi=float(i);
        float pulse=sin(3.14159*fract(t*300.0+fi*0.731));
        float angle=fi*3.883222;
        float y=fi/30.0-1.0;
        float r=sqrt(1.0-y*y);
        vec3 orbDir=vec3(cos(angle)*r,y,sin(angle)*r);
        orbDir . x+=sin(orbDir . y*2.0+phase*10.0)*0.1;
        orbDir . y+=cos(orbDir . z*2.0+phase*10.0)*0.1;
        orbDir . z+=sin(orbDir . x*2.0+phase*10.0)*0.1;
        orbDir=normalize(orbDir);
        vec3 tangent=normalize(cross(orbDir,vec3(0.0,1.0,0.001)));
        vec3 bitangent=cross(orbDir,tangent);
        vec2 uv=vec2(dot(dir,tangent),dot(dir,bitangent))*40.0;
        float sharp=0.4+fract(sin(fi*1.45)*45.1)*0.3;
        float dist=pow(pow(abs(uv . x),sharp)+pow(abs(uv . y),sharp),1.0/sharp);
        float radius=pulse*2.0;
        float ring=smoothstep(radius,radius-0.4,dist)*smoothstep(0.0,radius*0.5,dist);
        float halo=exp(-dist*8.0);
        glow+=mix(plumeViolet,lilac,pulse)*(ring+halo*1.5)*pulse;
    }
    return glow;
}
// The violet nebula: noise layers for plumes and filaments, with orbs and sparkles on top.
vec3 nebulaColour(vec3 dir,float t) {
    vec3 drift=t*vec3(400.0,-800.0,200.0);
    float flicker=sin(t*3000.0)*0.5+0.5;
    float plume=smoothNoise(dir*7.5+smoothNoise(dir*4.5+t*150.0)*(2.0+flicker));
    float wisp=smoothNoise(dir*18.0+drift*0.4);
    float dust=smoothNoise(dir*375.0+t*400.0);
    float sparkle=pow(max(dust-0.75,0.0)*4.0,3.5);
    float phase=t*140.0;
    vec3 wind=vec3(sin(phase*0.002),cos(phase*0.001),sin(phase*0.0015))*2.0;
    float lilacMix=pow(max(smoothNoise(dir*3.0+wind+vec3(0.0,phase*0.001,0.0))-0.3,0.0)*1.4,2.0);
    float bright=pow(max(smoothNoise(dir*5.0-wind+vec3(0.0,phase*0.0015,0.0))-0.35,0.0)*1.5,2.5);
    float mixed=pow(plume*0.4+wisp*0.6,1.1);
    vec3 base=mix(mix(deepViolet,violet,plume),brightViolet,wisp*mixed);
    vec3 glowMix=mix(plumeViolet,lilac,lilacMix)*lilacMix+magenta*bright*0.8;
    vec3 result=mix(base+glowMix*1.5+orbField(dir,t),mix(softViolet,lilac,0.5),sparkle);
    float filament=pow(max(smoothNoise(vec3(dir . x*20.0,dir . y*2.0,dir . z*20.0)+drift*0.1)-0.6,0.0)*2.5,3.0);
    return result+magenta*filament*0.2;
}
// Voronoi cell distances: the nearest and second-nearest cell centres.
vec2 voronoi2(vec3 p) {
    vec3 cell=floor(p);
    vec3 local=fract(p);
    float d1=9.0,d2=9.0;
    for(int z=-1;z<=1;z++)for(int y=-1;y<=1;y++)for(int x=-1;x<=1;x++) {
        vec3 offset=vec3(x,y,z);
        vec3 diff=offset+hash33(cell+offset)-local;
        float dist2=dot(diff,diff);
        if(dist2<d1) {
            d2=d1;
            d1=dist2;
        }
        else if(dist2<d2) {
            d2=dist2;
        }
    }
    return vec2(sqrt(d1),sqrt(d2));
}
// The web pass. cellEdgeStrength is 1.0, which returns the colour unchanged, so the web is off.
// Lower it below 1.0 to draw the web lines over the nebula.
vec3 cellEdgePass(vec3 dir,vec3 colour,float time) {
    if(cellEdgeStrength>=1.0)return colour;
    float edgeWidth=mix(0.09,0.008,smoothstep(0.55,0.85,cellEdgeStrength));
    float strength=1.0-smoothstep(0.85,0.97,cellEdgeStrength);
    vec3 p=dir*cellEdgeScale;
    p . x+=sin(p . y+time*0.9)*0.14;
    p . y+=cos(p . z+time*0.9)*0.14;
    p . z+=sin(p . x+time*0.9)*0.14;
    p+=time*0.0025;
    vec2 cells=voronoi2(p);
    float edgeDist=cells . y-cells . x;
    float mask=(1.0-smoothstep(edgeWidth,edgeWidth+0.008,edgeDist))*strength;
    float mixAmount=smoothstep(0.55,0.85,cellEdgeStrength);
    return mix(colour,mix(paleLilac,vec3(1.0),mixAmount),mask);
}
// Entry point: builds the nebula colour, then applies the optional web pass.
vec3 sky(vec3 dir,float time) {
    vec3 nebula=nebulaColour(dir,time/1200.0);
    return clamp(cellEdgePass(dir,nebula,time),0.0,1.0);
}
