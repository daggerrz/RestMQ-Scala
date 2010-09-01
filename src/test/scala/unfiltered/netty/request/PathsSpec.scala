package unfiltered.netty.request

import org.specs.Specification
import org.jboss.netty.handler.codec.http._
import unfiltered.netty.request.{Path => P}
class PathsSpec extends Specification {

  val url = "/seg1/seg2"
  val req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, url)

  "Path" should {
    "extract url and req" in {
      (req match {
        case P(url, req) => true
      }) must beTrue 
    }
  }

  "Seg" should {
    "skip leading slash and split rest" in {
      Seg.unapply(url) must_== Some(List("seg1", "seg2"))
      (url match {
        case Seg("seg1" :: "seg2" :: Nil) => true
        case _ => false
      }) must beTrue
    }
  }

}