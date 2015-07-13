package org.scalameter.examples

import org.scalameter.api._
import org.scalameter.execution.invocation.InvocationCountMatcher
import org.scalameter.execution.invocation.InvocationCountMatcher.{MethodMatcher, ClassMatcher}


object ListBenchmark extends PerformanceTest.OfflineRegressionReport {
  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val lists = for {
    size <- sizes
  } yield (0 until size).toList

  performance of "Option" in {
    measure method "allocations" in {
      using(lists) in {
        l => l.map(Some(_))
      }
    }
  }

  // we want to count allocations of `Option` method invocations
  override def measurer: Measurer =
    Measurer.MethodInvocationCount(InvocationCountMatcher(
      ClassMatcher.Descendants(classOf[Option[_]], direct = true, withSelf = false),
      MethodMatcher.MethodName("<init>")
    ))

  // we want one JVM instance since this measurer is deterministic
  override def defaultConfig: Context = Context(exec.independentSamples -> 1)
}
