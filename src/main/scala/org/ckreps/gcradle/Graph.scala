package org.ckreps.gcradle


/**
 * Defines methods intended to be implemented by all Graph types
 * in the system.  The plan is to keep the interaction with Graphs
 * uniform be they very large and backed by a remote store or small
 * in-memory implementations.
 *
 *
 * Some initial thoughts on requirements for our Graphs.   A Graph...
 * 1. Must always be connected.
 * 2. Can contain multiple name types (aka "labeled edges")
 * 3. Will support construction via an optimized representation.
 * 4. Can supply an optimized representation of itself.
 * 5. Will be immutable.
 * 6. Can be a single Vertex.
 * 7. Will be directional.
 *
 * @author ckreps
 */
trait Graph[V, E] {

  /**
   * The number of vertices.
   */
  def order:Int

  /**
   * Produces a light weight representation of the Graph.
   */
  def compress:Map[V, Map[E, Set[V]]]

  /**
   * Creates a new Graph from the union of this Graph and the given one.
   * An exception is thrown if the result would have been a disconnected Graph.
   */
  def union(that:Graph[V, E]):Graph[V, E]

  /**
   * Creates a Graph that is a subgraph of this Graph.  The subgraph will match as
   * much of the given Pattern as possible starting at the given target
   * vertex.  Will return None if nothing matches.
   */
  def sub(target:V, pattern:Pattern[E]):Option[Graph[V, E]]

}
