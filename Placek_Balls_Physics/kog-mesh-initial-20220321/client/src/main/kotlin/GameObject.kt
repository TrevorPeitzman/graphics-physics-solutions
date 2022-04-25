import vision.gears.webglmath.*
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.floor

open class GameObject(
  vararg val meshes : Mesh
   ) : UniformProvider("gameObject") {

  val mass = 1
  val angularMass = 1
  val velocity = Vec3()
 // val angularAcceleration = 0





  val position = Vec3()
  var roll = 0.0f
  var pitch = 0.0f 
  var yaw = 0.0f 
  val scale = Vec3(1.0f, 1.0f, 1.0f)

  val modelMatrix by Mat4()
  val modelMatrixInverse by Mat4()
  val rotationMatrix by Mat4()

  var parent : GameObject? = null

  init { 
    addComponentsAndGatherUniforms(*meshes)
  }

  fun update() {
    //do I need to make a move function or maybe an animations subclass?????
    //maybe make another subclass that categorizes physics for a ball so then
    //I can make other physics for objects that dont roll
    modelMatrix.set().
      scale(scale)
      modelMatrix *= rotationMatrix
      modelMatrix.translate(position)
    parent?.let{ parent -> 
      modelMatrix *= parent.modelMatrix
    }
    modelMatrixInverse.set(modelMatrix).invert()
  }



  open inner class Motion {
    open operator fun invoke(
      dt : Float = 0.016666f, 
      t : Float = 0.0f, 
      keysPressed : Set<String> = emptySet<String>(), 
      gameObjects : List<GameObject> = emptyList<GameObject>()
      ) : Boolean { 
        position += velocity * dt
        gameObjects.forEach{
          if(it != this@GameObject){
            if((it.position - position).length() < 0.6f){
              var diffNorm = (it.position - position).normalize()
              it.position += diffNorm * .01f 
              position -= diffNorm * .01f
              var relVel = it.velocity - velocity
              var impulse = diffNorm.dot(relVel) / (1f/1f + 1f/1f) * (1f + 0.5f)
              it.velocity += -impulse / 1f * diffNorm
              velocity += impulse / 1f * diffNorm

            }
          }
        }
        rotationMatrix.rotate(-velocity.length()/0.3f*dt, velocity.cross(Vec3(0f,1f,0f)))
        velocity *= 0.99f
        return true 
    }
  }
  var move = Motion()

}
