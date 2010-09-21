package restmqscala.jetty

import restmqscala.{RestInterface, RedisRestMQBroker}

object RestMQJettyServer {
  def main(args: Array[String]) {
    implicit val broker = RedisRestMQBroker
    unfiltered.jetty.Http(8888).filter(unfiltered.filter.Planify(new RestInterface(broker).intent)).run

  }
}
