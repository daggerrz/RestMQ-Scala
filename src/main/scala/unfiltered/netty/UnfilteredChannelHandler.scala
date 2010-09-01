package unfiltered.netty

import org.jboss.netty.channel._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.handler.codec.http.{HttpRequest, DefaultHttpResponse}
import unfiltered.netty.response.ResponsePackage.ResponseFunction

/**
 * ChannelHandler which responds via Unfiltered functions.
 * 
 */
abstract class UnfilteredChannelHandler extends SimpleChannelUpstreamHandler {

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {

    val request = e.getMessage().asInstanceOf[HttpRequest]

    val response = new DefaultHttpResponse(HTTP_1_1, OK)
    response.setHeader("Server", "Scala Netty Unfiltered Server")
    val ch = request.getHeader("Connection")
    val keepAlive = request.getProtocolVersion match {
      case HTTP_1_1 => !"close".equalsIgnoreCase(ch)
      case HTTP_1_0 => "Keep-Alive".equals(ch)
    }

    filter(request).apply(response)

    if (keepAlive) {
      response.setHeader("Connection", "Keep-Alive")
      response.setHeader("Content-Length", response.getContent().readableBytes());
    } else {
      response.setHeader("Connection", "close")
    }


    val future = e.getChannel.write(response)
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE)
    }

  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) {
  }


  def filter: PartialFunction[HttpRequest, ResponseFunction]

}