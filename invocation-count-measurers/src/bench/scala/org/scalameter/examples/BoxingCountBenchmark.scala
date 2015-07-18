package org.scalameter.examples

import org.scalameter.api._


class BoxingCountBenchmark extends PerformanceTest.Microbenchmark {
  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val lists = for {
    size <- sizes
  } yield (0 until size).toList

  performance of "Range" in {
    measure method "map" in {
      using(lists) in {
        r => r.map(_ + 1)
      }
    }
  }

  // we want to count (auto)boxing of int values
  override lazy val measurer: Measurer = Measurer.BoxingCount(classOf[Int])

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}
