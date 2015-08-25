package org.scalameter.examples

import org.scalameter.api._
import org.scalameter.execution.invocation.InvocationCountMatcher
import org.scalameter.picklers.noPickler._


class FullMethodInvocationCountBenchmark extends Bench.Forked[Long] {
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
        // count should equal range size for no-arg toString method, and 0 for rest methods
        case (r, m) => r.foreach(e => m(BigInt(e)))
      }
    }
  }

  // we want to count only no-arg toString method, so we just expect one map entry
  // and we reduce result map to `Long` to simplify benchmark
  def measurer: Measurer[Long] = Measurer.MethodInvocationCount(
    InvocationCountMatcher.forClass(classOf[BigInt], classOf[BigInt].getMethod("toString"))
  ).map(v => v.copy(value = v.value.valuesIterator.sum))

  def aggregator: Aggregator[Long] = Aggregator.median

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}
