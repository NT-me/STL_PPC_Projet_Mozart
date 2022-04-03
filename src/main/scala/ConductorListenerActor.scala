package upmc.akka.leader

import akka.actor._
import upmc.akka.leader.DataBaseActor.{Chord, Measure}

case class PlayMeasure(chords: List [Chord])

class ConductorListenerActor(parentID: Int, player: ActorRef) extends Actor {

     def receive: Receive = {

          case PlayMeasure (chords) => {
               player ! Measure(chords)
               println(parentID+" Joue !")
          }

     }
}
