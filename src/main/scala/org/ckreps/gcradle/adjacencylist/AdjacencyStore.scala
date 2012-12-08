package org.ckreps.gcradle.adjacencylist

import collection.mutable.{Map => MuteMap}
import GraphConverters._

/**
 * Interface used to represent a graph as an adjacency list.  It pairs
 * every vertex in the graph with adjacent vertices grouped by name type.
 * It is mutable where needed for efficiency.
 *
 * An AdjacencyList (for this app) is always well-connected.  A Graph
 * can then rely on this and not worry about implementing checks for connectivity
 * itself.  It's also easier to check for connectivity here than at a higher level.
 *
 * The Graph algorithms frequently build up new Graphs in stages.  As a
 * result the AdjacencyList need only provide additive operations.  This
 * is nice since deletion would require us to seek out any "references" to
 * the deleted elements we might have elsewhere which is probably expensive.
 *
 * All write operations are atomic (the "+=" methods).  If the resulting
 * AdjacencyList would be disconnected then NONE of the given vertices are added
 * and an exception is thrown.
 *
 * The rules here are pretty simple:
 * - We will index all vertexes.
 * - We will only support insert and read operations (no update or delete).
 * - We will only perform operations resulting in a connected AdjacencyList.
 *
 * @author ckreps
 */
trait AdjacencyStore[V, E] {

  /**
   * Returns all adjacent vertices for the given vertex if found.
   */
  def get(vertex: V): Option[Adjacency[V, E]]

  /**
   * Returns the minimum of information needed to
   * construct a Graph (or another AdjacencyStore) from
   * this AdjacencyStore.
   */
  def compress: Map[V, Map[E, Set[V]]]


  /**
   * The number of vertices.
   */
  def order: Int

  /**
   * Insertions must result in a connected graph.
   */
  def +=(edges: Edge[V, E]): AdjacencyStore[V, E]

  def +=(that: AdjacencyStore[V, E]): AdjacencyStore[V, E]
}

object AdjacencyStore {

  def apply[V, E]() = new InMemoryAdjacencyStore[V, E](Set.empty)

  def apply[V, E](edgeMap: Map[V, Map[E, Set[V]]]): AdjacencyStore[V, E] = {
    new InMemoryAdjacencyStore[V, E](asEdges(edgeMap))
  }

  def apply[V, E](edges: Set[Edge[V, E]]): AdjacencyStore[V, E] = {
    new InMemoryAdjacencyStore[V, E](edges)
  }
}
