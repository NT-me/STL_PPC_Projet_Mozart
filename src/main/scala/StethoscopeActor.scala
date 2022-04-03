package upmc.akka.leader

import akka.actor._

import scala.collection.mutable

case class HeartSignal (id: Int)

class StethoscopeActor (parentId: Int) extends Actor {

  var aliveIdsList: List[Int] = List()

    def receive: Receive = {
        case HeartSignal (id) => {

          if (!aliveIdsList.contains(id)) {
            aliveIdsList = aliveIdsList ::: List(id)
          }

          if (parentId == id){
            context.parent ! ReInitAliveList
            context.parent ! AliveActor(aliveIdsList)
            aliveIdsList = List()
          }

        }
     }
}
