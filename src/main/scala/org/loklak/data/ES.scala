package org.loklak.data

/**
  * Created by Scott on 6/4/16.
  */
import org.elasticsearch.common.settings.Settings
/**
  * Created by Scott on 1/13/16.
  */


import java.net.InetAddress

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import scala.collection.JavaConversions._


object ES{
  //factory method for ESclient
  def client: ES = {
    import org.loklak.tools.ConfigObject.{es_address,es_port}
    val settings = Settings.settingsBuilder()
      .put("cluster.name", "elasticsearch")
      .put("client.transport.sniff", true)
      .build()

    //return
    val c = TransportClient.builder().settings(settings).build()
      .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(es_address),es_port))
    new ES(c)
  }
}

class ES(private val client:Client){
  final def close() = client.close()
  def append(indice:String, typo:String, json:String) = client
    .prepareIndex(indice,typo)
    .setSource(json)
    .execute
  def index(indice:String,typo:String, json:String, id:String) = client
    .prepareIndex(indice,typo)
    .setId(id)
    .setSource(json)
    .execute()
  def delele(indice:String, typo:String, id:String) = client.prepareDelete(indice,typo,id).execute()
  def get(indice:String, typo:String, id:String) = client.prepareGet(indice, typo,id).execute()
  def cleanIndex():Unit = {
    val response = client.admin.cluster.prepareState.execute.actionGet()
    val indices = response.getState.getMetaData.getConcreteAllIndices
    indices.foreach(indice => client.admin().indices.prepareDelete(indice).execute().actionGet() )
  }
  def getClient = client
}
