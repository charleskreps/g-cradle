package org.ckreps.gcradle.adjacencylist

/**
 * @author ckreps
 */
case class Adjacency[V, E](vertex: V,
                           pointsOut: Map[E, Set[V]],
                           pointsIn: Map[E, Set[V]])
