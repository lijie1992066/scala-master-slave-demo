package cn.touna.domain

import akka.actor.ActorSelection

class WorkInfo(var worker: ActorSelection, var time: Long){
  
}