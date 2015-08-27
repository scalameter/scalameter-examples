package org.scalameter.examples



import org.scalameter.{Context, Gen}
import org.scalameter.api._
import org.scalameter.japi.JBench
import scala.collection.mutable
import scala.util.Random



class BufferBenchmark extends JBench.OfflineReport {
  // we don't need `JGen` here, we can take advantage of the Scala API
  val sizes = Gen.exponential("size")(100, 10000, 10)

  // we don't need `JGen` here,
  // we can take advantage of the Scala API and for comprehensions
  val arrayBuffers = for {
    size <- sizes
  } yield (size, new mutable.ArrayBuffer[Int]())

  // we don't need `JGen` here,
  // we can take advantage of the Scala API and for comprehensions
  val listBuffers = for {
    size <- sizes
  } yield (size, new mutable.ListBuffer[Int]())

  // we don't need `ContextBuilder` here, we can take advantage of Scala API
  override def defaultConfig: Context = Context(
    exec.benchRuns -> 25,
    exec.independentSamples -> 2
  )

  def arrayBufferSetup(v: (Int, mutable.ArrayBuffer[Int])): Unit = {
    val (size, buffer) = v

    val random: Random = new Random(size)
    (0 to size).foreach(_ => buffer += random.nextInt())
  }

  def listBufferSetup(v: (Int, mutable.ListBuffer[Int])): Unit = {
    val (size, buffer) = v

    val random: Random = new Random(size)
    (0 to size).foreach(i => buffer += random.nextInt())
  }

  @gen("arrayBuffers")
  @setup("arrayBufferSetup")
  @benchmark("buffers.forloops")
  @curve("array")
  def arrayBufferSum(v: (Int, mutable.ArrayBuffer[Int])): Int = {
    val (_, buffer) = v

    buffer.sum
  }

  @gen("listBuffers")
  @setup("listBufferSetup")
  @benchmark("buffers.forloops")
  @curve("list")
  def listBufferSum(v: (Int, mutable.ListBuffer[Int])): Int = {
    val (_, buffer) = v

    buffer.sum
  }

  @gen("arrayBuffers")
  @benchmark("buffers.ops.add")
  @curve("array")
  def arrayBufferAdd(v: (Int, mutable.ArrayBuffer[Int])) = {
    val (size, buffer) = v

    val random = new Random(size)
    var i = 0
    while (i < size) {
      buffer += random.nextInt()
      i += 1
    }

    buffer
  }

  @gen("listBuffers")
  @benchmark("buffers.ops.add")
  @curve("list")
  def listBufferAdd(v: (Int, mutable.ListBuffer[Int])) = {
    val (size, buffer) = v

    val random = new Random(size)
    var i = 0
    while (i < size) {
      buffer += random.nextInt()
      i += 1
    }

    buffer
  }

  @gen("arrayBuffers")
  @setup("arrayBufferSetup")
  @benchmark("buffers.ops.remove")
  @curve("array")
  def arrayBufferRemove(v: (Int, mutable.ArrayBuffer[Int])) = {
    val (size, buffer) = v

    var random = new Random(size)
    val newBuffer = random.shuffle(buffer)
    random = new Random(size)
    var i = 0
    while (i < size) {
      newBuffer -= random.nextInt()
      i += 1
    }

    buffer
  }

  @gen("listBuffers")
  @setup("listBufferSetup")
  @benchmark("buffers.ops.remove")
  @curve("list")
  def listBufferRemove(v: (Int, mutable.ListBuffer[Int])) = {
    val (size, buffer) = v

    var random = new Random(size)
    val newBuffer = random.shuffle(buffer)
    random = new Random(size)
    var i = 0
    while (i < size) {
      newBuffer -= random.nextInt()
      i += 1
    }

    buffer
  }
}
