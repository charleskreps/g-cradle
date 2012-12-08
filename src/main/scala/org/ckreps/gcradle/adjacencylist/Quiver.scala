package org.ckreps.gcradle.adjacencylist

import collection.mutable.{Set => MuteSet, Map => MuteMap}

/**
 * @author ckreps
 */
case class Quiver[E, V](arrows: MuteMap[E, MuteSet[V]] = MuteMap[E, MuteSet[V]]()) {

  def insert(edge:E, vertex:V) {
    arrows.get(edge).getOrElse{
      val muteSet = MuteSet(vertex)
      arrows.put(edge, muteSet)
      muteSet
    }.add(vertex)
  }
}
