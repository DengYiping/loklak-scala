package org.loklak.crawler.tweets

import scala.util.matching.Regex
/**
  * Created by Scott on 12/28/15.
  */
case class MultilineException(e:String) extends Throwable
abstract class TweetComp {
  def key:String
  def value:Any
  override def toString:String
}

trait LineFeature{
  def pattern:Regex
  def extractor(raw:String):TweetComp
}

trait Downloadable{
  def url:String
}

trait SeedFeature

class UserInfo(val value:(String,String,Long)) extends TweetComp{
  override val key = "userinfo"
  override def toString = value.toString()
  def nickname = value._1
  def realname = value._2
  def id = value._3
}

object UserInfo extends LineFeature{
  override val pattern = """data-screen-name="(.*)" data-name="(.*)" data-user-id="(\d+)"""".r

  def main(args: Array[String]): Unit ={
    val test = "data-screen-name=\"WhtlyLffn\" data-name=\"Whitley Laffin\" data-user-id=\"3278647608\""
    val info = this.extractor(test)
    println(info)
  }

  override def extractor(raw:String) ={
    val pattern(nickname,realname,userid) = raw
    new UserInfo((nickname,realname,userid.toLong))
  }
}

class UserAvatar(val value:String) extends TweetComp with Downloadable
{
  override val key = "useravatarurl"
  override def toString = value
  override def url = value
}
object UserAvatar extends LineFeature{
  override val pattern = """<img class="avatar js-action-profile-avatar" src="(http.*)" alt="">""".r

  def main(args: Array[String]): Unit ={
    val test = "<img class=\"avatar js-action-profile-avatar\" src=\"https://pbs.twimg.com/profile_images/607907337161068544/LR9xxa_B_bigger.jpg\" alt=\"\">"
    val info = this.extractor(test)
    println(info)
  }

  override def extractor(raw:String) = {
    val pattern(url) = raw
    new UserAvatar(url)
  }
}


class TweetTime(val value:Long, val tweet_link:String) extends TweetComp
{
  override val key = "time"
  override def toString = value.toString + "\n" + tweet_link
}
object TweetTime extends LineFeature{
  override val pattern = """<a href="(.*?)".*tweet-timestamp.*data-time="(\d+).*""".r

  def main(args: Array[String]): Unit ={
    val test = "<a href=\"/akbarezzati/status/681394229441114112\" class=\"tweet-timestamp js-permalink js-nav js-tooltip\" title=\"12:40 AM - 28 Dec 2015\" ><span class=\"_timestamp js-short-timestamp js-relative-timestamp\"  data-time=\"1451292018\" data-time-ms=\"1451292018000\" data-long-form=\"true\" aria-hidden=\"true\">40s</span><span class=\"u-hiddenVisually\" data-aria-label-part=\"last\">40 seconds ago</span></a>"
    val info = this.extractor(test)
    println(info)
  }

  override def extractor(raw:String) = {
    val pattern(link,data_time) = raw
    val long_link = "https://twitter.com" + link
    new TweetTime(data_time.toLong, long_link)
  }
}

class TweetImage(override val value:String) extends TweetComp with Downloadable{
  override val key = "img"
  override def toString = value
  override def url = value
}

object TweetImage extends LineFeature{
  override val pattern = """data-image-url="(http.*)"""".r

  def main(args: Array[String]): Unit ={
    val test = "data-image-url=\"https://pbs.twimg.com/media/CXTMLvQWQAEAZyG.jpg\""
    val info = this.extractor(test)
    println(info)
  }

  override def extractor(raw:String) = {
    val pattern(url) = raw
    new TweetImage(url)
  }
}

class TweetGIF(override val value:String) extends TweetComp with Downloadable{
  override val key = "GIF"
  override def toString = value
  override def url = value
}
object TweetGIF extends LineFeature{
  override val pattern = """.*class="animated-gif".*poster="(http.*)".*""".r

  def main(args: Array[String]): Unit ={
    val test = "<video name=\"media\" class=\"animated-gif\" data-media-id=\"681393847906078720\" data-height=\"\" data-width=\"\" poster=\"https://pbs.twimg.com/tweet_video_thumb/CXTMGDCUEAAyTbB.png\" loop>"
    val info = this.extractor(test)
    println(info)
  }

  override def extractor(raw:String) ={
    val pattern(url) = raw
    new TweetGIF(url)
  }
}

class TweetRaw(override val value:String,
               val language:String,
               val hashtags:List[String],
               val mentions:List[(String,Long,String)],
               val emojis: List[(String,String,String)],
               val urls:List[(String,String)],
               val tweet_urls:List[String]) extends TweetComp{

  override lazy val toString = {
    var temp = value
    hashtags.foreach(tag => temp = temp.replaceFirst(TweetRaw.hashtag_pttrn.toString, "#" + tag + " "))
    mentions.foreach(tag => temp = temp.replaceFirst(TweetRaw.mention_pttrn.toString, "@" + tag._3 + " "))
    emojis.foreach(tag => temp = temp.replaceFirst(TweetRaw.emoji_pttrn.toString, "Emoji:" + tag._3 + " "))
    urls.foreach(tag => temp = temp.replaceFirst(TweetRaw.url_pttrn.toString, "Short_url:" + tag._1 + " "))
    tweet_urls.foreach(tag => temp = temp.replaceFirst(TweetRaw.tweet_url_pttrn.toString, "Tweet:" + tag + " "))
    temp
  }
  override val key = "main"
}
object TweetRaw extends LineFeature{
  override val pattern = """<p class="TweetTextSize  js-tweet-text tweet-text"(.*)""".r //signature of twitter text

