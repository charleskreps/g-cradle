package org.ckreps.gcradle.adjacencylist

import collection.mutable.{HashMap => MuteHashMap, Set => MuteSet, MultiMap}

/**
 * @author ckreps
 */
object AdjacencyList{

  def apply[V, E]() = new AdjacencyList[V, E]

  def apply[V, E](edges:MuteSet[Edge[V, E]]) = new AdjacencyList[V, E](edges)

  def headOnlyAdjacencyList[V, E](edges:MuteSet[Edge[V, E]]) = new AdjacencyList[V, E](edges) with HeadOnlyIndex[V, E]
  
  def tailOnlyAdjacencyList[V, E](edges:MuteSet[Edge[V, E]]) = new AdjacencyList[V, E](edges) with TailOnlyIndex[V, E]
}
sealed trait EdgeIndex[V, E] {
  self:AdjacencyList[V, E] =>

  protected def indexEdgeSet(toIndex:MuteSet[Edge[V, E]]){
    toIndex.foreach{
      case Edge(tail, _, head) => {
        indexVertices(tail, head)
      }
    }
  }
  protected def indexVertices(tail:V, head:V){
    addBinding(tail, head)
    addBinding(head, tail)
  }
}
trait HeadOnlyIndex[V, E] extends EdgeIndex[V, E]{
  self:AdjacencyList[V, E] =>

  override protected def indexVertices(tail:V, head:V){
    addBinding(head, tail)
  }
}
trait TailOnlyIndex[V, E] extends EdgeIndex[V, E]{
  self:AdjacencyList[V, E] =>

  override protected def indexVertices(tail:V, head:V){
    addBinding(tail, head)
  }
}

class AdjacencyList[V, E](edges:MuteSet[Edge[V, E]])
  extends MuteHashMap[V, MuteSet[V]]
  with MultiMap[V, V]
  with EdgeIndex[V, E] {
  def this() = this(MuteSet.empty)

  indexEdgeSet(edges)
}
