package org.loklak.crawler

import akka.actor.{Actor, ActorLogging}
import org.loklak.crawler.tweets.{TweetRaw, TweetComp}
import org.loklak.tools.{Bloomfilter, ConfigObject}

/**
  * Created by Scott on 6/4/16.
  */
trait Feed{
  def filter:String => Boolean
}
class FeedActor extends Actor with ActorLogging with Feed{
  private val filter_size = ConfigObject.conf.getInt("bloomfilter.size")
  private val hash_level = ConfigObject.conf.getInt("bloomfilter.hash_level")
  val filter = Bloomfilter[String](filter_size,hash_level)
  def receive = {
    case x:List[TweetComp] => {
      val contents = x
        .withFilter(_.isInstanceOf[TweetRaw])
        .map(_.asInstanceOf[TweetRaw])
        .foreach{
          raw => {
            raw.hashtags.filterNot(filter).foreach(sender() ! _)
            raw.mentions.map(_._3).filterNot(filter).foreach(sender() ! _)
          }
        }
    }
  }
}
