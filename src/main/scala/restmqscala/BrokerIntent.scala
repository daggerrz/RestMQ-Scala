package restmqscala

import unfiltered.response._
import unfiltered.request._

class RestInterface[T](val broker: RestMQBroker) {

  def intent[T]: unfiltered.Unfiltered.Intent[T] = {
    case GET(Path(Seg("q" :: qn :: Nil), req)) =>
      broker.get(qn) match {
        case Some(res) => ResponseString(res)
        case None => NoContent ~> ResponseString("No messages in queue " + qn)
      }

    case POST(Path(Seg("q" :: qn :: Nil), Bytes(content, req))) =>
      broker.add(qn, new String(content, "UTF-8"))
      Created

    case _ => NotFound
  }

}