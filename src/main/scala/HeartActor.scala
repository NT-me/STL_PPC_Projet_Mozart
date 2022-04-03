package upmc.akka.leader

import akka.actor._
import akka.remote.transport.ActorTransportAdapter.AskTimeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps

case class HeartBeat ()

class HeartActor (val parentID: Int, val terminaux:List[Terminal]) extends Actor {
  val TIME_BASE: FiniteDuration = 2 milliseconds
  val scheduler: Scheduler = context.system.scheduler
  var resetCounter: Int = 0

       def receive: Receive = {

          case HeartBeat() => {
              for(i <- terminaux.indices by 1){
                  val selectionnedActor =
                    context.actorSelection(
                      "akka.tcp://MozartSystem"+
                        terminaux(i).id+
                        "@"+terminaux(i).ip.replace("\"", "")+
                        ":" + terminaux(i).port + "/user/Musicien" +terminaux(i).id + "/stethoscopeActor")
                  selectionnedActor ! HeartSignal(parentID)
                }
            scheduler.scheduleOnce(TIME_BASE, self, HeartBeat())
          }
     }
}
