package upmc.akka.leader

import akka.actor._
import akka.remote.transport.ActorTransportAdapter.AskTimeout

case class HeartBeat ()

class HeartActor (val parentID: Int, val terminaux:List[Terminal]) extends Actor {

       def receive: Receive = {

          case HeartBeat() => {
              for(i <- terminaux.indices by 1){
                val selectionnedActor =
                  context.actorSelection(
                    "akka.tcp://MozartSystem"+
                      terminaux(i).id+
                      "@"+terminaux(i).ip.replace("\"", "")+
                      ":"+terminaux(i).port+"/user/Musicien"+terminaux(i).id+"/stethoscopeActor")
                selectionnedActor ! HeartSignal(parentID)
              }
          }
     }
}
