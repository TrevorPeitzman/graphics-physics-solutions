#version 300 es

precision highp float;

in vec4 texCoord;
in vec4 worldNormal;
in vec4 worldPosition;

uniform struct {
  sampler2D colorTexture; 
  samplerCube envTexture;
} material;

uniform struct{
  mat4 viewProjMatrix; 
  vec3 position; 
} camera;

uniform struct{
  vec4 position;
  vec3 powerDensity;
} lights[8];

out vec4 fragmentColor;


void main(void) {
  vec3 normal = normalize(worldNormal.xyz);
  vec3 viewDir = camera.position - worldPosition.xyz/worldPosition.w;

  vec3 radience = vec3(0,0,0);
  for(int iLight = 0; iLight < 1; iLight++){
    vec3 lightDir = lights[iLight].position.xyz;
    vec3 powerDensity = lights[iLight].powerDensity;
    float cosa = clamp(dot(normal, lightDir), 0.0, 1.0);
    radience += texture(material.colorTexture, texCoord.xy/texCoord.w).rgb * cosa * powerDensity;
  }
  fragmentColor = vec4(radience, 1.0);



  //MIRROR
  //vec3 normal = normalize(worldNormal.xyz);
  //fragmentColor = texture(material.colorTexture, texCoord.xy/texCoord.w); //UNCOMMENT
  //vec3 viewDir = camera.position - worldPosition.xyz/worldPosition.w;
  //vec3 reflDir = reflect(-viewDir, normal);
  //fragmentColor = texture(material.envTexture, reflDir);
}