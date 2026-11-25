// Web Nebula: a nebula with a web of glowing cells and drifting orbs.
// Unobfuscated from the Bliss SMP Pack 6 (26.3 item shader).
// Names were chosen from what the code does. Comments added by hand.

// Constants: the scale of the planar warp, and how many orbs are drawn.
#define planeScale 7.5
#define orbCount 48
// The nebula's seven colours, from dark ember up to cream. The whole sky reads from this one struct.
struct ColourPalette {
    vec3 ember;
    vec3 amber;
    vec3 gold;
    vec3 paleGold;
    vec3 brightGold;
    vec3 orange;
    vec3 cream;
}
;
// Returns the palette above.
ColourPalette nebulaPalette() {
    return ColourPalette(vec3(0.46,0.31,0.09),vec3(0.62,0.44,0.15),vec3(0.86,0.66,0.27),vec3(1.0,0.96,0.72),vec3(1.0,0.82,0.36),vec3(0.97,0.7,0.26),vec3(1.0,0.98,0.88));
}
// Random vec3 from a vec3 position. Places a random point inside every Voronoi cell.
vec3 hash33(vec3 p) {
    return fract(sin(vec3(dot(p,vec3(127.1,311.7,74.7)),dot(p,vec3(269.5,183.3,246.1)),dot(p,vec3(113.5,271.9,124.6))))*43758.55);
}
// 3D value noise: smooth random values from a hashed grid.
float valueNoise3(vec3 p) {
    vec3 cell=floor(p);
    vec3 fade=fract(p);
    fade=fade*fade*(3.0-2.0*fade);
    return mix(mix(mix(float((fract(sin(dot(vec3(cell+vec3(0,0,0)),vec3(12.9898,78.233,37.719)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,0,0)),vec3(12.9898,78.233,37.719)))*43758.55))),fade . x),mix(float((fract(sin(dot(vec3(cell+vec3(0,1,0)),vec3(12.9898,78.233,37.719)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,1,0)),vec3(12.9898,78.23301,37.719)))*43758.55))),fade . x),fade . y),mix(mix(float((fract(sin(dot(vec3(cell+vec3(0,0,1)),vec3(12.9898,78.23301,37.719)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,0,1)),vec3(12.9898,78.233,37.719)))*43758.55))),fade . x),mix(float((fract(sin(dot(vec3(cell+vec3(0,1,1)),vec3(12.9898,78.233,37.719)))*43758.55))),float((fract(sin(dot(vec3(cell+vec3(1,1,1)),vec3(12.9898,78.233,37.719)))*43758.54))),fade . x),fade . y),fade . z);
}
// Base cloud colour: two layers of noise mix the palette's dark and gold tones.
vec3 nebulaBase(vec3 dir,float time,ColourPalette palette) {
    float n1=valueNoise3(dir*2.4+valueNoise3(dir*4.8+time*0.12)*3.0+time*vec3(0.02,-0.05,0.01));
    float n2=valueNoise3(dir*6.0+valueNoise3(dir*3.5-time*0.2)*1.6+time*vec3(0.05,-0.1,0.03));
    vec3 colour=mix(mix(palette . ember,palette . amber,n1),palette . gold,clamp(n2*n1*1.1,0.0,1.0));
    return colour;
}
// Glowing orbs: orbCount orbs spread over a sphere. Each is a ring with a halo, and each pulses at its own time.
vec3 sparkOrbs(vec3 dir,float time,ColourPalette palette) {
    vec3 glow=vec3(0.0);
    float phase=mod(time*12.5,999.9999)*0.005;
    for(int i=0;i<orbCount;i++) {
        float fi=float(i);
        float y=fi/(float(orbCount)*0.5)-1.0+1.0/float(orbCount);
        float r=sqrt(1.0-y*y);
        float angle=fi*2.39996;
        vec3 orbDir=vec3(cos(angle)*r,y,sin(angle)*r);
        orbDir . x+=sin(orbDir . y*2.0+phase*10.0)*0.1;
        orbDir . y+=cos(orbDir . z*2.0+phase*10.0)*0.1;
        orbDir . z+=sin(orbDir . x*2.0+phase*10.0)*0.1;
        orbDir=normalize(orbDir);
        if(dot(dir,orbDir)<=0.0)continue;
        float pulse=sin(3.14159*fract(time*0.25+fi*0.731));
        vec3 tangent=normalize(cross(orbDir,vec3(0.0,1.0,0.001)));
        vec3 bitangent=cross(orbDir,tangent);
        vec2 uv=abs(vec2(dot(dir,tangent),dot(dir,bitangent))*40.0);
        float sharp=0.4+0.3*fract(sin(fi*1.45)*45.1);
        float dist=pow(pow(uv . x,sharp)+pow(uv . y,sharp),1.0/sharp);
        float radius=pulse*4.0;
        float ring=smoothstep(radius,radius-0.4,dist)*smoothstep(0.0,radius*0.5,dist);
        float halo=exp(-dist*8.0)*0.8;
        glow+=mix(palette . orange*0.75,palette . orange,pulse)*(ring+halo)*pulse;
    }
    return glow;
}
// Voronoi cells: returns the distance to the nearest and second-nearest cell centre.
// The gap between those two distances is what draws the web.
vec2 voronoi2(vec3 p) {
    vec3 cell=floor(p);
    vec3 local=fract(p);
    float d1=9.0,d2=9.0;
    for(int dz=-1;dz<=1;dz++)for(int dy=-1;dy<=1;dy++)for(int dx=-1;dx<=1;dx++) {
        vec3 offset=vec3(dx,dy,dz);
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
// The web: bright lines along the cell borders, where the nearest and second-nearest distances meet, with a slow shimmer.
vec3 cellGlow(vec3 dir,vec3 base,float time,ColourPalette palette) {
    vec3 p=dir*planeScale;
    p . x+=sin(p . y+time*0.9)*0.14;
    p . y+=cos(p . z+time*0.9)*0.14;
    p . z+=sin(p . x+time*0.9)*0.14;
    p+=time*0.0025;
    vec2 edges=voronoi2(p);
    float edgeWidth=edges . y-edges . x;
    float line=1.0-smoothstep(0.006,0.02,edgeWidth);
    float pulse=0.75+0.5*(0.5+0.5*sin(time*0.85));
    float glow=pow(1.0-smoothstep(0.0,0.28,edgeWidth),3.0)*1.7*pulse;
    vec3 tint=mix(palette . paleGold,palette . brightGold,valueNoise3(p*0.5+time*0.08));
    vec3 rim=mix(tint,palette . brightGold,0.55);
    tint=mix(tint,vec3(1.0),line*line*0.6);
    return mix(base,tint,line)+rim*glow;
}
// A slow shimmering band of light across the nebula.
float shimmer(vec3 dir,float time) {
    vec3 drift=time*vec3(-0.05,0.085,-0.035);
    float n1=valueNoise3(dir*14.0+drift);
    float n2=valueNoise3(dir*42.0+drift*0.8);
    float n3=valueNoise3(dir*58.0+drift*1.5);
    float p1=pow(max(n1*n1-0.6,0.0)*2.8,4.0);
    float p2=pow(max(n2*n2-0.7,0.0)*3.4,3.0);
    float p3=pow(max(n3*n3-0.74,0.0)*4.0,2.6);
    float mask=sin(valueNoise3(dir*10.0+time*0.4)*3.14159)*0.5+0.5;
    return (p1*1.1+p2*0.9+p3*0.45)*mask;
}
// Soft cloud bands from three layers of noise at different scales.
float cloudBands(vec3 dir,float time) {
    vec3 p=dir*vec3(1.6,2.6,1.6);
    float t=time*0.12;
    float b1=valueNoise3(p+vec3(sin(t)*1.7,t*0.35,cos(t*0.8)));
    float b2=valueNoise3(p*0.85+vec3(cos(t*1.3)*2.1,t*0.25,4.0));
    float b3=valueNoise3(p*1.25+vec3(9.0,-t*0.3,sin(t*0.7)*2.6));
    float bands=pow(max(b1-0.6,0.0)*2.4,2.2)+pow(max(b2-0.64,0.0)*2.6,2.0)*0.8+pow(max(b3-0.62,0.0)*2.5,2.1)*0.7;
    float fade=0.55+0.45*valueNoise3(dir*vec3(5.0,13.0,5.0)+time*vec3(0.18,0.05,0.14));
    return bands*fade;
}
// Combines the layers in order: base, shimmer, cloud bands, orbs, and the web on top.
vec3 nebulaSky(vec3 dir,ColourPalette palette,float time) {
    vec3 colour=nebulaBase(dir,time,palette);
    float shimmerAmount=shimmer(dir,time);
    colour=mix(colour,palette . cream,clamp(shimmerAmount,0.0,1.0))+palette . cream*shimmerAmount*1.6;
    float bandAmount=cloudBands(dir,time);
    colour=mix(colour,palette . paleGold,clamp(bandAmount*0.5,0.0,1.0))+palette . cream*bandAmount*1.2;
    colour+=sparkOrbs(dir,time,palette)*0.55;
    colour=cellGlow(dir,colour,time,palette);
    return clamp(colour,0.0,1.0);
}
// Entry point called by the game for every sky pixel.
vec3 sky(vec3 dir,float time) {
    return nebulaSky(dir,nebulaPalette(),time);
}
