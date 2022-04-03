package upmc.akka.leader

import akka.actor._
import math._

import javax.sound.midi._
import javax.sound.midi.ShortMessage._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

import akka.actor.{Props, Actor, ActorRef, ActorSystem}

import DataBaseActor._

case class Message (content:String)

case class giveMeasure(chords: List [Chord])
case class StartGame()

class Conductor(provider: ActorRef, player: ActorRef) extends Actor {

     def receive: Receive = {
          case StartGame () => {
               val r = scala.util.Random
               provider ! getMeasure(r.nextInt(11))
          }

          case giveMeasure(chords: List [Chord]) =>{
               player ! Measure(chords)
               Thread.sleep(1800)
               context.self ! StartGame()
          }

     }
}
