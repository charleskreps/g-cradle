package org.ckreps.gcradle.util

import collection.immutable.ListMap

/**
 * Times execution of operations that process collections, e.g.,
 * [[scala.collection.List]]'s diff and intersection methods.
 *
 * This is useful because time complexity can be difficult to reason about:
 * - The max size of a collection is typically unbound but our compute resources aren't.
 * - It isn't always documented in the libraries you use.
 * - The operation may be poorly written or uses legacy coding practices.
 * - Hardware impacts some algorithms more than others, e.g., it may or may not
 *   utilize multiple cores efficiently.
 * The point is even if the operation is a one line method correctly documented as
 * being O(log n) there is still uncertainty.
 *
 * To use just define a generator function appropriate for the operation you are
 * time testing.  The generator is used to create inputs of various size to the
 * target test operation.
 *
 * Here's an example that tests [[scala.collection.Set]] union on an empty Set:
 * {{{
 * scala> TimeTest(x => {Set((0 to x):_*)}, Set.empty.union(_:Set[Int]), logger = Some(println))
 * }}}
 * It took my machine about 13 seconds to give this result:
 * {{{
 * StressResult(Map(10 -> 0, 100 -> 0, 1000 -> 3, 10000 -> 4, 100000 -> 58, 1000000 -> 981))
 * }}}
 * The result maps input size to average execution time in milliseconds.
 *
 * @author ckreps
 */
object TimeTest {

  val POW_10_TO_4 = orderOfMag(4, 10)
  val POW_10_TO_6 = orderOfMag(6, 10)
  val POW_10_TO_10 = orderOfMag(10, 10)
  val TENS = expo(10, 10)
  val HUNDREDS = expo(10, 100)
  val THOUSANDS = expo(10, 1000)

  case class StressResult(averages:ListMap[Int, Long] = ListMap.empty){
    def +(that:StressResult) = StressResult(averages ++ that.averages)
  }

  def apply[P, C[P], T <: C[P] => Any](generator: Int => C[P],
                                       target:T,
                                       sizes:Seq[Int] = POW_10_TO_6,
                                       repeats:Int = 10,
                                       warmupCount:Int = 2,
                                       logger: Option[String => Any] = None):StressResult = {

    def log = (x:String) => logger.foreach(_(x))
    var allResults = StressResult()

    for(size <- sizes) {

      // Warmup:
      val payload = generator(size)
      warmup(payload, target, warmupCount)

      // Test:
      var total = 0L
      for(i <- 1 to repeats) {
        val execTime = time(target(payload))
        log("Size "+size+" run "+i+"/"+repeats+" = "+execTime)
        total = total + execTime
      }
      val result = StressResult(ListMap(size -> (total / repeats).toLong))
      log(result.toString)
      allResults = allResults + result
    }
    allResults
  }

  def time(thunk: => Any):Long = {
    val s = System.currentTimeMillis
    thunk
    System.currentTimeMillis - s
  }  

  private def warmup[P, C[P], T <: C[P] => Any](payload:C[P], target:T, warmupCount:Int) {
    for(i <- 0 until warmupCount) {
      target(payload)
    }
  }

  private def orderOfMag(max:Int, power:Int):Seq[Int] = {
    (1 to max).map(math.pow(power, _).toInt)
  }

  private def expo(max:Int, times:Int):Seq[Int] = {
    (1 to max).map(_ * times)
  }
}