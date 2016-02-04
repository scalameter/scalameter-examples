package org.scalameter.examples



import org.scalameter.api._
import org.scalameter.picklers.noPickler._



class BoxingCountBenchmark extends Bench.Forked[Long] {
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

  // we want to count (auto)boxing of int values, so we just expect one map entry
  // and we reduce result map to `Long` to simplify benchmark
  def measurer: Measurer[Long] = Measurer.BoxingCount(classOf[Int]).map(v =>
    v.copy(value = v.value.valuesIterator.sum)
  )

  def aggregator: Aggregator[Long] = Aggregator.median

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}
