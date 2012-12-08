package org.ckreps.gcradle

/**
 * @author ckreps
 */
object Pattern {
  val START = 1
}
case class Pattern[E](indexMap:Map[Int, Map[E, Set[Int]]]) {

  assert(indexMap.get(Pattern.START).nonEmpty, "Pattern must contain start entry.")

  def get(index:Int):Map[E, Set[Int]] = {
    indexMap.get(index).getOrElse(Map[E, Set[Int]]())
  }
}

