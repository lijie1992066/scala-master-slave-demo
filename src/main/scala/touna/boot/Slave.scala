package cn.touna.boot

import java.util.UUID

import akka.actor.{Actor, ActorSelection, Props}

import scala.concurrent.duration.DurationInt
import akka.actor.Actor.Receive
import cn.touna.caseclass._
import cn.touna.utils.ActorUtils

class Slave(host: String, port: Int, actSys: String, act: String, mhost: String, mport: Int, mactSys: String, mact: String) extends Actor {

  var master: ActorSelection = _
  var workId: String = _
  val HEARBEATTIME = 10000

  override def preStart(): Unit = {
    workId = UUID.randomUUID().toString
    master = context.actorSelection(s"akka.tcp://$mactSys@$mhost:$mport/user/$mact")
    master ! Register(workId, host, port, actSys, act)
  }

  override def receive: Receive = {
    case Registered => {
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, HEARBEATTIME millis, self, SendHeartbeat)
    }

    case SendHeartbeat => {
      master ! Heartbeat(workId)
    }

    case GetWork(str) => {
      println(s"workId:$workId and println: $str")
      Thread.sleep(2000)
      master ! OK(workId)
    }
  }
}

object Slave {
  def main(args: Array[String]): Unit = {
    val argss = Array[String]("127.0.0.1", "8651", "workSystem", "workMaster", "127.0.0.1", "8181", "masterSystem", "actorMaster")

    val host = argss(0)

    val port = argss(1).toInt

    val actorSystem = argss(2)

    val actorName = argss(3)

    val mhost = argss(4)

    val mport = argss(5).toInt

    val mactorSystem = argss(6)

    val mactorName = argss(7)

    val actSys = ActorUtils.getActorSystem(host, port, actorSystem)
    val act = actSys.actorOf(Props(new Slave(host, port, actorSystem, actorName, mhost, mport, mactorSystem, mactorName)), actorName)
    actSys.awaitTermination()
  }
}