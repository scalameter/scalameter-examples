package org.scalameter.examples

import org.scalameter.api._
import org.scalameter.execution.invocation.InvocationCountMatcher
import org.scalameter.picklers.noPickler._
import scala.util.Random


class SimpleAllocationsBenchmark extends Bench.Forked[Long] {
  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val lists = for {
    size <- sizes
  } yield (0 until size).toList

  performance of "List" in {
    measure method "map" in {
      using(lists) in {
        // approximately half of elements should be counted
        l => l.map(e => if (Random.nextBoolean()) Left(e) else Right(e))
      }
    }
  }

  // we want to count allocations of `Right`, so we just expect one map entry
  // and we reduce result map to `Long` to simplify benchmark
  def measurer: Measurer[Long] = Measurer.MethodInvocationCount(
    InvocationCountMatcher.allocations(classOf[Right[_, _]])
  ).map(v => v.copy(value = v.value.valuesIterator.sum))

  def aggregator: Aggregator[Long] = Aggregator.median

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}
