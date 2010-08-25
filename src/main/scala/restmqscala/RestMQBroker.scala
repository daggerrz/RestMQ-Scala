package restmqscala

import com.redis.Redis
import java.util.concurrent.LinkedBlockingQueue

abstract trait RestMQBroker {
  def add(queue: String, value: String) : String
  def get(queue: String) : Option[String]
  def queueLen(queue: String) : Int
  def listQueues() : Set[String]
}

object RedisRestMQBroker extends RestMQBroker {

  // And how do we get this thread-safe?
  val redis = new Redis("localhost", 6379)

  val QS_STARTED = "1"
  val QS_STOPPED = "0"

  protected object Names  {

    val QUEUE_SET = "RMQ_QUEUES"
    val QUEUE_STATUS = "RMQ_QUEUE_STATUS"

    def queueCounter(q: String) = q + ":UUID"
    def messageKey(q: String, uuid: String) = q + ":" + uuid
    def messageList(q: String) = q + ":queue"
    def status(q: String) = QUEUE_STATUS + ":" + q
  }

  /**
   * Adds a value to the specified queue and creates the queue
   * if it does not already exist.
   */
  def add(queue: String, value: String): String = {
    // Get the next message id
    val uuid = redis.incr(Names.queueCounter(queue)).get

    // Get the key for the message and set it
    val key = Names.messageKey(queue, uuid.toString)
    redis.set(key, value)

    // Get the message list key for the queue
    val queueList = Names.messageList(queue)

    // Check if the queue exists and add it if not
    if (!redis.setMemberExists(Names.QUEUE_SET, queueList))
      configureNewQueue(queue, queueList)

    // Push the message key to the end of the queue's message list
    redis.pushTail(queueList, key)
    key
  }

  protected def configureNewQueue(queue: String, lkey: String) {
    if (redis.setAdd(Names.QUEUE_SET, queue)) {
      val ckey = Names.status(queue)
      redis.set(ckey, QS_STARTED)
    }    
  }

  def get(queue: String) : Option[String] = {
    redis.popHead(Names.messageList(queue)) match {
      case Some(key) =>
        val res = redis.get(key)
        redis.delete(key)
        res
      case None => None
    }
  }

  /**
   * Returns the set of existing queues.
   */
  def listQueues() : Set[String] = {
    redis.setMembers(Names.QUEUE_SET).getOrElse(Set())
  }

  /**
   * Returns the length of the specified queue or 0 if the queue
   * does not exist.
   */
  def queueLen(queue: String) = redis.listLength(Names.messageList(queue)).getOrElse(0)

  def purge: Unit = {
    listQueues.foreach { qn =>
      redis.delete(Names.queueCounter(qn))
      redis.delete(Names.messageKey(qn, "*"))
      redis.delete(Names.status(qn))
      redis.delete(Names.messageList(qn))
    }
    redis.delete(Names.QUEUE_SET)
  }
}