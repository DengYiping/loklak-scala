package org.loklak.requests

/**
  * Created by Scott on 12/27/15.
  */

case class HttpException(msg: String) extends Throwable

/**
  * for making Http Request and download files
  */
object HttpReq {
  import scalaj.http._

  /**
    * make request and return a string of body
    * @param url:String
    * @return body:String
    * @throws HttpException(msg:String)
    */
  def get(url:String):String = get(url,Nil)

  /**
    * pass in a URL address and download into a Byte array.
    * @param url:String
    * @return Array[Byte]
    */
  def download(url:String):Array[Byte] = {
    val request = Http(url)
    request.header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
    val response = try{
      request.asBytes
    } catch {
      case _:Throwable => throw new HttpException("error in get data")
    }
    if(response.isSuccess)
      response.body
    else
      throw new HttpException("Error in get data, maybe it is a bad URL")
  }

  /**
    *
    * @param url url address
    * @param cookies:List of cookie pairs: (String,String)
    * @return
    */
  def get(url:String, cookies: List[(String,String)]): String ={
    val request = Http(url)
    cookies.foreach((tuple_string) => request.cookie(tuple_string._1, tuple_string._2)) //insert cookies
    request.header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
    val response = try{
      request.asString
    } catch {
      case _:Throwable => throw new HttpException("error in get data and translate to String, maybe it's bad connection")
    }

    val reg_200 = """.*(200).*""".r
    if(response.isSuccess && reg_200.findFirstIn(response.statusLine).isDefined){
      try{
        response.body
      }catch{
        case throwable: Throwable => throw new HttpException("fail to get the content")
      }
    }
    else{
      throw new HttpException("bad request" + response.statusLine)
    }
  }

  def get_redirect(url:String):String = {
    val request = Http(url)
    request.header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
    val response = try{
      request.asString
    } catch {
      case _:Throwable => throw new HttpException("cannot get redirection")
    }
    if(response.isCodeInRange(301,301)){
      response.header("location") match {
        case Some(x) => x
        case None => throw new HttpException("return 301, however, there is no redirection")
      }
    }
    else if(response.isCodeInRange(200,200))
      url
    else
      throw new HttpException("code error: it's not a redirection")
  }
  def main(args: Array[String]): Unit ={
    try{
      //println(HttpReq.get("https://twitter.com/search?f=tweets&vertical=default&q=kaffee&src=typd"))
      println(get_redirect("https://t.co/2EnG70T0wi"))
    }
    catch{
      case HttpException(msg:String) => println(msg)
    }

  }
}//end of singleton object

