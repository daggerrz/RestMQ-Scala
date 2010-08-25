package restmqscala

import unfiltered.request._
import unfiltered.response._
import com.redis.Redis

object QueueName {
  def unapply(str: String) = Some(str)
}


class RestMQPlan(implicit val broker: RestMQBroker) extends unfiltered.Plan {

  def filter = {
    case GET(Path(Seg(QueueName(qn) :: Nil), req)) =>
      broker.get(qn) match {
        case Some(res) => ResponseString(res)
        case None => Status(204)
      }

    case POST(Path(Seg(QueueName(qn) :: Nil), Params(params, req))) =>
      params("value").headOption match {
        case Some(value) =>
          ResponseString(broker.add(qn, value))
        case None =>
          Status(400)
      }

  }
}

object RestMQServer {
  def main(args: Array[String]) {
    implicit val broker = RedisRestMQBroker
    val plan = new RestMQPlan
    unfiltered.server.Http(8888)
            .context("/q") { _.filter(plan) }
    .run()
  }
}
