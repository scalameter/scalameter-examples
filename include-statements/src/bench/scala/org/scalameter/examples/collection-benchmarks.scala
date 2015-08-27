package org.scalameter.examples



import org.scalameter.api._



/** When a benchmark is implemented as a trait,
 *  it is actually a *bench template*.
 *  Bench templates are not automatically detected as tests by SBT.
 *  Instead, they have to be included into bench groups with the
 *  include statement.
 */
trait RangeBenchmark extends Bench.OfflineReport {
  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "Range" in {
    measure method "map" in {
      using(ranges) in {
        r => r.map(_ + 1)
      }
    }
  }
}


/** Another benchmark template, this time used to measure the memory footprint.
 */
trait RangeMemoryFootprint extends Bench.OfflineReport {
  override def measurer = new Measurer.MemoryFootprint

  val sizes = Gen.range("size")(1000000, 5000000, 2000000)

  performance of "MemoryFootprint" in {
    performance of "Array" in {
      using(sizes) config (
        exec.minWarmupRuns -> 2,
        exec.maxWarmupRuns -> 5,
        exec.benchRuns -> 5,
        exec.independentSamples -> 1
      ) in { sz =>
        // The memory footprint of the resulting array gets measured.
        (0 until sz).toArray
      }
    }
  }
}


object CollectionBenchmarks extends Bench.Group {
  // We override the resultDir key here to ensure that the reports
  // get generated in different subdirectories.
  performance of "memory" config(
    reports.resultDir -> "target/benchmarks/memory"
  ) in {
    include(new RangeMemoryFootprint {})
  }

  // Same here -- related report is separate from memory footprint report.
  performance of "running time" config(
    reports.resultDir -> "target/benchmarks/time"
  ) in {
    include(new RangeBenchmark {})
  }
}
