package touna.boot

import java.util.UUID

import akka.actor.{Actor, ActorSelection, Props}
import akka.actor.Actor.Receive
import cn.touna.caseclass.{Register, Submit, SubmitSelf}
import cn.touna.utils.ActorUtils

/**
  * Created by Administrator on 2017/7/14.
  */
class Client(mhost: String, mport: Int, mactSys: String, mact: String) extends Actor {

  var master: ActorSelection = _

  override def preStart(): Unit = {
    master = context.actorSelection(s"akka.tcp://$mactSys@$mhost:$mport/user/$mact")
  }

  override def receive: Receive = {
    case SubmitSelf(pri) => {
      master ! Submit(pri)
    }
  }
}


object Client {
  def main(args: Array[String]): Unit = {
    val argss = Array[String]("127.0.0.1", "8732", "clientSystem", "clientMaster", "127.0.0.1", "8181", "masterSystem", "actorMaster")

    val host = argss(0)

    val port = argss(1).toInt

    val actorSystem = argss(2)

    val actorName = argss(3)

    val mhost = argss(4)

    val mport = argss(5).toInt

    val mactorSystem = argss(6)

    val mactorName = argss(7)

    val actSys = ActorUtils.getActorSystem(host, port, actorSystem)
    val act = actSys.actorOf(Props(new Client(mhost, mport, mactorSystem, mactorName)), actorName)

    act ! SubmitSelf("print someing")

    actSys.awaitTermination()
  }
}
