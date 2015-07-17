package org.scalameter.examples

import org.scalameter.api._
import org.scalameter.execution.invocation.InvocationCountMatcher
import org.scalameter.picklers.noPickler._


class SimpleMethodInvocationCountBenchmark extends PerformanceTest.Microbenchmark {
  val sizes = Gen.range("size")(1000, 10000, 1000)
  val methods = Gen.enumeration("method")(
    (bi: BigInt) => bi.toString(), (bi: BigInt) => bi.toString(10), (bi: BigInt) => bi.toByteArray
  )

  val ranges = for {
    size <- sizes
  } yield 0 until size

  val combined = Gen.tupled(ranges, methods)

  performance of "Range" in {
    measure method "foreach" in {
      using(combined) in {
        // count should equal range size for both toString methods, and 0 for toByteArray method
        case (r, m) => r.foreach(e => m(BigInt(e)))
      }
    }
  }

  // we want to count all methods that are toString methods
  override lazy val measurer: Measurer =
    Measurer.MethodInvocationCount(InvocationCountMatcher.forName("scala.math.BigInt", "toString"))

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}
