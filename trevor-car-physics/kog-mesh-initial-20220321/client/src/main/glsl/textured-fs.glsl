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
    fragmentColor = texture(material.colorTexture, texCoord.xy/texCoord.w) * shadow;

    //    reflection stuff
    //    vec3 normal = normalize(worldNormal.xyz);
    //    vec3 viewDir = camera.position - worldPosition.xyz / worldPosition.w;
    //    vec3 reflDir = reflect(-viewDir, normal);
    //    fragmentColor = texture(material.envTexture, reflDir);

}