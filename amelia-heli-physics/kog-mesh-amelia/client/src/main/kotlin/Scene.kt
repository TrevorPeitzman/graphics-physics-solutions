import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL //# GL# we need this for the constants declared ˙HUN˙ a constansok miatt kell
import kotlin.js.Date
import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec1
import vision.gears.webglmath.Vec2
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4
import vision.gears.webglmath.Mat4
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos

class Scene (
  val gl : WebGL2RenderingContext)  : UniformProvider("scene") {

  val lights = Array<Light>(1) { Light(it, *Program.all) }

  val shadowMatrix by Mat4( 
          1.0f ,    0.0f ,    0.0f ,   0.0f, 
          0.0f ,    0.0f ,    0.0f ,   0.0f, 
          0.0f ,    0.0f ,    1.0f ,   0.0f, 
          0.0f ,    0.1f ,    0.0f ,   1.0f) //FIX SHADOW
  val vsShadow = Shader(gl, GL.VERTEX_SHADER, "shadow-vs.glsl")
  val fsShadow = Shader(gl, GL.FRAGMENT_SHADER, "shadow-fs.glsl")
  val shadowProgram = Program(gl, vsShadow, fsShadow)
  val shadowMaterial = Material(shadowProgram)

  val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
  val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")
  val texturedProgram = Program(gl, vsTextured, fsTextured)

  val vsQuad = Shader(gl, GL.VERTEX_SHADER, "quad-vs.glsl")
  val fsBackground = Shader(gl, GL.FRAGMENT_SHADER, "bg-fs.glsl")
  val backgroundProgram = Program(gl, vsQuad, fsBackground)
  val skyCubeTexture = TextureCube(gl, 
    /*
    "media/posx512.jpg",
    "media/negx512.jpg",
    "media/posy512.jpg",
    "media/negy512.jpg",
    "media/posz512.jpg",
    "media/negz512.jpg"
    */
    "media/sky/bluecloud_ft.jpg",
    "media/sky/bluecloud_bk.jpg",
    "media/sky/bluecloud_up.jpg",
    "media/sky/bluecloud_dn.jpg",
    "media/sky/bluecloud_rt.jpg",
    "media/sky/bluecloud_lf.jpg",
    )
  val backgroundMaterial = Material(backgroundProgram).apply{
    this["envTexture"]?.set( skyCubeTexture )
  }
  val texturedQuadGeometry = TexturedQuadGeometry(gl)
  val backgroundMesh = Mesh(backgroundMaterial, texturedQuadGeometry)

  val groundMaterial = Material(texturedProgram).apply{
    this["colorTexture"]?.set( Texture2D(gl, "media/grass.jpg") )
    this["envTexture"]?.set(skyCubeTexture)
  }
  val groundMesh = Mesh(groundMaterial, GroundGeometry(gl))


  // LABTODO: load geometries from the JSON file, create Meshes  
  val jsonLoader = JsonLoader()
  val slowpokeMeshes = jsonLoader.loadMeshes(gl,
    "media/slowpoke/slowpoke.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
          Texture2D(gl, "media/slowpoke/YadonDh.png"))
    },
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
          Texture2D(gl, "media/slowpoke/YadonEyeDh.png"))
    }
  )
  //this["envTexture"]?.set(bgTexture)

  // HELICOPTER BODY
  val heliMeshes = jsonLoader.loadMeshes(gl,
    "media/heli/heli1.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
          Texture2D(gl, "media/heli/heli.png"))
      this["envTexture"]?.set(skyCubeTexture)
    }
  )

  val rotarMeshes = jsonLoader.loadMeshes(gl,
    "media/heli/mainrotor.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
          Texture2D(gl, "media/heli/blades.png"))
      this["envTexture"]?.set(skyCubeTexture)
    }
  )

  val tailRotorMeshes = jsonLoader.loadMeshes(gl,
    "media/heli/tailrotor.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
          Texture2D(gl, "media/heli/blades.png"))
      this["envTexture"]?.set(skyCubeTexture)
    }
  )


  val gameObjects = ArrayList<GameObject>()

  /*
  Key Codes: ----------------------------------------------------
  K/I = lift
  J/L = roll
  U/O = pitch
  */

  var heli = GameObject(*heliMeshes).apply{
      move = object : GameObject.Motion(){
      override operator fun invoke(dt : Float, t : Float,
          keysPressed : Set<String>, gameObjects : List<GameObject>):Boolean {
        val gravity = Vec3(0f, -10.0f, 0f) //move out of function
        //lift.y = 10.0f

        var rotarAxisDir = Vec3((Vec4(0f, 1f, 0f, 0f) * modelMatrix).xyz)
        acceleration = gravity + rotarAxisDir * lift.y 
        velocity += acceleration * dt
        position += velocity * dt
        
        return true
        }
      }
    }

  init {
    //gameObjects += GameObject(*slowpokeMeshes).apply{
      //scale.set(0.1f, 0.1f, 0.1f)
      //roll = 1.5f
    //}

    lights[0].position.set(1.0f, 1.0f, 1.0f, 0.0f).normalize()
    lights[0].powerDensity.set(1.0f, 1.0f, 0.0f)


    gameObjects += GameObject(groundMesh).apply{
      pitch = 1.57079632679f
      noShadow = true
    }

    gameObjects += heli


    gameObjects += GameObject(*rotarMeshes).apply{
      move = object : GameObject.Motion(){
      //val velocity = Vec3(0.1f, 0.1f)
      override operator fun invoke(dt : Float, t : Float,
          keysPressed : Set<String>, gameObjects : List<GameObject>):Boolean {
        position.set(0.0f, 15.0f, 5.0f)
        parent = heli

        yaw += 7.0f * dt
        return true
        }
      }
    }

    gameObjects += GameObject(*tailRotorMeshes).apply{
      move = object : GameObject.Motion(){
      //val velocity = Vec3(0.1f, 0.1f)
      override operator fun invoke(dt : Float, t : Float,
          keysPressed : Set<String>, gameObjects : List<GameObject>):Boolean {
        position.set(1.0f, 10.0f, -35.0f)
        parent = heli

        pitch -= 7.0f * dt
        return true
        }
      }
    }
  }


  val camera = PerspectiveCamera(*Program.all).apply{
    position.set(25.0f, 50.0f, -150.0f)
    yaw = 3.14f
  }

  fun resize(canvas : HTMLCanvasElement) {
    gl.viewport(0, 0, canvas.width, canvas.height)//#viewport# tell the rasterizer which part of the canvas to draw to ˙HUN˙ a raszterizáló ide rajzoljon
    camera.setAspectRatio(canvas.width.toFloat()/canvas.height)
  }

  val timeAtFirstFrame = Date().getTime()
  var timeAtLastFrame =  timeAtFirstFrame

  init{
    //LABTODO: enable depth test
    addComponentsAndGatherUniforms(*Program.all)
  }

  @Suppress("UNUSED_PARAMETER")
  fun update(keysPressed : Set<String>) {
    val timeAtThisFrame = Date().getTime() 
    val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
    val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
    timeAtLastFrame = timeAtThisFrame

    camera.move(dt, keysPressed)


    if ("I" in keysPressed){
      heli.lift.y += 0.5f * dt
    }
    if ("K" in keysPressed){
      heli.lift.y -= 0.5f * dt
    }
    
    if ("L" in keysPressed){
      heli.roll += 0.01f 
    }
    if ("J" in keysPressed){
      heli.roll -= 0.01f
    }
    if ("O" in keysPressed){
      heli.pitch += 0.01f
    }
    if ("U" in keysPressed){
      heli.pitch -= 0.01f
    }

    heli.roll *= 0.98f
    heli.pitch *= 0.98f



    gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)//## red, green, blue, alpha in [0, 1]
    gl.clearDepth(1.0f)//## will be useful in 3D ˙HUN˙ 3D-ben lesz hasznos
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)//#or# bitwise OR of flags

    gl.enable(GL.DEPTH_TEST)
    gl.enable(GL.BLEND)
    gl.blendFunc(
      GL.SRC_ALPHA,
      GL.ONE_MINUS_SRC_ALPHA)


    gameObjects.forEach{ it.move(dt, t, keysPressed, gameObjects) }

    gameObjects.forEach{ it.update() }

    backgroundMesh.draw(camera)
    gameObjects.forEach{ it.draw(this, camera, *lights) }

  
    gameObjects.forEach {
      if(!it.noShadow){ // ground, background need no shadow
        it.using(shadowMaterial).draw(this, this.camera)
      }
    }

  }
}
