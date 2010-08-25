package restmqscala

import org.specs._
import restmqscala.{RedisRestMQBroker => b}

class RestMQBrokerSpec extends Specification  {

  val QN = "testqueue"
  b.purge

  "Broker" should {
    "be empty on first run" in {
      b.listQueues must_== Set()
    }
    "create queues on first add" in {
      b.add(QN, "foo")
      b.listQueues must_== Set(QN)
    }
    "return the correct queue size" in {
      b.queueLen(QN) must_== 0
      b.add(QN, "foo")
      b.queueLen(QN) must_== 1
    }
    "return None when the queue is empty" in {
      b.get(QN) must_== None
    }
    "return the correct element on get" in {
      b.add(QN, "foo")
      b.get(QN) must_== Some("foo")
    }
    "delete the element on get" in {
      b.add(QN, "foo")
      b.get(QN) must_== Some("foo")
      b.queueLen(QN) must_== 0
    }
  }

}
