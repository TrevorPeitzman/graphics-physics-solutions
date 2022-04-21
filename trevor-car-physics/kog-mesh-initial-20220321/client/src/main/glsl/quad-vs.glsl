#version 300 es

in vec4 vertexPosition; //#vec4# A four-element vector [x,y,z,w].; We leave z and w alone.; They will be useful later for 3D graphics and transformations. #vertexPosition# attribute fetched from vertex buffer according to input layout spec
in vec4 vertexTexCoord;

uniform struct{
//  mat4 viewProjMatrix;
  mat4 rayDirMatrix;
} camera;

uniform struct {
  float time;
  mat4 shadowMatrix;
} scene;

out vec4 texCoord;
out vec4 rayDir;

void main(void) {
  texCoord = vertexTexCoord;
  gl_Position = vertexPosition;
  gl_Position.z = 0.99999;
  rayDir = vertexPosition * camera.rayDirMatrix;
}