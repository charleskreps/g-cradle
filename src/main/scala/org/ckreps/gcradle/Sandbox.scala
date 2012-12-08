package org.ckreps.gcradle

import adjacencylist._
import adjacencylist.AdjacencyList._
import util.TimeTest
import util.TimeTest._
import collection.mutable.{ListBuffer, Set => MuteSet, Map => MuteMap}

/**
 * @author ckreps
 */
object Sandbox {

  private type ET = (Int, String, Int)
  private type EDGE_SET = Set[Edge[Int, String]]

  
  def main(args:Array[String]) {

    TimeTest(x => {Set((0 to x):_*)}, Set.empty.union(_:Set[Int]), logger = Some(println))

    /*
    //val generator = edgesWithIdInterval(_:Int, 1)
    //val generator = squareGraph(_:Int)
    val generator = (i:Int) => chain(i)

    //val target = (edges:Set[Edge[Int, String]]) => { UnionFind.isConnected(edges) }
    val target = (edges:Set[Edge[Int, String]]) => { GraphConverters.asMutableAdjacencyMap(edges) }




    TimeTest(
      generator,
      target,
      //sizes = Seq(1000),
      //repeats = 3,
      //warmupCount = 0,
      logger = Some(println))

    //val edges = Edge.edgeSet((5,"x",6),(6,"x",7),(3,"x",4),(4,"x",5),(10,"x",10))
    //val edges = Edge.edgeSet((8,"x",8),(9,"x",9),(10,"x",10))
    //println(UnionFind.isConnected(edges))
    */
  }


  def disjointChain(count:Int):Set[Edge[Int, String]] = {
    val edges = (0 to count).map(i => {
      val even = i % 2
      (i + even, "x", i + even + 1)
    })
    Edge.edgeSet(edges:_*)
  }

  def chain(count:Int):Set[Edge[Int, String]] = {
    Edge.edgeSet((0 to count).map(i => {(i, "x", i+1)}):_*)
  }

  def grid(width:Int, height:Int):Set[Edge[Int, String]] = {

    val list = ListBuffer[(Int, String, Int)]()
    for (x <- 1 to width; y <- 1 to height) {
      val id = x * y
      if(x < width){
        list.prepend((id, "x", (x + 1) * y))
      }
      if(y < height){
        list.prepend((id, "x", x * (y + 1)))
      }
    }
    Edge.edgeSet(list:_*)
  }

}
