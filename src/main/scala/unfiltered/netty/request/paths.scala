package unfiltered.netty.request

import org.jboss.netty.handler.codec.http.HttpRequest


object Path {
  def unapply(req: HttpRequest) = Some((req.getUri), req)
}

object Seg {
  def unapply(path: String): Option[List[String]] = path.split("/").toList match {
    case "" :: rest => Some(rest) // skip a leading slash
    case all => Some(all)
  }
}