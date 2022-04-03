package upmc.akka.leader

import akka.actor._

case class NewConductorChoice (newConductorId:Int)

class ConductorChoiceAddresseeActor extends Actor {

     def receive: Receive = {

          case NewConductorChoice (newConductorId) => {
               context.parent ! SetNewConductor(newConductorId)
          }

     }
}
