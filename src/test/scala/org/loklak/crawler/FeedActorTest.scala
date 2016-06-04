package org.loklak.crawler

import akka.actor.ActorSystem
import org.scalatest.{WordSpecLike, BeforeAndAfterAll, ShouldMatchers, FlatSpec}
import akka.testkit._
/**
  * Created by Scott on 6/4/16.
  */
class FeedActorTest extends TestKit(ActorSystem("MySpec")) with ImplicitSender
with WordSpecLike with ShouldMatchers with BeforeAndAfterAll  {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
  "An Feed actor" must {
    "send seed" in {
      val feedactor = TestActorRef[FeedActor]
      val search_result = TwitterSearchFunc("Trump")
      search_result.foreach(feedactor ! _)
      expectMsgType[String]
    }
  }
}
