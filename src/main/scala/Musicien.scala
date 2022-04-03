package upmc.akka.leader

import akka.actor._

import scala.collection.mutable

case class Start ()
case class Test (msg: String)
case class ReInitAliveList()
case class AliveActor(actorId: Int)

class Musicien (val id:Int, val terminaux:List[Terminal]) extends Actor {

     // Les differents acteurs du systeme
     val displayActor: ActorRef = context.actorOf(Props[DisplayActor], name = "displayActor")
     val heartActor: ActorRef = context.actorOf(Props(new HeartActor(id, terminaux)), name = "heartActor")
     val stethoscopeActor: ActorRef = context.actorOf(Props(new StethoscopeActor()), name = "stethoscopeActor")

     var aliveList: mutable.HashMap[Int, Boolean] = new mutable.HashMap()

     def receive: Receive = {

          // Initialisation
          case Start => {
               terminaux.foreach(x=> aliveList.put(x.id, false)) // Set hashmap
               aliveList.update(id, true)
               displayActor ! Message ("Musicien " + this.id + " is created")
               heartActor ! HeartBeat()
          }

          case Test(msg) => {
               println(msg+ " " +id)
          }

          case ReInitAliveList => {
           aliveList.foreach(x => aliveList.update(x._1, false)) // Re set all values on false
           aliveList.update(id, true)
          }

          case AliveActor (actorId) =>{
               aliveList.update(actorId, true)
               println(aliveList)
          }
     }
}
