package upmc.akka.leader

import akka.actor._

import scala.collection.mutable

case class Start ()
case class ReInitAliveList()
case class AliveActor(actorsIds: List[Int])
case class DeadActor(actorId: Int)
case class SetNewConductor(newConductorId: Int)

class Musicien (val id:Int, val terminaux:List[Terminal]) extends Actor {

     // Les differents acteurs du systeme
     val displayActor: ActorRef = context.actorOf(Props[DisplayActor], name = "displayActor")
     val heartActor: ActorRef = context.actorOf(Props(new HeartActor(id, terminaux)), name = "heartActor")
     val stethoscopeActor: ActorRef = context.actorOf(Props(new StethoscopeActor(id)), name = "stethoscopeActor")
     val conductorChooserActor: ActorRef = context.actorOf(Props(new ConductorChooserActor(id, terminaux)), name = "conductorChooserActor")
     val conductorChoiceAddresseeActor: ActorRef = context.actorOf(Props(new ConductorChoiceAddresseeActor()), name = "conductorChoiceAddresseeActor")

     var aliveList: mutable.HashMap[Int, Boolean] = new mutable.HashMap() // Updated list of all alive actors
     var conductorId: Int = -1 // Conductor id, if -1 not init.

     def receive: Receive = {

          // Initialisation
          case Start => {
               terminaux.foreach(x=> aliveList.put(x.id, false)) // Set hashmap
               aliveList.update(id, true)
               displayActor ! Message ("Musicien " + this.id + " is created")
               heartActor ! HeartBeat()
          }

          case ReInitAliveList => {
               aliveList.foreach(x => aliveList.put(x._1, false)) // Re set all values on false
          }

          case AliveActor (actorsIds) =>{
               actorsIds.foreach(actorId => aliveList.update(actorId, true))

               if (!aliveList.getOrElse(conductorId, false)){
                    conductorChooserActor ! ChooseNewConductor(aliveList)
               }
          }

          case SetNewConductor(newConductorId) =>{
               conductorId = newConductorId
               println("Le nvx chef d'orcheste est " + conductorId)
          }
     }
}
