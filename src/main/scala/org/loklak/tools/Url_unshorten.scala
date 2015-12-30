package org.loklak.tools

/**
  * Created by Scott on 12/29/15.
  */

object Unshorten extends (String=>String){
  import org.loklak.requests.{HttpReq,HttpException}
  private val Shortener_list =
    "bbc.in"::
    "fb.me"::
    "wp.me"::
    "j.mp"::
    "t.co"::
    "bit.ly"::
    "ift.tt"::
    "goo.gl"::
    "tinyurl.com"::
    "ow.ly"::
    "tiny.cc"::
    "bit.do"::
    "amzn.to"::
    "tmblr.co"::
    "tumblr.com"::
    "www.tumblr.com"::
    Nil

  private val untested = List(
    "is.gd",
    "ta.gd",
    "cli.gs",
    "sURL.co.uk",
    "y.ahoo.it",
    "yi.tl",
    "su.pr",
    "Fwd4.Me",
    "budurl.com",
    "snipurl.com",
    "igg.me",
    "twiza.ru"
  )

  def apply(short_url:String):String ={
    val isEnlisted = (url:String) => Shortener_list.exists(enlisted => url.indexOf(enlisted) != -1)
    if(!isEnlisted(short_url))
      short_url
    else {
      try {
        val result = HttpReq.get_redirect(short_url)
        if (isEnlisted(result)) {

          var temp = result
          val max_loop = 5 //don't get into a forever loop
          var i = 1
          do {
            i += 1
            temp = HttpReq.get_redirect(temp)
          } while (isEnlisted(temp) && i < max_loop)
          temp
        }
        else result
      } catch {
        case HttpException(e) => short_url
      }
    }
  }

  def main(args: Array[String]): Unit ={
    println(Unshorten("https://t.co/2EnG70T0wi"))
  }
}
