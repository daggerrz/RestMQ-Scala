package unfiltered.netty.response

import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.util.CharsetUtil
import org.jboss.netty.buffer.{ChannelBuffers, ChannelBuffer}

trait ResponseWriter extends Responder {
  def respond(res: HttpResponse) {
    res.setContent(getBuffer)
  }

  def getBuffer(): ChannelBuffer
}
case class ResponseString(content: String) extends ResponseWriter {
  def getBuffer() = ChannelBuffers.copiedBuffer(content, CharsetUtil.UTF_8)

}

//case class Html(nodes: scala.xml.NodeSeq) extends ChainResponse(HtmlContent ~> ResponseString(nodes.toString))