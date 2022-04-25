#version 300 es

precision highp float;

in vec4 texCoord;
in vec4 worldNormal;
in vec4 worldPosition;
//in vec4 shadowMatrix;

uniform struct {
  sampler2D colorTexture; 
  samplerCube envTexture;
} material;

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
  //fragmentColor = texture(material.colorTexture, texCoord.xy/texCoord.w) * scene.shadow; 
  vec3 normal = normalize(worldNormal.xyz);
  vec3 viewDir = camera.position - worldPosition.xyz/worldPosition.w;

  // vec3 lightDir = normalize(vec3(1, 1, 1));
  // float cosa = clamp(dot(normal, lightDir), 0.0, 1.0f);
  // vec3 radiance = (texture(material.envTexture, normalize(worldNormal.xyz)) * scene.shadow).rgb * cosa;
  // fragmentColor = vec4(radiance, 1);

  vec3 reflDir = reflect(-viewDir, normal);
  fragmentColor = texture(material.envTexture, normalize(worldNormal.xyz)) * scene.shadow;
  fragmentColor.a = 1.0;
}