package org.scalameter.examples

import org.scalameter.api._


class RangeBenchmark extends PerformanceTest.Microbenchmark {
  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "Range" in {
    measure method "boxing" in {
      using(ranges) in {
        r => r.map(_ + 1)
      }
    }
  }

  // we want to count (auto)boxing of int values
  override def measurer: Measurer = Measurer.BoxingCount(classOf[Int])

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}
