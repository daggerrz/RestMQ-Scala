package restmqscala.netty

import restmqscala.{RestInterface, RedisRestMQBroker}

object RestMQNettyServer {
  def main(args: Array[String]) {
    val broker = RedisRestMQBroker
    new unfiltered.netty.Server(8888, unfiltered.netty.Planify(new RestInterface(broker).intent)).start
  }
}