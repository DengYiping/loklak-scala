package org.loklak.requests
/**
  * Created by Scott on 6/2/16.
  */
import org.scalatest._
class HttpRequestTest extends FlatSpec with ShouldMatchers {
  "A HttpRequester" should "be able to open Baidu" in{
    HttpReq.get("http://www.baidu.com").length shouldBe > (0)
  }
  it should "be able to open twitter" in{
    HttpReq.get("https://twitter.com/ACTWorkforce")
  }
  "A redirect fetcher" should "be able to get redirection" in{
    val full_url = HttpReq.get_redirect("https://t.co/2EnG70T0wi")
    full_url.length shouldBe  > (0)
  }
}
