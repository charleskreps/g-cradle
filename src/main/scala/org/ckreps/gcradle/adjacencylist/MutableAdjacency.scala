package org.ckreps.gcradle.adjacencylist

/**
 *
 * @author ckreps
 */
case class MutableAdjacency[V, E](pointsOut: Quiver[E, V] = Quiver[E, V](),
                                  pointsIn: Quiver[E, V] = Quiver[E, V]())


