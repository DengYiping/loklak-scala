package org.loklak.data

import akka.actor._

/**
  * Created by Scott on 6/4/16.
  */
case object ClearESCount
class DataAccessActor extends Actor with ActorLogging{
  var count = 0
  def receive = {
    case (id:String,json:String) => DAO.store(id,json); count = count + 1
    case ClearESCount => count = 0
  }
}
