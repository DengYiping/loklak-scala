package org.loklak.crawler
/**
  * Helper class and main implemention for a crawler.
  * Created by Scott on 12/27/15.
  *
  */
import _root_.org.loklak.requests._
import _root_.org.loklak.crawler.tweets._

import scala.collection.mutable.ListBuffer

case class OnlineSearchException(e:String) extends Throwable
object TwitterSearchFunc extends (String => List[List[TweetComp]]) {
  import org.loklak.crawler.tweets._

  /**
    * given a key word, return its correct format url to perform search
    * @param key_word a key_word
    * @return url
    */
  private def contructSearchURL(key_word:String):String = {
    val legal_word = key_word.replace('+',' ').replace(',',' ').replace('"',' ').replaceAllLiterally(" ","%20")
    "https://twitter.com/search?f=tweets&vertical=defaul&q=" + legal_word + "&src=typd"
  }

  /**
    * given a line, parse it to a correct Tweet Composite
    * It is designed in a special pattern for ease of change, you only have to change TweetComp.
    * @param raw a line
    * @return a TweetComp or null
    */
  private def parseLine(raw:String):TweetComp = {
    raw match{
      case UserInfo.pattern(_*) => UserInfo.extractor(raw)
      case UserAvatar.pattern(_*) => UserAvatar.extractor(raw)
      case TweetTime.pattern(_*) => TweetTime.extractor(raw)
      case TweetImage.pattern(_*) => TweetImage.extractor(raw)
      case TweetGIF.pattern(_*) => TweetGIF.extractor(raw)
      case TweetRaw.pattern(_*) => try { TweetRaw.extractor(raw) } catch { case _:Throwable => null}
      case TweetPlace.pattern(_*) => TweetPlace.extractor(raw)
      case TweetPlaceID.pattern(_*) => TweetPlaceID.extractor(raw)
      case _ => null
    }
  }

  /**
    * high level function:
    * Given a topic, return a list of Tweets
    * @param topic the key words to search
    * @return a raw TweetComp
    * @throws OnlineSearchException(e:String)
    */
  private def search(topic:String):List[TweetComp] = {
    val raw:String = try{
      HttpReq.get(contructSearchURL(topic))
    }catch{
      case HttpException(e) => throw new OnlineSearchException(e)
      case _:Throwable => throw new OnlineSearchException("unknown error")
    }
    val lines = raw.lines.withFilter(_.length > 15).map(_.trim).toList

    for{
      line <-lines
      trans = parseLine(line)
      if trans != null
    }yield trans
  }

  /**
    * group tweets together
    * @param raw Put the element of single tweet together in a List
    * @return List of List of TweetComp
    */
  private def group_tweets(raw:List[TweetComp]):List[List[TweetComp]] = {
    val reversed = raw.reverse //over reversed, we will reverse it back later
    var temp = new ListBuffer[TweetComp]
    val two_leveled = new ListBuffer[List[TweetComp]]
    reversed.foreach{
      tweetcomp =>{
        if(tweetcomp.isInstanceOf[UserInfo]){
          temp prepend tweetcomp
          val one_level = temp.toList
          two_leveled prepend one_level //use prepend to reverse the order again
          temp = new ListBuffer[TweetComp]
        }
        else{
          temp prepend tweetcomp//use prepend to reverse the order again
        }
      }
    }//end of foreach
    two_leveled.toList
  }
  def grouped_search(topic:String) = group_tweets(search(topic))
  def apply(topic:String) = group_tweets(search(topic))
  def main(args: Array[String]): Unit ={

    val tweets = try{this.search("Trump")}
    catch{
      case OnlineSearchException(e) => print(e); Nil
    }
    val grouped_tweets = group_tweets(tweets)
    grouped_tweets.foreach{
      group =>{
        println(group mkString "\n")
        println("--------------------------------")
        println()
      }
    }//end of foreach
    println("total:" + grouped_tweets.size)

  }
}
