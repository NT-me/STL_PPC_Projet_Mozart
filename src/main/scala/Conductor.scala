package upmc.akka.leader

import akka.actor._

import math._
import javax.sound.midi._
import javax.sound.midi.ShortMessage._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import DataBaseActor._

import scala.collection.mutable
import scala.language.postfixOps

case class giveMeasure(chords: List [Chord])
case class StartGame(aliveList: mutable.HashMap[Int, Boolean])

class Conductor(provider: ActorRef, terminaux:List[Terminal], parentID: Int) extends Actor {
     var aliveList: mutable.HashMap[Int, Boolean] = mutable.HashMap()

     def receive: Receive = {
          case StartGame (aliveListSended: mutable.HashMap[Int, Boolean]) => {
               aliveList = aliveListSended
               val r = scala.util.Random
               provider ! getMeasure(r.nextInt(5) + r.nextInt(5))

          }

          case giveMeasure(chords: List [Chord]) =>{
               var preSelectedList: List[Int] = List()
               aliveList.filter(x => x._2 && x._1 != parentID).foreach(u => preSelectedList = preSelectedList ::: List(u._1))

               val r = scala.util.Random
               var selectedId = parentID

               if (preSelectedList.nonEmpty) {
                    selectedId = preSelectedList(r.nextInt(preSelectedList.size))


                    val selectedTerminal = terminaux.filter(u => u.id == selectedId).head
                    println(selectedTerminal + " A vous de jouer")

                    val selectionnedActor =
                         context.actorSelection(
                              "akka.tcp://MozartSystem" +
                                selectedTerminal.id +
                                "@" + selectedTerminal.ip.replace("\"", "") +
                                ":" + selectedTerminal.port + "/user/Musicien" + selectedTerminal.id + "/conductorListenerActor")

                    selectionnedActor ! PlayMeasure(chords)
                    context.parent ! ComeOnMaestro()
               }
          }

     }
}
