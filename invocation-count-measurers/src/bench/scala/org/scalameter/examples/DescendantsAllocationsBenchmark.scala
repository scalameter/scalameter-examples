package org.scalameter.examples



import org.scalameter.api._
import org.scalameter.api.Aggregator.Implicits._
import org.scalameter.execution.invocation.InvocationCountMatcher
import org.scalameter.execution.invocation.InvocationCountMatcher.MethodMatcher
import org.scalameter.execution.invocation.InvocationCountMatcher.ClassMatcher
import org.scalameter.picklers.noPickler._
import scala.util.Random



class DescendantsAllocationsBenchmark extends Bench.Forked[Map[String, Long]] {
  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val lists = for {
    size <- sizes
  } yield (0 until size).toList

  performance of "List" in {
    measure method "map" in {
      using(lists) in {
        // since we count both Left and Right, all list elements should be counted
        l => l.map(e => if (Random.nextBoolean()) Left(e) else Right(e))
      }
    }
  }

  // we want to count allocations of `Either`
  def measurer: Measurer[Map[String, Long]] = Measurer.MethodInvocationCount(
    InvocationCountMatcher(
      ClassMatcher.Descendants(classOf[Either[_, _]], direct = true, withSelf = false),
      MethodMatcher.Allocation
    )
  )

  def aggregator: Aggregator[Map[String, Long]] = Aggregator.median

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}
