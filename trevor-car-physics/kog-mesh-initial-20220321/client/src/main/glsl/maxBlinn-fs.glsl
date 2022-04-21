#version 300 es

precision highp float;

in vec4 texCoord;
in vec4 shadow;
in vec4 worldNormal;
in vec4 worldPosition;

uniform struct{
    mat4 viewProjMatrix;
    vec3 position;
} camera;

uniform struct {
    sampler2D colorTexture;
    samplerCube envTexture;
} material;

out vec4 fragmentColor;

void main(void) {
    vec3 normal = normalize(worldNormal.xyz);
    vec3 viewDir = camera.position - worldPosition.xyz;

    vec3 lightDir = normalize(vec3(1,1,1));
    float cosa = clamp(dot(normal, lightDir), 0.0, 1.0);
    vec3 radiance = texture(material.colorTexture, texCoord.xy/texCoord.w).rgb * cosa;

//    vec3 radiance = vec3(0, 0,0);
//    for (int iLight = 0; iLight < lights.length(); iLight++){
////        vec3 lightDir
//    }

    fragmentColor = vec4(radiance, 1.0);
//    fragmentColor = vec4(cosa, cosa, cosa, 1);
//    fragmentColor = texture(material.envTexture, rayDir.xyz);
}