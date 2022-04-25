#version 300 es

precision highp float;

in vec4 texCoord;
in vec4 worldNormal;
in vec4 worldPosition;
//in vec4 shadowMatrix;

uniform struct {
  sampler2D colorTexture; 
} material;

uniform struct {
  vec4 position;
  vec3 powerDensity;
} lights[8];

uniform struct{
  mat4 viewProjMatrix; 
  vec3 position;
} camera;

uniform struct {
  float time;
  mat4 shadowMatrix;
  vec4 shadow;
} scene;

out vec4 fragmentColor;

void main(void) {

  vec3 normal = normalize(worldNormal.xyz);
  vec3 viewDir = camera.position - worldPosition.xyz/worldPosition.w;
  vec3 radiance = vec3(0,0,0);

  for(int iLight=0; iLight < 2; iLight++){
    vec3 lightDir = lights[iLight].position.xyz;
    vec3 powerDensity = lights[iLight].powerDensity;
    float cosa = clamp(dot(normal, lightDir), 0.0, 1.0f);
    radiance += (texture(material.colorTexture, texCoord.xy/texCoord.w) * scene.shadow).rgb * cosa * powerDensity;
  }
  
  fragmentColor = vec4(radiance, 1.0);
  //fragmentColor = texture(material.colorTexture, texCoord.xy/texCoord.w) * scene.shadow; 
}