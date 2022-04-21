import vision.gears.webglmath.Mat4
import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec3
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

open class GameObject(
    vararg val meshes: Mesh
) : UniformProvider("gameObject") {

    var descriptor = ""
    var modelMatrixInverse = Mat4()
    val position = Vec3()
    var velocity = Vec3()
    var acceleration = Vec3()
    var angularVelocity = 0f
    var dampedAngularVel = 0f
    var angularAcceleration = 0f
    var ahead = Vec3()
    var aheadVelocity = Vec3()
    var sideVelocity = Vec3()

    var mass = 1.0f
    var force = 0.0f
    var torque = 0.0f

    var roll = 0.0f
    var yaw = 0.0f
    var pitch = 0.0f

    val scale = Vec3(1.0f, 1.0f, 1.0f)
    var turnFactor = 0f

    val modelMatrix by Mat4()

    var parent: GameObject? = null

    init {
        addComponentsAndGatherUniforms(*meshes)
    }

    fun update() {
        modelMatrix.set()
            .scale(scale)
            .rotate(roll)
            .rotate(pitch, 1.0f, 0.0f, 0.0f)
            .rotate(yaw, 0.0f, 1.0f, 0.0f)
            .translate(position)
        parent?.let { parent ->
            modelMatrix *= parent.modelMatrix
        }
//        val temp = modelMatrix
//        modelMatrixInverse.set(temp.invert())
    }

    open inner class Motion {
        open operator fun invoke(
            dt: Float = 0.016666f,
            t: Float = 0.0f,
            keysPressed: Set<String> = emptySet<String>(),
            gameObjects: List<GameObject> = emptyList<GameObject>()
        ): Boolean {
//            control(dt, keysPressed)


            // --- ROTATIONAL --- //
//            Sigmoid function https://en.wikipedia.org/wiki/Sigmoid_function
            turnFactor = 1f / (1f + exp(-0.3f * velocity.length() + 6f))

//            flip turn angle when going in reverse
            if (force < 0) {
                torque *= -1
            }

            angularAcceleration = torque * mass.pow(-1)
            angularVelocity += angularAcceleration * dt
            dampedAngularVel =
                torque / (1f + exp(-8f * angularVelocity + 6f)) //another sigmoid function https://www.desmos.com/calculator/kn9tpwdan5
//            angularVelocity *= exp(-dt * 1f) //og drag attempt, seemed to work sorta
            yaw = yaw.plus(dampedAngularVel * dt * turnFactor)


            // --- DIRECTIONAL --- //
            ahead = Vec3(sin(yaw), 0f, cos(yaw)).normalize()
            acceleration.set(Vec3(force * sin(yaw), 0f, force * cos(yaw)) * mass.pow(-1))
            velocity.plusAssign(acceleration * dt)
//            velocity = velocity.times(exp(-dt * 1f)) //simple drag attempt, seems to work. OLD

            aheadVelocity = ahead.times(ahead.dot(velocity))
            sideVelocity = velocity.minus(aheadVelocity)
            velocity.timesAssign(Vec3.zeros) // note you cannot just say velocity = Vec3.zeros, this does not work

            velocity.plusAssign(aheadVelocity * 0.5f.pow(dt)) //TODO: relatively untuned constant
            velocity.plusAssign(sideVelocity * 0.00005f.pow(dt)) // attenuate out the "drifting", smaller constant


            // --- DEBUGGING --- //
//            if (descriptor == "avatar") {
////                console.log("force: " + force)
//                console.log("max velocity: " + maxVelocity)
//                maxVelocity = velocity.length()
//                console.log("velocity: " + velocity.length())
//                console.log("ahead velocity: " + aheadVelocity.length())
//                console.log("side velocity: " + sideVelocity.length())
//                console.log("accel: " + acceleration.length())
//                console.log("aheadx: " + ahead.x, "aheady: " + ahead.y, "aheadz: " + ahead.z)
//
////                console.log("torque: " + torque)
//            console.log("ang-vel: " + angularVelocity)
//            console.log("ang-accel: " + angularAcceleration)
//            }


            // --- MISC NOTES (from slides) --- //
//            velocity =
//                velocity.cross(Vec3(sin(yaw) * exp(-dt * 2f), 0f, cos(yaw) * exp(-dt * 2f))) //drag attempt, seems to work
//            if (descriptor == "avatar")
//                position += velocity * dt

//            velocity.set(
//                (
//                        velocity.xyz0 * modelMatrix.invert()
//                                *
//                                Vec4(exp(-dt * 1f), exp(-dt * 1f), 1.0f, 0.0f)
//                                *
//                                modelMatrix
//                        ).xyz
//            )


            // --- WHEEL ROLLING --- //
//          speed-based wheel turning (in x), wrong
//            pitch = pitch.plus((parent?.velocity?.length() ?: 0f) * dt)

//            dot-prod speed-based wheel turning, courtesy of prof
            pitch = pitch.minus(
                (parent?.velocity?.dot(
                    Vec3(-sin(parent?.yaw ?: 0f).toFloat(), 0f, -cos(parent?.yaw ?: 0f).toFloat())
                )
                    ?: 0f) * 0.008f // TODO: may need to tune this in a bit more, it dictates how fast the wheels move in relation to the speed of the car
            )

            // don't move the ground
            if (descriptor != "ground") {
                position += velocity * dt
            }

            return true
        }
    }

    var move = Motion()

    fun control(dt: Float, keysPressed: Set<String>) {
        acceleration.set()

        if ("UP" in keysPressed) {
            acceleration.plusAssign(Vec3(this.mass * 2))
        }
        if ("DOWN" in keysPressed) {
            acceleration.plusAssign(Vec3(this.mass * -2))
        }
        if ("LEFT" in keysPressed) {
        }
        if ("RIGHT" in keysPressed) {
        }
    }

}
