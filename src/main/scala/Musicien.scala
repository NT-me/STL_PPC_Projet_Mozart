package upmc.akka.leader

import akka.actor._

case class Start ()
case class Test (msg: String)

class Musicien (val id:Int, val terminaux:List[Terminal]) extends Actor {

     // Les differents acteurs du systeme
     val displayActor: ActorRef = context.actorOf(Props[DisplayActor], name = "displayActor")
     val heartActor: ActorRef = context.actorOf(Props(new HeartActor(id, terminaux)), name = "heartActor")
     val stethoscopeActor: ActorRef = context.actorOf(Props(new StethoscopeActor()), name = "stethoscopeActor")

     def receive: Receive = {

          // Initialisation
          case Start => {
               displayActor ! Message ("Musicien " + this.id + " is created")
               heartActor ! HeartBeat()
          }

          case Test(msg) => {
               println(msg+" "+id)
          }

     }
}
