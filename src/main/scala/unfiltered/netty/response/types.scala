package unfiltered.netty.response

import org.jboss.netty.handler.codec.http.HttpResponse

case class ContentType(content_type: String) extends Responder {
  def respond(res: HttpResponse) {
    res.setHeader("Content-type", "%s; charset=%s".format(content_type, charset))
  }
  def charset = "utf-8"
}
object CssContent extends ContentType("text/css")
object HtmlContent extends ContentType("text/html")
object JsContent extends ContentType("text/javascript")
object CsvContent extends ContentType("text/csv")
object TextXmlContent extends ContentType("text/xml")
object PlainTextContent extends ContentType("text/plain")
object JsonContent extends ContentType("application/json")
object ApplicationXmlContent extends ContentType("application/xml")