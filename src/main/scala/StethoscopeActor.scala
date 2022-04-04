package upmc.akka.leader

import akka.actor._

import scala.collection.mutable

case class HeartSignal (id: Int)

class StethoscopeActor (parentId: Int) extends Actor {

  var aliveIdsList: List[Int] = List()
  var stepCounter = 0
  val BUFFER_TIME_OUT = 2

    def receive: Receive = {
        case HeartSignal (id) => {

          if (!aliveIdsList.contains(id)) {
            aliveIdsList = aliveIdsList ::: List(id)
          }

          if (parentId == id){
            stepCounter += 1
            if (stepCounter > BUFFER_TIME_OUT) {
              context.parent ! ReInitAliveList
              context.parent ! AliveActor(aliveIdsList)

              aliveIdsList = List()
              stepCounter = 0
            }
          }

        }
     }
}
