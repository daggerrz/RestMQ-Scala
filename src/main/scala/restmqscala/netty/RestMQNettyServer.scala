package restmqscala.netty

import java.util.concurrent.Executors
import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import java.net.InetSocketAddress
import org.jboss.netty.handler.codec.http.{HttpRequestDecoder, HttpResponseEncoder}
import org.jboss.netty.channel._
import unfiltered.netty.UnfilteredChannelHandler
import org.jboss.netty.util.CharsetUtil

class RestMQNettyServer(val port: Int, lastHandler: ChannelHandler) {
  val DEFAULT_IO_THREADS = Runtime.getRuntime().availableProcessors() + 1;
	val DEFAULT_EVENT_THREADS = DEFAULT_IO_THREADS * 4;

  def start(): Unit = {

    val bootstrap = new ServerBootstrap(
      new NioServerSocketChannelFactory(
        Executors.newFixedThreadPool(DEFAULT_IO_THREADS),
        Executors.newFixedThreadPool(DEFAULT_EVENT_THREADS)))

    bootstrap.setPipelineFactory(new ServerPipelineFactory(lastHandler))

   	bootstrap.setOption("child.tcpNoDelay", true)
		bootstrap.setOption("child.keepAlive", true)
		bootstrap.setOption("receiveBufferSize", 128 * 1024)
		bootstrap.setOption("sendBufferSize", 128 * 1024)
		bootstrap.setOption("reuseAddress", true)
		bootstrap.setOption("backlog", 16384)
    bootstrap.bind(new InetSocketAddress(port))

  }
}

class ServerPipelineFactory(val lastHandler: ChannelHandler) extends ChannelPipelineFactory {

  def getPipeline(): ChannelPipeline = {
    val line = Channels.pipeline

    line.addLast("decoder", new HttpRequestDecoder)
    line.addLast("encoder", new HttpResponseEncoder)
    line.addLast("handler", lastHandler)
    
    line
  }
}

import restmqscala.RestMQBroker

class RestMQNettyFilter(val broker: RestMQBroker) extends UnfilteredChannelHandler {
  import unfiltered.netty.request._
  import unfiltered.netty.response._

  def filter = _ match {
    case GET(Path(Seg("q" :: qn :: Nil), req)) =>
      broker.get(qn) match {
        case Some(res) => ResponseString(res)
        case None => NoContent andThen ResponseString("No messages in queue " + qn)
      }

    case POST(Path(Seg("q" :: qn :: Nil), req)) =>
      val content = req.getContent
      broker.add(qn, content.toString(CharsetUtil.UTF_8))
      Created

    case _ => NotFound
  }
}

import restmqscala.RedisRestMQBroker

object RestMQNettyServer {
  def main(args: Array[String]) {
    val broker = RedisRestMQBroker
    new RestMQNettyServer(8888, new RestMQNettyFilter(broker)).start
  }
}