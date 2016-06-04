package org.loklak.data

/**
  * Created by Scott on 6/4/16.
  */
object DAO {
  private val es = ES.client
  def store(id:String, json:String) = {
    es.index("loklak","twitter",json,id)
  }
}
