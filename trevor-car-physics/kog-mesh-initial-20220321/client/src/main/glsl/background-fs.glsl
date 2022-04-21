#version 300 es

precision highp float;

in vec4 rayDir;

uniform struct {
    samplerCube envTexture;
    vec4 shadow;
} material;

out vec4 fragmentColor;

void main(void) {
    fragmentColor = texture(material.envTexture, rayDir.xyz);
}