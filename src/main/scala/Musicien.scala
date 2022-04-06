package upmc.akka.leader

import akka.actor._

import scala.collection.mutable
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

case class Start ()
case class ReInitAliveList()
case class AliveActor(actorsIds: List[Int])
case class DeadActor(actorId: Int)
case class SetNewConductor(newConductorId: Int)
case class ComeOnMaestro()
case class TimeOutChecker()

class Musicien (val id:Int, val terminaux:List[Terminal]) extends Actor {

     // Les differents acteurs du sous-systeme
     val displayActor: ActorRef = context.actorOf(Props[DisplayActor], name = "displayActor")
     val heartActor: ActorRef = context.actorOf(Props(new HeartActor(id, terminaux)), name = "heartActor")
     val stethoscopeActor: ActorRef = context.actorOf(Props(new StethoscopeActor(id)), name = "stethoscopeActor")
     val conductorChooserActor: ActorRef = context.actorOf(Props(new ConductorChooserActor(id, terminaux)), name = "conductorChooserActor")
     val conductorChoiceAddresseeActor: ActorRef = context.actorOf(Props(new ConductorChoiceAddresseeActor()), name = "conductorChoiceAddresseeActor")

     val db: ActorRef = context.actorOf(Props(new DataBaseActor()), "DataBaseActor")
     val player: ActorRef = context.actorOf(Props(new PlayerActor()),"PlayerActor")
     val provider: ActorRef = context.actorOf(Props(new Provider(db)),"Provider")
     val conductor: ActorRef = context.actorOf(Props(new Conductor(provider, terminaux, id)),"Conductor")
     val conductorListenerActor: ActorRef = context.actorOf(Props(new ConductorListenerActor(id, player)),"conductorListenerActor")

     var aliveList: mutable.HashMap[Int, Boolean] = new mutable.HashMap() // Updated list of all alive actors
     var conductorId: Int = -1 // Conductor id, if -1 not init.

     val MUSIC_TIME: FiniteDuration = 1800 milliseconds
     val TIMEOUT_TIME: FiniteDuration = 20 seconds
     val scheduler: Scheduler = context.system.scheduler

     // Scheduler
     scheduler.scheduleOnce(TIMEOUT_TIME, self, TimeOutChecker())

     def receive: Receive = {


          // Initialisation
          case Start => {
               terminaux.foreach(x=> aliveList.put(x.id, false)) // Set hashmap
               aliveList.update(id, true)
               displayActor ! Message ("Musicien " + this.id + " is created")
               heartActor ! HeartBeat()
          }

          case ReInitAliveList => {
               aliveList.foreach(x => aliveList.put(x._1, false)) // Re set all values on false
          }

          case AliveActor (actorsIds) =>{
               actorsIds.foreach(actorId => aliveList.update(actorId, true))

               if (!aliveList.getOrElse(conductorId, false)){
                    conductorChooserActor ! ChooseNewConductor(aliveList)
               }
          }

          case SetNewConductor(newConductorId) =>{
               conductorId = newConductorId
               println("Le nvx chef d'orcheste est " + conductorId)

               if (conductorId == id){
                    println(id + " Prend les manettes !")
                    conductor ! StartGame(aliveList)
               }
          }

          case ComeOnMaestro() =>{
               if (conductorId == id){
                    scheduler.scheduleOnce(MUSIC_TIME, conductor, StartGame(aliveList))
               }
          }

          case TimeOutChecker() => {
               var truelyAlive: List[Int] = List()

               aliveList.iterator.filter(x => x._2).foreach(x => truelyAlive = truelyAlive ::: List(x._1))

               if (truelyAlive.size <= 1){
                    println("Nobody love me :(")
                    self ! PoisonPill
               }
               else{
                    scheduler.scheduleOnce(TIMEOUT_TIME, self, TimeOutChecker())
               }
          }
     }
}
