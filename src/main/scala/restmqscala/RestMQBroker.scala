package restmqscala

import com.redis.RedisClient

abstract trait RestMQBroker {
  /**
   * Adds a value to the specified queue and creates the queue
   * if it does not already exist.
   */
  def add(queue: String, value: String) : Unit

  /**
   * Gets a value from the top of the queue if one exists.
   */
  def get(queue: String) : Option[String]

  /**
   * Returns the length of the specified queue or 0 if the queue
   * does not exist.
   */
  def queueLen(queue: String) : Int

  /**
   * Returns a set of all existing queues.
   */
  def listQueues() : Set[String]
}

object RedisRestMQBroker extends RestMQBroker {

  // Quick and dirty thread-safeness
  val redisLocal = new ThreadLocal[RedisClient]

  def redis = {
    var r = redisLocal.get
    if (r == null) {
      r = new RedisClient("localhost", 6379)
      redisLocal.set(r)
    }
    r
  }

  val QS_STARTED = "1"
  val QS_STOPPED = "0"

  protected object Names  {

    val QUEUE_SET = "RMQ_QUEUES"
    val QUEUE_STATUS = "RMQ_QUEUE_STATUS"

    def messageList(q: String) = q + ":queue"
    def status(q: String) = QUEUE_STATUS + ":" + q
  }

  def add(queue: String, value: String): Unit = {

    // Get the message list key for the queue
    val queueList = Names.messageList(queue)

    // Check if the queue exists and add it if not
    if (!redis.sismember(Names.QUEUE_SET, queueList))
      configureNewQueue(queue, queueList)

    // Push the value to the end of the queue's message list
    redis.rpush(queueList, value)

  }

  protected def configureNewQueue(queue: String, lkey: String) {
    if (redis.sadd(Names.QUEUE_SET, queue).get  == 1) {
      val ckey = Names.status(queue)
      redis.set(ckey, QS_STARTED)
    }    
  }

  def get(queue: String) : Option[String] = {
    redis.lpop(Names.messageList(queue))
  }

  def listQueues() : Set[String] = {
    redis.smembers(Names.QUEUE_SET) match {
      case Some(s) => s.flatMap( { case Some(s) => List(s) case None => Nil } ).toSet
      case _ => Set()
    }
  }

  def queueLen(queue: String) = redis.llen(Names.messageList(queue)).getOrElse(0)

  /**
   * Purge all queue information (mainly for testing).
   */
  def purge: Unit = {
    listQueues.foreach { qn =>
      redis.del(Names.status(qn))
      redis.del(Names.messageList(qn))
    }
    redis.del(Names.QUEUE_SET)
  }
}