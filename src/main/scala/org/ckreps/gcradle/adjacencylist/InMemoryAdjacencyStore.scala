package org.ckreps.gcradle.adjacencylist

import collection.mutable.{Map => MuteMap, Set => MuteSet}
import GraphConverters._
import AdjacencyList._

/**
 *
 * @author ckreps
 */
class InMemoryAdjacencyStore[V, E](edges: Set[Edge[V, E]])
  extends AdjacencyStore[V, E] {
  
  validate(edges)
  
  private val BAD_EDGE_REPORT_CAP = 20
  
  private val adjacencies = asMutableAdjacencyMap(edges)

  def get(vertex: V): Option[Adjacency[V, E]] = {
    adjacencies.get(vertex).map{
      case MutableAdjacency(out, in) => {
        Adjacency(vertex,
          asImmutable(out.arrows),
          asImmutable(in.arrows))
      }
    }
  }

  def compress: Map[V, Map[E, Set[V]]] = {
    adjacencies.map{
      case (v, adjacency) => {
        (v, asImmutable(adjacency.pointsOut.arrows))
      }
    }.toMap
  }

  def order: Int = {
    adjacencies.size
  }

  def +=(edge: Edge[V, E]): AdjacencyStore[V, E] = {
    val toInsert = validate(edge, this)
    insertEdges(toInsert, adjacencies)
    this
  }

  def +=(that: AdjacencyStore[V, E]): AdjacencyStore[V, E] = {
    val toInsert = validate(that, this)
    insertEdges(toInsert, adjacencies)
    this
  }

  private def emptyIntersection(left:Set[Edge[V, E]], right:Set[Edge[V, E]]):Boolean = {
    val rightVertices = tailAndHeadSet(right)
    tailAndHeadSet(left).find(rightVertices.contains(_)).isEmpty
  }

  private def symmetricDifference[X](a: Set[X], b: Set[X]): Set[X] = {
    a.diff(b).union(b.diff(a))
  }

  private def tailAndHeadSet(edges: Set[Edge[V, E]]): Set[V] = {
    edges.flatMap(et => Set(et.tail, et.head))
  }

  private def validate(untrusted:Edge[V, E], trusted:AdjacencyStore[V, E]):Set[Edge[V, E]] = {
    val untrustedSet = Set(untrusted)
    if(trusted.order > 0){
      if(emptyIntersection(untrustedSet, asEdges(trusted.compress))){
        throwValidationException(untrustedSet)
      }
    }
    untrustedSet
  }
  
  private def validate(untrusted:AdjacencyStore[V, E], trusted:AdjacencyStore[V, E]):Set[Edge[V, E]] = {
    val untrustedSet = asEdges(untrusted.compress)
    if(trusted.order > 0){
      if(emptyIntersection(untrustedSet, asEdges(trusted.compress))){
        throwValidationException(untrustedSet)
      }
    }
    untrustedSet
  }

  private def validate(untrusted:Set[Edge[V, E]]) {
    if(untrusted.nonEmpty) {
      
      if(! UnionFind.isConnected(untrusted)){
        throw new IllegalArgumentException("Insertion results in disconnected Graph.")
      }
    }
  }
  
  private def throwValidationException(badEdges:Set[Edge[V, E]]) {
    // Cap the number of bad edges in the message since this could be a very large number:
    val badEdgeSample = badEdges.take(BAD_EDGE_REPORT_CAP)
    throw new IllegalArgumentException(
      "Insertion results in disconnected Graph.  First "+
        badEdgeSample.size +
        " of " +
        badEdges.size +
        " problematic edges: " +
        badEdgeSample)
  }

  /**
   * Creates an immutable incidence list.
   */
  private def asImmutable(muteMap: MuteMap[E, MuteSet[V]]): Map[E, Set[V]] = {
    muteMap.map{
      case(e, setV) => (e, setV.toSet)
    }.toMap
  }
}