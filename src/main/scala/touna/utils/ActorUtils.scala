package cn.touna.utils

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor

object ActorUtils {

  //获取actor工具类
  def getActorSystem(host: String, port: Int, actorSystem: String) = {
    val conf = s"""
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.netty.tcp.hostname = "$host"
      |akka.remote.netty.tcp.port = "$port"
      """.stripMargin

    val config = ConfigFactory.parseString(conf)

    //创建注册worker的的ActorSystem
    val actorSys = ActorSystem(actorSystem, config)

    //返回actorSystem
    actorSys
  }

}