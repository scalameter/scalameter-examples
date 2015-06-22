package org.scalameter.examples

import org.scalameter.api._
import org.scalameter.Measurer._


object ListBenchmark extends PerformanceTest.OfflineRegressionReport {
  val tupledLists = for {
    a <- Gen.range("a")(0, 100, 10)
    b <- Gen.range("b")(100, 200, 10)
  } yield ((0 until a).toList, (100 until b).toList)

  performance of "List" in {
    measure method "zip" in {
      using(tupledLists) config (
        exec.benchRuns -> 10, // we want 10 benchmark runs
        exec.independentSamples -> 1 // and one JVM instance
      ) in {
        case (a, b) => a.zip(b)
      }
    }
  }

  override def measurer: Measurer = new MemoryFootprint

  // GZIPJSONSerializationPersistor is default but we want to choose custom path for regression data
  override def persistor: Persistor = new GZIPJSONSerializationPersistor("target/results")
}
