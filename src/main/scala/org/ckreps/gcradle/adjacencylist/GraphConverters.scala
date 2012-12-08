package org.ckreps.gcradle.adjacencylist

import collection.mutable.{Set => MuteSet, Map => MuteMap}

/**
 * A mixed bag of methods for converting between types used
 * to represent an adjacency list (or parts of one).
 *
 * I'd like to remove this class eventually since all this
 * conversion stuff smells a little but it's useful for now.
 *
 * @author ckreps
 */
object GraphConverters {

  type AdjacencyMap[V, E] = MuteMap[V, MutableAdjacency[V, E]]

  def asEdges[V, E](edgeMap: Map[V, Map[E, Set[V]]]): Set[Edge[V, E]] = {

    (for ((tail, incidence) <- edgeMap.toSeq;
         (edge, headList) <- incidence.toSeq;
         (head) <- headList) yield Edge(tail, edge, head)).toSet
  }

  def asMutableAdjacencyMap[V, E](edgeMap: Map[V, Map[E, Set[V]]]): MuteMap[V, MutableAdjacency[V, E]] = {
    // TODO: Optimize with single conversion. For now we'll just convert twice.
    asMutableAdjacencyMap(
      asEdges(
        edgeMap))
  }

  def asMutableAdjacencyMap[V, E](edges: Set[Edge[V, E]]): MuteMap[V, MutableAdjacency[V, E]] = {
    val muteAdjMap = MuteMap[V, MutableAdjacency[V, E]]()
    insertEdges(edges, muteAdjMap)
    muteAdjMap
  }

  def asEdgeMap[V, E](edges: Set[Edge[V, E]]): Map[V, Map[E, Set[V]]] = {
    val ret = MuteMap[V, MuteMap[E, MuteSet[V]]]()

    def findOrCreateVertexEntry(vertex: V): MuteMap[E, MuteSet[V]] = {
      ret.get(vertex).getOrElse{
        val muteMap = MuteMap[E, MuteSet[V]]()
        ret.put(vertex, muteMap)
        muteMap
      }
    }

    def insertVertex(edge: E, vertex: V, map: MuteMap[E, MuteSet[V]]){

      val muteSet = map.get(edge).getOrElse{
        val muteSet = MuteSet[V]()
        map.put(edge, muteSet)
        muteSet
      }
      muteSet.add(vertex)
    }

    edges.foreach{
      case Edge(tail, edge, head) => {
        val incidence = findOrCreateVertexEntry(tail)
        insertVertex(edge, head, incidence)
      }
    }

    // Make immutable:
    ret.map{
      case (vertex, edgeMap) => {
        (vertex, edgeMap.map{
          case (edge, incidenceSet) => {
            (edge, incidenceSet.toSet)
          }
        }.toMap)
      }
    }.toMap
  }

  def insertEdges[V, E](edges: Set[Edge[V, E]], adjacencies: MuteMap[V, MutableAdjacency[V, E]]): Unit = {

    edges.foreach{
      case Edge(tail, edge, head) => {
        val tailMuteAdj = findOrCreateMutableAdjacency(tail, adjacencies)
        val headMuteAdj = findOrCreateMutableAdjacency(head, adjacencies)
        tailMuteAdj.pointsOut.insert(edge, head)
        headMuteAdj.pointsIn.insert(edge, tail)
      }
    }
  }


  def findOrCreateMutableAdjacency[V, E](vertex: V,
                                         adjacencies: MuteMap[V, MutableAdjacency[V, E]]): MutableAdjacency[V, E] = {

    adjacencies.get(vertex).getOrElse{
      val ret = MutableAdjacency[V, E]()
      adjacencies.put(vertex, ret)
      ret
    }
  }

}

