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
import kotlin.math.PI

class Scene (
  val gl : WebGL2RenderingContext)  : UniformProvider("scene") {

  val vsTextured = Shader(gl, GL.VERTEX_SHADER, "mirror-vs.glsl")
  val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")

  val vsMirror = Shader(gl, GL.VERTEX_SHADER, "mirror-vs.glsl")
  val fsMirror = Shader(gl, GL.FRAGMENT_SHADER, "mirror-fs.glsl")

  val vsQuad = Shader(gl, GL.VERTEX_SHADER, "quad-vs.glsl")
  val fsBG = Shader(gl, GL.FRAGMENT_SHADER, "bg-fs.glsl")
  var newVel = 5.0f

    val envTexture = TextureCube(gl, 
    "media/posx512.jpg",
    "media/negx512.jpg",
    "media/posy512.jpg",
    "media/negy512.jpg",
    "media/posz512.jpg",
    "media/negz512.jpg"
    )

  val backgroundProgram = Program(gl, vsQuad, fsBG)
  val backgroundMaterial = Material(backgroundProgram).apply{
    this["envTexture"]?.set(envTexture)
  }

  val mirrorProgram = Program(gl, vsMirror, fsMirror)
  //val mirrorQuadGeometry = mirrorQuadGeometry(gl)

  val texturedProgram = Program(gl, vsTextured, fsTextured)
  val texturedQuadGeometry = TexturedQuadGeometry(gl)

  val quadGeometry = TexturedQuadGeometry(gl)

  val backgroundMesh = Mesh(backgroundMaterial, quadGeometry)

  val lights = Array<Light>(8){ Light(it, *Program.all)}

  // LABTODO: load geometries from the JSON file, create Meshes  
  val jsonLoader = JsonLoader()




  val soccerMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
        Texture2D(gl, "media/sphere/ball.png"))
      }
  )
  val baseballMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
        Texture2D(gl, "media/sphere/WiiBaseballBall.png"))
      }
  )
  val pokeballMesh = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
        Texture2D(gl, "media/sphere/pokeball.jpg"))
      }
  )

  val groundMaterial = Material(texturedProgram).apply{
    this["colorTexture"]?.set(Texture2D(gl, "media/poolTable.jpg"))
  }

  val groundMesh = Mesh(groundMaterial, GroundGeometry(gl))

  //POLLBALLS
    val cueMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/cue.png"))
        }
    )
    val oneMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/one.jpg"))
        }
    )
    val twoMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/two.jpg"))
        }
    )
    val threeMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/three.jpg"))
        }
    )
    val fourMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/four.jpg"))
        }
    )
    val fiveMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/five.jpg"))
        }
    )
    val sixMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/six.jpg"))
        }
    )
    val sevenMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/seven.jpg"))
        }
    )
    val eightBallMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/eight.jpg"))
        }
    )
    val nineMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/nine.jpg"))
        }
    )
    val tenMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/ten.jpg"))
        }
    )
    val elevenMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/eleven.jpg"))
        }
    )
    val twelveMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/twelve.jpg"))
        }
    )
    val thirteenMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/thirteen.jpg"))
        }
    )
    val fourteenMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/fourteen.jpg"))
        }
    )
    val fifteenMeshes = jsonLoader.loadMeshes(gl, "media/sphere/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
          Texture2D(gl, "media/sphere/fifteen.jpg"))
        }
    )

  //POOLBALLS

  val shadowMatrix by Mat4()
  val shadow by Vec4()


  val soccerBall = GameObject(*soccerMeshes).apply{
    // scale.set(0.3f, 0.3f, 0.3f)
    position.set(-3.0f, 1.0f, 3.0f)
  }
  val baseBall = GameObject(*baseballMeshes).apply{
    scale.set(0.3f, 0.3f, 0.3f)
    position.set(-2.0f, 0.3f, 3.0f)
    rotationMatrix.rotate(PI.toFloat())
  }
  val pokeball = GameObject(*pokeballMesh).apply{
    scale.set(0.3f, 0.3f, 0.3f)
    position.set(-3.0f, 0.3f, 3.0f)
    rotationMatrix.rotate(PI.toFloat())
  }
  val ground = GameObject(groundMesh).apply{
    rotationMatrix.rotate(PI.toFloat()/2.0f, 1.0f, 0f, 0f)
    position.set(0.0f, -0.001f, 0.0f)
  }

  //POOLBALLS
    val cue = GameObject(*cueMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(-1.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val one = GameObject(*oneMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(0.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val two = GameObject(*twoMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(1.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val three = GameObject(*threeMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(2.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val four = GameObject(*fourMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(3.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val five = GameObject(*fiveMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(4.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val six = GameObject(*sixMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(5.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val seven = GameObject(*sevenMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(6.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val eightBall = GameObject(*eightBallMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(7.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val nine = GameObject(*nineMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(8.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val ten = GameObject(*tenMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(9.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val eleven = GameObject(*elevenMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(10.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val twelve = GameObject(*twelveMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(11.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val thirteen = GameObject(*thirteenMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(12.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val fourteen = GameObject(*fourteenMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(13.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    val fifteen = GameObject(*fifteenMeshes).apply{
      scale.set(0.3f, 0.3f, 0.3f)
      position.set(14.0f, 0.3f, 3.0f)
      rotationMatrix.rotate(PI.toFloat())
    }
    
  //POOLBALLS

  val gameObjects = ArrayList<GameObject>()


  init {
    gameObjects += baseBall
    gameObjects += pokeball
    gameObjects += cue
    gameObjects += one
    gameObjects += two
    gameObjects += three
    gameObjects += four
    gameObjects += five
    gameObjects += six
    gameObjects += seven
    gameObjects += eightBall
    gameObjects += nine
    gameObjects += ten
    gameObjects += eleven
    gameObjects += twelve
    gameObjects += thirteen
    gameObjects += fourteen
    gameObjects += fifteen
    //gameObjects += soccerBall
    //baseBall.velocity.set(5f, 0f, 5f)
    lights[0].position.set(1.0f, 1.0f, 1.0f, 1.0f).normalize()
    lights[0].powerDensity.set(1.0f,1.0f,1.0f)
    lights[1].position.set(-1.0f, -1.0f, 1.0f, 1.0f).normalize()
    lights[1].powerDensity.set(2.0f,2.0f,2.0f)
  }

  // LABTODO: replace with 3D camera
  val camera = PerspectiveCamera(*Program.all).apply{
    position.set(0.0f, 4.0f, 12.0f)
    rayDirMatrix.set().translate(position)
    rayDirMatrix *= viewProjMatrix
    rayDirMatrix.invert()
  }

  fun resize(canvas : HTMLCanvasElement) {
    gl.viewport(0, 0, canvas.width, canvas.height)//#viewport# tell the rasterizer which part of the canvas to draw to ˙HUN˙ a raszterizáló ide rajzoljon
    camera.setAspectRatio(canvas.width.toFloat()/canvas.height)
  }

  val timeAtFirstFrame = Date().getTime()
  var timeAtLastFrame =  timeAtFirstFrame

 

  init{
    gl.enable(GL.DEPTH_TEST)
    addComponentsAndGatherUniforms(*Program.all)
  }

  @Suppress("UNUSED_PARAMETER")
  fun update(keysPressed : Set<String>) {
    val timeAtThisFrame = Date().getTime() 
    val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
    val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
    timeAtLastFrame = timeAtThisFrame
    

    camera.move(dt, keysPressed)


    if("1" in keysPressed){
      newVel = 1.0f
    }
    if("2" in keysPressed){
      newVel = 2.0f
    }
    if("3" in keysPressed){
      newVel = 3.0f
    }
    if("4" in keysPressed){
      newVel = 4.0f
    }
    if("5" in keysPressed){
      newVel = 5.0f
    }
    if("6" in keysPressed){
      newVel = 6.0f
    }
    if("7" in keysPressed){
      newVel = 7.0f
    }
    if("8" in keysPressed){
      newVel = 8.0f
    }
    if("9" in keysPressed){
      newVel = 9.0f
    }
    if("0" in keysPressed){
      newVel = 50.0f
    }


    if("UP" in keysPressed){
      cue.velocity.set(0f, 0f, -newVel.toFloat())
    }
    if("DOWN" in keysPressed){
      cue.velocity.set(0f, 0f, newVel.toFloat())
    }
    if("RIGHT" in keysPressed){
      cue.velocity.set(newVel.toFloat(), 0f, 0f)
    }
    if("LEFT" in keysPressed){
      cue.velocity.set(-newVel.toFloat(), 0f, 0f)
    }

    
    gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)//## red, green, blue, alpha in [0, 1]
    gl.clearDepth(1.0f)//## will be useful in 3D ˙HUN˙ 3D-ben lesz hasznos
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)//#or# bitwise OR of flags

    gl.enable(GL.BLEND)
    gl.blendFunc(
      GL.SRC_ALPHA,
      GL.ONE_MINUS_SRC_ALPHA)

    gameObjects.forEach{ it.move(dt, t, keysPressed, gameObjects) }

    backgroundMesh.draw(camera)

    // lights[0].position.x = cos(t)
    // lights[0].position.z = sin(t)
    // lights[0].position.normalize()
    // lights[1].position.x = cos(t)
    // lights[1].position.z = sin(t)
    // lights[1].position.normalize()

    shadowMatrix.set()
    shadow.set(1.0f, 1.0f, 1.0f, 1.0f)

    gameObjects.forEach{ it.update() }
    gameObjects.forEach{ it.draw(this, camera, *lights) }


    ground.update()
    ground.draw()

    shadowMatrix.set().scale(Vec3(1.0f, 0.0f, 1.0f)).translate(Vec3(0.0f, 0.000001f, 0.0f))
    shadow.set(0.0f, 0.0f, 0.0f, 1.0f)

    gameObjects.forEach{ it.draw(this, camera) }


  }
}
