package org.ckreps.gcradle.adjacencylist

/**
 * @author ckreps
 */
case class Edge[V, E](tail: V, name: E, head: V)

object Edge {

  def apply[V, E](edgeTriple:(V, E, V)):Edge[V, E] = {
    (Edge(_:V,_:E,_:V)).tupled(edgeTriple)
  }

  def edgeSet[V,E](triples: (V, E, V)*): Set[Edge[V, E]] = {
    triples.map(Edge(_)).toSet
  }

}

