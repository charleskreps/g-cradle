package org.ckreps.gcradle.adjacencylist

import collection.mutable.ListBuffer
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import UnionFind._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


/**
 * @author ckreps
 */
@RunWith(classOf[JUnitRunner])
class UnionFindTest
  extends Spec
  with ShouldMatchers {


  describe("Analyzing connected graphs with UnionFind.") {

    it("Should return true for a chain graph."){
      isConnected(chain(1000)) should be (true)
    }
    it("Should return true for a grid graph."){
      isConnected(grid(10, 20)) should be (true)
    }
    it("Should return true when additional graphs are put in the same instance."){
      val unionFind = new UnionFind[Int, String]
      unionFind.insert(chain(100))
      unionFind.insert(chain(100,90)) should be (true)
    }
    it("Should return true for a single edge."){
      isConnected(Set(Edge(1, "x", 2))) should be (true)
    }
    it("Should return true for a single, self-referring edge."){
      isConnected(Set(Edge(1, "x", 1))) should be (true)
    }
    it("Should return true for an empty set."){
      isConnected(Set[Edge[Int, String]]()) should be (true)
    }
  }

  describe("Analyzing disconnected graphs with UnionFind."){

    it("Should return false for a pair of disjoint edges."){
      isConnected(Edge.edgeSet((1, "x", 2),(11, "x", 12))) should be (false)
    }
    it("Should return false when given a large graph with 2 large disjoint edge sets."){
      val setA = grid(100, 200)
      val setB = grid(100, 200, 105, 205)
      isConnected(setA.union(setB)) should be (false)
    }
    it("Should return false when given a large graph where one edge is disjoint and self-referring."){
      val setA = chain(10000)
      val id = 2000000
      isConnected(setA.union(Edge.edgeSet((id, "x", id)))) should be (false)
    }
    it("Should return false when additional disjoint graphs a put in the same instance."){
      val unionFind = new UnionFind[Int, String]
      unionFind.insert(chain(100))
      unionFind.insert(chain(100, 105)) should be (false)
    }
    it("Should return false for a graph made of many small disjoint edge sets."){
      isConnected(disjointChain(500000)) should be (false)
    }
  }

  def chain(count:Int, offset:Int = 0):Set[Edge[Int, String]] = {
    Edge.edgeSet((offset until offset + count).map(i => {(i, "x", i+1)}):_*)
  }

  def disjointChain(count:Int):Set[Edge[Int, String]] = {
    val edges = (0 to count).map(i => {
      val even = i % 2
      (i + even, "x", i + even + 1)
    })
    Edge.edgeSet(edges:_*)
  }

  def grid(width:Int, height:Int, widthOffset:Int = 0, heightOffset:Int = 0):Set[Edge[Int, String]] = {

    val maxWidth = width + widthOffset
    val maxHeight = height + heightOffset
    
    val list = ListBuffer[(Int, String, Int)]()
    for (x <- widthOffset to maxWidth; y <- heightOffset to maxHeight) {
      val id = x * y
      if(x < maxWidth){
        list.prepend((id, "x", (x + 1) * y))
      }
      if(y < maxHeight){
        list.prepend((id, "x", x * (y + 1)))
      }
    }
    Edge.edgeSet(list:_*)
  }
}
