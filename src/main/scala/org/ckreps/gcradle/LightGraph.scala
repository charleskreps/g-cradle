package org.ckreps.gcradle

import adjacencylist.{Adjacency, Edge, AdjacencyStore}
import collection.mutable.{Set => MuteSet, ArrayBuffer}

/**
 * @author ckreps
 */
case class LightGraph[V, E](adjList:AdjacencyStore[V, E])
  extends Graph[V, E] {

  private val store = AdjacencyStore(adjList.compress)

  override val order = store.order

  override lazy val compress = store.compress

  override def union(that:Graph[V, E]):Graph[V, E] = {
    LightGraph(
      AdjacencyStore(compress) += AdjacencyStore(that.compress))
  }

  /**
   * Creates a Graph that is a subgraph of this Graph.  The subgraph will match as
   * much of the given Pattern as possible starting at the given target
   * vertex.  Will return None if nothing matches.
   */
  override def sub(target:V, pattern:Pattern[E]):Option[Graph[V, E]] = {

    val found = MuteSet[Edge[V, E]]()
    var hits = findPatternMatches(pattern, Pattern.START, target)
    while(hits.size > 0) {
      hits = hits.flatMap{
        case PatternMatch(tail, edge, head, patternHeadIndex) => {
          val edgeTriple = Edge(tail, edge, head)
          if(!found.contains(edgeTriple)) {
            found.add(edgeTriple)
            findPatternMatches(pattern, patternHeadIndex, head)
          } else {
            Nil
          }
        }
      }
    }

    if(found.size > 0){
      Some(LightGraph(AdjacencyStore(found.toSet)))
    } else {
      None
    }
  }

  private case class PatternMatch[V, E](tail:V, edge:E, head:V, patternHeadIndex:Int)

  private def findPatternMatches(pattern:Pattern[E],
                                 patternTailIndex:Int,
                                 tail:V):Seq[PatternMatch[V, E]] = {

    val buffer = ArrayBuffer[PatternMatch[V, E]]()

    for(Adjacency(_, targetOut, _) <- store.get(tail);
        (patternEdge, patternVertices) <- pattern.get(patternTailIndex);
        targetVertices <- targetOut.get(patternEdge);
        (patternHead, targetHead) <- patternVertices.zip(targetVertices)) {

      buffer += PatternMatch(tail, patternEdge, targetHead, patternHead)
    }
    buffer
  }
}
