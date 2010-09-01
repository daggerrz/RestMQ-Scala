package unfiltered.netty.request

import org.jboss.netty.handler.codec.http.{HttpMethod, HttpRequest}

class Method(method: HttpMethod) {
  def unapply(req: HttpRequest) =
    if (req.getMethod == method) Some(req)
    else None
}

object GET extends Method(HttpMethod.GET)
object POST extends Method(HttpMethod.POST)
object PUT extends Method(HttpMethod.PUT)
object DELETE extends Method(HttpMethod.DELETE)
object HEAD extends Method(HttpMethod.HEAD)