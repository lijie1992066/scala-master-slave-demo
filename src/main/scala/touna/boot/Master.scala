package cn.touna.boot

import scala.collection.mutable.HashMap
import scala.concurrent.duration.DurationInt
import akka.actor.Actor
import akka.actor.Props
import cn.touna.caseclass._
import cn.touna.utils.ActorUtils
import cn.touna.domain.WorkInfo

class Master(host: String, port: Int, actSys: String, act: String) extends Actor {

  var workerConn = new HashMap[String, WorkInfo]
  var clientConn = new HashMap[String, WorkInfo]

  val OVERTIME = 20000

  override def preStart(): Unit = {
    import context.dispatcher
    //启动的时候定时检查worker是否挂了，如果挂了就从workerConn移除
    context.system.scheduler.schedule(0 millis, OVERTIME millis, self, CheckConn)
  }

  def receive: Actor.Receive = {
    case Register(id, host, port, actSys, act) => {

      println(s"akka.tcp://$actSys@$host:$port/user/$act")
      val actRef = context.actorSelection(s"akka.tcp://$actSys@$host:$port/user/$act")
      workerConn += (id -> new WorkInfo(actRef, 0))
      actRef ! Registered
    }

    case Heartbeat(workId) => {
      if (workerConn.contains(workId)) {
        val wi = workerConn.get(workId).get
        wi.time = System.currentTimeMillis()
        workerConn -= workId
        workerConn += (workId -> wi)
      }
    }

    case CheckConn => {
      val offLines = workerConn.filter(t => {
        System.currentTimeMillis() - t._2.time > OVERTIME
      })
      for (offL <- offLines.keySet) {
        workerConn -= offL
      }
      println("alive count is " + workerConn.size)
    }

    case Submit(str) => {
      workerConn.foreach( t => { t._2.worker ! GetWork(str) })
      println("master get submit")
    }

    case OK(workId) =>{
      println(s"workId:$workId exec OK")
    }

  }
}

object Master {
  def main(args: Array[String]): Unit = {
    val argss = Array[String]("127.0.0.1", "8181", "masterSystem", "actorMaster")

    val host = argss(0)

    val port = argss(1).toInt

    val actorSystem = argss(2)

    val actorName = argss(3)

    val actSys = ActorUtils.getActorSystem(host, port, actorSystem)

    val act = actSys.actorOf(Props(new Master(host, port, actorSystem, actorName)), actorName)

    actSys.awaitTermination()

  }
}