  val mention_pttrn = """<a href="(.*?)".*?data-mentioned-user-id="(\d+?)".*?<s>@</s><b>(.*?)</b></a>""".r
  val hashtag_pttrn = """<a href="/hashtag/.*?<s>#</s><b>(.*?)</b></a>""".r
  val emoji_pttrn = """<img class="Emoji.*?src="(http.*?)".*?alt="(.*?)".*?title="(.*?)".*?">""".r
  val url_pttrn  = """<a href="(https://t.co/.*?)".*?data-expanded-url="(.*?).*?</a>""".r
  val tweet_url_pttrn = """<a href="(.*?)".*?data-pre-embedded.*?</a>""".r

  def main(args: Array[String]): Unit ={
    val test = "<p class=\"TweetTextSize  js-tweet-text tweet-text\" lang=\"en\" data-aria-label-part=\"0\"><a href=\"/MagicBBallTix\" class=\"twitter-atreply pretty-link js-nav\" dir=\"ltr\" data-mentioned-user-id=\"3191722551\" ><s>@</s><b>MagicBBallTix</b></a> yes.  He asked for a basketball ball for <strong>christmas</strong> and has not put it down.  Said he wants to <a href=\"/hashtag/playinmiddleschool?src=hash\" data-query-source=\"hashtag_click\" class=\"twitter-hashtag pretty-link js-nav\" dir=\"ltr\" ><s>#</s><b>playinmiddleschool</b></a>  <a href=\"/hashtag/happymom?src=hash\" data-query-source=\"hashtag_click\" class=\"twitter-hashtag pretty-link js-nav\" dir=\"ltr\" ><s>#</s><b>happymom</b></a></p>"
    val info = this.extractor(test)
    println(info)
  }

  override def extractor(raw:String) = {
    val extract_pttrn = """<p class="TweetTextSize  js-tweet-text tweet-text" lang="(.*)" data-aria-label-part="\d">(.*)</p>""".r //extracting text
    try{
      val extract_pttrn(lang,raw_text) = raw
      val text = raw_text
        .replaceAllLiterally("&#39;","\'")
        .replaceAllLiterally("<strong>","")
        .replaceAllLiterally("&quot;","\"")
        .replaceAllLiterally("</strong>","")
        .replaceAllLiterally("&amp;","&")
        .replaceAllLiterally("&frasl;","/")
        .replaceAllLiterally("&lt;","<")
        .replaceAllLiterally("&gt;",">")

      val hashtags = hashtag_pttrn.findAllIn(text).map{
        plain =>{
          val hashtag_pttrn(x) = plain
          x
        }
      }.toList

      val mentions = mention_pttrn.findAllIn(text).map{
        plain =>{
          val mention_pttrn(url,id,name) = plain
          (url,id.toLong,name)
        }
      }.toList

      val emojis = emoji_pttrn.findAllIn(text).map{
        plain =>{
          val emoji_pttrn(url,code,name) = plain
          (url,code,name)
        }
      }.toList

      val urls = url_pttrn.findAllIn(text).map{
        plain =>{
          val url_pttrn(short_url,full_url) = plain
          (short_url,full_url)
        }
      }.toList

      val tweet_urls:List[String] = tweet_url_pttrn.findAllIn(text).map{
        plain =>{
          val tweet_url_pttrn(short_url) = plain
          if(short_url.indexOf("http") == -1)
            "https://twitter.com" + short_url
          else
            short_url
        }
      }.toList

      new TweetRaw(text,lang,hashtags,mentions, emojis, urls, tweet_urls)
    } catch {
      case _:Throwable => throw new MultilineException("go to the next line")
    }
  }
}


class TweetVideo(override val value:String) extends TweetComp{
  override val key = "video"
  override val toString = value
}
object TweetVideo extends LineFeature{
  override val pattern = "<source video-src".r
  override def extractor(raw:String) = {
    new TweetVideo(raw)
  }
}

class TweetPlace(override val value:String) extends TweetComp{
  override val key = "place"
  override val toString = value
}
object TweetPlace extends LineFeature{
  override val pattern = """<span class="Tweet-geo.*?title="(.*)">""".r
  override def extractor(raw:String) =  {
    val pattern(place) = raw
    new TweetPlace(place)
  }
}

class TweetPlaceID(override val value:String) extends TweetComp{
  override val key="placeid"
  override val toString = value
}
object TweetPlaceID extends LineFeature{
  override val pattern = """<a class="ProfileTweet-actionButton u-linkClean js-nav js-geo-pivot-link" href=.*?data-place-id="(.*?)">""".r
  override def extractor(raw:String) = {
    val pattern(place_id) = raw
    new TweetPlaceID(place_id)
  }
}


