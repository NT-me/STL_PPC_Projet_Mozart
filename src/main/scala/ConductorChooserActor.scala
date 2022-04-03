package upmc.akka.leader

import akka.actor._
import upmc.akka.leader.Terminal

import scala.collection.mutable

case class ChooseNewConductor (remainingIdsList:mutable.HashMap[Int, Boolean])

class ConductorChooserActor(val parentID: Int, val terminaux:List[Terminal]) extends Actor {

     def receive: Receive = {

          case ChooseNewConductor (remainingIdsList) => {

               var idList: List[Int] = List()
               remainingIdsList.iterator.filter(x => x._2).foreach(x => idList = idList ::: List(x._1))

               val selectedId: Int = if (idList.nonEmpty) idList.min else parentID

               for(i <- terminaux.indices by 1){
                    val selectionnedActor =
                         context.actorSelection(
                              "akka.tcp://MozartSystem"+
                                terminaux(i).id+
                                "@"+terminaux(i).ip.replace("\"", "")+
                                ":" + terminaux(i).port + "/user/Musicien" +terminaux(i).id + "/conductorChoiceAddresseeActor")
                    selectionnedActor ! NewConductorChoice(selectedId)
               }
          }
     }
}
