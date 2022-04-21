import org.w3c.dom.HTMLCanvasElement
import vision.gears.webglmath.Mat4
import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4
import kotlin.js.Date
import kotlin.math.PI
import org.khronos.webgl.WebGLRenderingContext as GL

class Scene(
    val gl: WebGL2RenderingContext
) : UniformProvider("scene") {

    val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
    val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")
    val vsQuad = Shader(gl, GL.VERTEX_SHADER, "quad-vs.glsl")
    val fsBackground = Shader(gl, GL.FRAGMENT_SHADER, "background-fs.glsl")
    val fsMaxBlinn = Shader(gl, GL.FRAGMENT_SHADER, "maxBlinn-fs.glsl")

    val texturedProgram = Program(gl, vsTextured, fsTextured)
    val backgroundProgram = Program(gl, vsQuad, fsBackground)
    val shadedProgram = Program(gl, vsTextured, fsMaxBlinn)

    val envTexture = TextureCube(
        gl,
        "media/posx512.jpg",
        "media/negx512.jpg",
        "media/posy512.jpg",
        "media/negy512.jpg",
        "media/posz512.jpg",
        "media/negz512.jpg"
    )

    val backgroundMaterial = Material(backgroundProgram).apply {
        this["envTexture"]?.set(envTexture)
    }

    val texturedQuadGeometry = TexturedQuadGeometry(gl)
    val backgroundMesh = Mesh(backgroundMaterial, texturedQuadGeometry)

    val jsonLoader = JsonLoader()

    val chevyMesh = jsonLoader.loadMeshes(
        gl,
        "media/chevy/chassis.json",
        Material(texturedProgram).apply {
            this["colorTexture"]?.set(
                Texture2D(gl, "media/chevy/chevy.png")
            )
            this["envTexture"]?.set(envTexture)
        },
    )

    val chevyShadedMesh = jsonLoader.loadMeshes(
        gl,
        "media/chevy/chassis.json",
        Material(shadedProgram).apply {
            this["colorTexture"]?.set(
                Texture2D(gl, "media/chevy/chevy.png")
            )
            this["envTexture"]?.set(envTexture)
        },
    )

    val wheelMesh = jsonLoader.loadMeshes(
        gl,
        "media/chevy/wheel.json",
        Material(texturedProgram).apply {
            this["colorTexture"]?.set(
                Texture2D(gl, "media/chevy/chevy.png")
            )
            this["envTexture"]?.set(envTexture)
        },
    )

    val groundMaterial = Material(texturedProgram).apply {
        this["colorTexture"]?.set(
            Texture2D(gl, "media/street.jpg")
        )
        this["envTexture"]?.set(envTexture)
    }
    val groundMesh = Mesh(groundMaterial, GroundGeometry(gl))

    val avatar = GameObject(*chevyMesh).apply {
        position.set(Vec3(0f, 7f, 0f))
        descriptor = "avatar"
//        move = object : GameObject.Motion() {
//            override operator fun invoke(
//                dt: Float, t: Float, keysPressed: Set<String>, gameObjects: List<GameObject>
//            ): Boolean {
//////                control()
////                Rose motion
//                val scale = 100
//                val x = scale * (cos(t) * sin(2f * t)) - 7f
//                //                    1st order derivative, just type the above "(cos(t) * sin(2f * t))" into wolfram
////                    d/dt(cos(t) sin(2 f t)) = 2 f cos(t) cos(2 f t) - sin(t) sin(2 f t)
//                val z = scale * (sin(t) * sin(2f * t)) + 5f
////                    d/dt(sin(t) sin(2 f t)) = 2 f sin(t) cos(2 f t) + cos(t) sin(2 f t)
//                position.set(x, position.y, z)
//
//                yaw = (atan2(
//                    2f * sin(t) * cos(2f * t) + cos(t) * sin(2f * t),
//                    2f * cos(t) * cos(2f * t) - sin(t) * sin(2f * t)
//                ))
////                yaw += PI.toFloat() / 4f
//                return true
//            }
//        }
    }

    val gameObjects = ArrayList<GameObject>()

    init {
        gameObjects += avatar
//        gameObjects += GameObject(*chevyShadedMesh).apply {
//            descriptor = "shaded chevy"
//            position.y = 10f
//            position.x = -50f
//        }

        gameObjects += GameObject(groundMesh).apply {
            pitch = PI.toFloat() / 2f
            scale *= 100f //(Vec3(200f))
            descriptor = "ground"
            position.y = -0.1f
        }

//        Rear 2 wheels
//        left
        gameObjects += GameObject(* wheelMesh).apply {
            parent = avatar
//            yaw = PI.toFloat()
            position.set(Vec3(6.5f, -4f, -11.1f))
        }
//        right
        gameObjects += GameObject(*wheelMesh).apply {
            parent = avatar
            position.set(Vec3(-6.5f, -4f, -11.1f))
        }

//        Front 2 wheels
//        left
        gameObjects += GameObject(*wheelMesh).apply {
            parent = avatar
//            v is ride height
            position.set(Vec3(6.5f, -4f, 13.85f))
//            yaw = PI.toFloat()
            descriptor = "front left"
        }
//        right
        gameObjects += GameObject(*wheelMesh).apply {
            parent = avatar
            position.set(Vec3(-6.5f, -4f, 13.85f))
            descriptor = "front"
        }

//        lights[0].position.set(1.0f, 1.0f, 1.0f, 0.0f).normalize()
//        lights[0].powerDenstiy.set(1.0f, 1.0f, 0.0f)
//        lights[1].position.set(-1.0f, -1.0f, 1.0f, 0.0f).normalize()
//        lights[1].powerDenstiy.set(0.0f, 1.0f, 1.0f)
    }

    val shadowMatrix by Mat4()
    val shadow by Vec4()

    // LABTODO: replace with 3D camera
    val camera = PerspectiveCamera(*Program.all).apply {
        position.set(0f, 65f, 200f)
    }

    fun resize(canvas: HTMLCanvasElement) {
        gl.viewport(
            0,
            0,
            canvas.width,
            canvas.height
        )//#viewport# tell the rasterizer which part of the canvas to draw to ˙HUN˙ a raszterizáló ide rajzoljon
        camera.setAspectRatio(canvas.width.toFloat() / canvas.height)
    }

    val timeAtFirstFrame = Date().getTime()
    var timeAtLastFrame = timeAtFirstFrame

    init {
        gl.enable(GL.DEPTH_TEST)
        addComponentsAndGatherUniforms(*Program.all)
    }

    @Suppress("UNUSED_PARAMETER")
    fun update(keysPressed: Set<String>) {
        val timeAtThisFrame = Date().getTime()
        val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
        val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
        timeAtLastFrame = timeAtThisFrame

        camera.move(dt, keysPressed)

        var turning = 0f
        var force = 0f
        var torque = 0f

        if ("UP" in keysPressed) {
            force = 100f
        }
        if ("DOWN" in keysPressed) {
            force = -100f
        }
        if ("LEFT" in keysPressed) {
            torque = 2f
            turning = 0.5f
        }
        if ("RIGHT" in keysPressed) {
            torque = -2f
            turning = -0.5f
        }

        gameObjects.forEach {
            if (it.parent == avatar) {
                if (it.descriptor.contains("front")) {
                    it.yaw = turning
                }
            }
            if (it == avatar) {
                it.force = force
                it.torque = torque
            }
        }

        gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)//## red, green, blue, alpha in [0, 1]
        gl.clearDepth(1.0f)//## will be useful in 3D ˙HUN˙ 3D-ben lesz hasznos
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)//#or# bitwise OR of flags

        gl.enable(GL.BLEND)
        gl.blendFunc(
            GL.SRC_ALPHA,
            GL.ONE_MINUS_SRC_ALPHA
        )

        backgroundMesh.draw(camera)

        gameObjects.forEach { it.move(dt, t, keysPressed, gameObjects) }
        gameObjects.forEach { it.update() }

        shadowMatrix.set()
        shadow.set(Vec4.ones)
        gameObjects.forEach { it.draw(this, camera) }

//        rough mouse way
//        val lx = camera.mouseDelta.x
//        val ly = camera.mouseDelta.y
//        val lz = 1f
//        shadowMatrix.set(
//            Mat4(
//                1f, 0.0f, 0.0f, 0.0f,
//                (-lx/ly), 0f, (-lz/ly), 0.0f, // -lx/ly, 0, -lz/ly, 0 where lx, ly, and lz all are a vector from the origin pointing to the light source
//                0.0f, 0.0f, 1f, 0.0f,
//                0.0f, 0.001f, 0f, 1.0f
//            )
//        )

//        og way, directly below
        shadowMatrix.set().scale(1f, 0f, 1f).translate(0f, 0.00001f, 0f)
        shadow.set(0f, 0f, 0f, 1f)
//        gameObjects.forEach { it.draw(this, camera) }
        gameObjects.forEach {
            if (it.descriptor != "ground") {
                it.draw(this, camera)
            }
        }
    }
}
