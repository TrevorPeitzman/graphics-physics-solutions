#version 300 es

precision highp float;

in vec4 texCoord;

uniform struct {
  sampler2D colorTexture; 
} material;

out vec4 fragmentColor;

void main(void) {
  fragmentColor = vec4(0.0f, 0.0f, 0.0f, 0.0f); 
}