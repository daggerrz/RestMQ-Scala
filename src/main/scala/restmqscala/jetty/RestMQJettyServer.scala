package restmqscala

import unfiltered.request._
import unfiltered.response._



class RestMQPlan(implicit val broker: RestMQBroker) extends unfiltered.Plan {

  def filter = {
    case GET(Path(Seg(qn :: Nil), req)) =>
      broker.get(qn) match {
        case Some(res) => ResponseString(res)
        case None => Status(204)
      }

    case POST(Path(Seg(qn :: Nil), Bytes(bytes, req))) =>
        broker.add(qn, new String(bytes, "UTF-8"))
        Status(201)

  }
}

object RestMQJettyServer {
  def main(args: Array[String]) {
    implicit val broker = RedisRestMQBroker
    val plan = new RestMQPlan
    unfiltered.server.Http(8888)
            .context("/q") { _.filter(plan) }
    .run()
  }
}
