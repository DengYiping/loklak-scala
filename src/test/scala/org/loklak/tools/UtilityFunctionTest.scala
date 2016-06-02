package org.loklak.tools

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

/**
  * Created by Scott on 6/2/16.
  */
class UtilityFunctionTest extends FlatSpec with ShouldMatchers{
  "A Bloomfilter" should "return true if in the filter(already exists)" in{
    val filter = Bloomfilter.optimal_filter[String](2000, 0.001)
    filter contains "hello234"
    filter("hello234") shouldEqual(true)
  }
  "A url unshortener" should "unshort links" in{
    Unshorten("https://t.co/2EnG70T0wi").length should be > ("https://t.co/2EnG70T0wi".length)
  }
}
