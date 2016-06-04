package org.loklak.tools

/**
  * Created by Scott on 6/4/16.
  */
import com.typesafe.config.ConfigFactory
object ConfigObject {
  val conf = ConfigFactory.load("./application.conf")
  val es_address = conf.getString("es.address")
  val es_port = conf.getInt("es.port")
  def main(args:Array[String]):Unit ={
    println(es_address)
  }
}