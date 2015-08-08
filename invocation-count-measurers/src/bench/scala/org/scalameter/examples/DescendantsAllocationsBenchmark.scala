package org.scalameter.examples

import org.scalameter.api._
import org.scalameter.execution.invocation.InvocationCountMatcher
import org.scalameter.execution.invocation.InvocationCountMatcher.{MethodMatcher, ClassMatcher}
import scala.util.Random


class DescendantsAllocationsBenchmark extends PerformanceTest.Microbenchmark {
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

  // we want to count allocations of `Option` method invocations
  override lazy val measurer: Measurer =
    Measurer.MethodInvocationCount(InvocationCountMatcher(
      ClassMatcher.Descendants(classOf[Either[_, _]], direct = true, withSelf = false),
      MethodMatcher.Allocation
    ))

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}