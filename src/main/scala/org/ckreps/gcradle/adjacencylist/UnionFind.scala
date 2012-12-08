package org.ckreps.gcradle.adjacencylist

import collection.mutable.{ListBuffer, MultiMap, Map => MuteMap, HashMap}


/**
 * A "union-find" algorithm for fast graph connectivity checking.
 * @see http://en.wikipedia.org/wiki/Disjoint-set_data_structure
 *
 * @author ckreps
 */
object UnionFind {

  def isConnected[V, E](edgeSet:Set[Edge[V, E]]):Boolean = {
    new UnionFind[V, E]().insert(edgeSet)
  }
}


class UnionFind[V, E] {

  // TODO: Parallel implementation
  //private val parents:MuteMap[V, Int] = new JConcurrentMapWrapper(new ConcurrentHashMap[V, Int])
  //private val sizes:MuteMap[Int, Int] = new JConcurrentMapWrapper(new ConcurrentHashMap[Int, Int])

  private val parents = MuteMap[V, V]()
  private val sizes = MuteMap[V, Int]()
  private var biggestCount = 0
  private var biggestRoot:V = _

  /**
   * Returns true if given edges are connected.
   * The edges are analyzed against edges added
   * from previous invocations of insert on the same
   * instance.
   *
   * @param edgeSet
   * @return True if the edges are connected when analyzed against any edges previously inserted.
   */
  def insert(edgeSet:Set[Edge[V, E]]):Boolean = {

    edgeSet.foreach{
      case Edge(tail, _, head) => {
        if(!connected(tail, head)){
          union(tail, head)
        }
      }
    }

    Option(biggestRoot) match {
      case Some(max) => sizes(max) == parents.size
      case None => parents.size <= 1
    }
  }
  

  private def find(vId:V):V = {

    val first = parents.get(vId)
    if(first.isDefined){
      var next = first.get
      var previous = vId
      while(next != previous){
        previous = next
        next = parents(previous)
      }
      next
    } else {
      parents(vId) = vId
      sizes(vId) = 1
      vId
    }
  }

  private def connected(p:V, q:V):Boolean = {
    find(p) == find(q)
  }

  private def union(p:V, q:V) {

    val i = find(p)
    val j = find(q)
    if(i != j) {
      if(sizes(i) < sizes(j)){
        changeParent(i, j)
      } else {
        changeParent(j, i)
      }
    }
  }
  
  private def changeParent(from:V, to:V){
    parents(from) = to
    val newSize = sizes(from) + sizes(to)
    sizes(to) = newSize
    if(newSize > biggestCount){
      biggestRoot = to
      biggestCount = newSize
    }
  }

}
