package org.loklak.tools

import org.scalatest.{ShouldMatchers, FlatSpec}

/**
  * Created by Scott on 6/4/16.
  */
class ConfigObjectTest extends FlatSpec with ShouldMatchers {
  "A configuration object" should "be able to load elastic search configuration" in{
    ConfigObject.es_address.length shouldBe > (0)
    ConfigObject.es_port shouldBe > (0)
  }
}
