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

case class getMeasure(result: Int)
case class measures(num: Int)

class Provider(db: ActorRef) extends Actor {

     var cpt = 0
     var tab1 = true

     var partie1 = Array.ofDim[Int] (11,8)
     partie1 = Array(Array(95, 21, 140, 40, 104, 121, 10, 29),
          Array(31, 5, 127, 62, 145, 45, 133, 80),
          Array(68, 94, 157, 12, 152, 54, 109, 23),
          Array(39, 16, 112, 84, 160, 1, 158, 99),
          Array(147, 73, 162, 44, 79, 96, 35, 106),
          Array(103, 156, 26, 166, 153, 67, 117, 90),
          Array(151, 59, 170, 52, 98, 132, 20, 126),
          Array(118, 83, 113, 49, 139, 85, 168, 93),
          Array(97, 141, 41, 155, 74, 128, 61, 122),
          Array(2, 86, 164, 60, 134, 46, 146, 32),
          Array(53, 129, 9, 102, 27, 36, 105, 4))

     var partie2 = Array.ofDim[Int] (11,8)
     partie2 = Array(Array(69, 120, 25, 8, 111, 48, 108, 13),
          Array(116, 38, 125, 55, 173, 17, 115, 82),
          Array(65, 138, 14, 131, 72, 57, 144, 78),
          Array(89, 175, 6, 33, 66, 159, 51, 169),
          Array(24, 142, 63, 124, 75, 135, 0, 92),
          Array(137, 70, 149, 28, 100, 161, 22, 150),
          Array(15, 154, 56, 174, 42, 167, 88, 171),
          Array(119, 87, 47, 165, 50, 114, 71, 110),
          Array(64, 76, 18, 81, 136, 37, 148, 7),
          Array(101, 3, 30, 163, 143, 58, 172, 77),
          Array(34, 19, 107, 91, 11, 123, 43, 130))

     var constructorAdrr: ActorRef = null
     def receive = {
          case getMeasure(result) => {
               constructorAdrr = sender
               if (tab1){
                    db ! GetMeasure(partie1(result)(cpt))

                    cpt += 1
                    if (cpt > 7){
                         tab1 = false
                         cpt = 0
                    }
               }
               else{
                    db ! GetMeasure(partie2(result)(cpt))
                    cpt += 1
                    if (cpt > 7){
                         tab1 = true
                         cpt = 0
                    }
               }
          }

          case Measure(chords) => {
               constructorAdrr ! giveMeasure(chords)
          }
     }
}
