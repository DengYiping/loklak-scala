package org.loklak.crawler

import org.scalatest.{ShouldMatchers, FlatSpec}

/**
  * Created by Scott on 6/2/16.
  */
class TwitterSearchFuncTest extends FlatSpec with ShouldMatchers{
  "Twitter Crawler" should "be able to perform search on any topic" in{
    TwitterSearchFunc.grouped_search("loklak").flatten.length shouldBe > (0)
  }
}
