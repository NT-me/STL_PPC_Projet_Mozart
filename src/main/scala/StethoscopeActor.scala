package upmc.akka.leader

import akka.actor._

case class HeartSignal (id: Int)

class StethoscopeActor extends Actor {

  //println(context.self.path)
    def receive: Receive = {
        case HeartSignal (id) => {
               println(id)
          }

     }
}
