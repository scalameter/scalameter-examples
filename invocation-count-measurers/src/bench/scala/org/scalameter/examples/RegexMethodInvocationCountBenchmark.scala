package org.scalameter.examples

import org.scalameter.api._
import org.scalameter.api.Aggregator.Implicits._
import org.scalameter.execution.invocation.InvocationCountMatcher
import org.scalameter.picklers.noPickler._


class RegexMethodInvocationCountBenchmark extends Bench.Forked[Map[String, Long]] {
  val sizes = Gen.range("size")(1000, 10000, 1000)
  val methods = Gen.enumeration("method")(
    (bi: BigInt) => bi.toString(), (bi: BigInt) => bi.toString(10), (bi: BigInt) => bi.toByteArray
  )

  val ranges = for {
    size <- sizes
  } yield 0 until size

  val combined = Gen.crossProduct(ranges, methods)

  performance of "Range" in {
    measure method "foreach" in {
      using(combined) in {
        // count should equal range size for all methods
        case (r, m) => r.foreach(e => m(BigInt(e)))
      }
    }
  }

  // we want to count all methods that begins with "to"
  def measurer: Measurer[Map[String, Long]] = Measurer.MethodInvocationCount(
    InvocationCountMatcher.forRegex("scala.math.BigInt".r, "^to\\w+".r)
  )

  def aggregator: Aggregator[Map[String, Long]] = Aggregator.median

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}
