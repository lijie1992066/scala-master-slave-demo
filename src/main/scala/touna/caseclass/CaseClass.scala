package cn.touna.caseclass

//M 2 M
case object CheckConn

//W 2 M
case class Register(workId: String, host: String, port: Int, actSys: String, act: String)

case class Heartbeat(workId: String)

case class OK(workId: String)

//M 2 W
case object Registered

case class GetWork(str: String)


//W 2 W
case object SendHeartbeat


//C 2 M
case class Submit(str: String)

//C 2 C
case class SubmitSelf(pri: String)