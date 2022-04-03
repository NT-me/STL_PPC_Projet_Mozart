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
               aliveList.filter(x => x._2).foreach(u => preSelectedList = preSelectedList ::: List(u._1))

               val r = scala.util.Random

               var selectedId = parentID

               while(selectedId == parentID) {
                    selectedId = preSelectedList(r.nextInt(preSelectedList.size))
               }

               if(selectedId != parentID) {
                    println(selectedId + " A vous de jouer")
                    terminaux.filter(u => u.id == selectedId)

                    val selectionnedActor =
                         context.actorSelection(
                              "akka.tcp://MozartSystem" +
                                terminaux.head.id +
                                "@" + terminaux.head.ip.replace("\"", "") +
                                ":" + terminaux.head.port + "/user/Musicien" + terminaux.head.id + "/conductorListenerActor")

                    selectionnedActor ! PlayMeasure(chords)
               }
          }

     }
}
