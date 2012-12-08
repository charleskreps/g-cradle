package org.ckreps.gcradle.adjacencylist

import org.scalatest.Spec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Assertions._
import org.scalatest.matchers.ShouldMatchers
import Edge.edgeSet

/**
 * @author ckreps
 */
@RunWith(classOf[JUnitRunner])
class AdjacencyListTest
  extends Spec
  with ShouldMatchers {

  case class Example(name: String, set: Set[Edge[Int, String]])

  describe("Adding one name to a newly created AdjacencyList") {
    val list = AdjacencyStore[Int, String]
    list += Edge(1, "name", 2)
    it("should be retrievable via it's tail and head") {
      assert(list.get(1) != None)
      assert(list.get(2) != None)
    }
    it("should not allow us to retrieve any vertex we did not add") {
      assert(list.get(0) == None)
      assert(list.get(3) == None)
    }
  }

  describe("Adding 3 edges with the same tail") {
    val list = AdjacencyStore(edgeSet(
      (1, "name", 2),
      (1, "name", 3),
      (1, "name", 4)))

    it("should indicate the tail points to the other 3") {
      val vertexMap = list.get(1).get

      val pointsIn = vertexMap.pointsIn
      pointsIn.size should be(0)

      val maybePointsOutForEdge = vertexMap.pointsOut.get("name")
      assert(maybePointsOutForEdge != None)
      val pointsOutForEdge = maybePointsOutForEdge.get
      pointsOutForEdge.size should be(3)
      List(2, 3, 4).foreach(head => assert(pointsOutForEdge.contains(head)))
    }

    it("should have entires for each head indicating the tail points to them") {
      List(2, 3, 4).foreach(head => {
        val vertexMap = list.get(head).get
        val pointsAtMe = vertexMap.pointsIn
        assert(pointsAtMe.size == 1, pointsAtMe.size)
        val maybeTailVertices = pointsAtMe.get("name")
        assert(maybeTailVertices != None)
        val tailVertices = maybeTailVertices.get
        assert(tailVertices.size == 1)
        assert(tailVertices.contains(1))
      })
    }
  }

  describe("Attempting to construct a new AdjacencyStore with a DISCONNECTED Graph") {
    evaluating{
      AdjacencyStore(Edge.edgeSet((1,"x",2),(3,"x",4)))
    } should produce[IllegalArgumentException]
  }

  describe("Inserting Edges resulting in a DISCONNECTED Graph") {

    val examples = Set(
      Example("set1", edgeSet(
        (1, "e", 2),
        (1, "e", 3),
        (1, "e", 4),
        (1, "e", 5),
        (1, "e", 6),
        (10, "e", 11))
      ), Example("set2", edgeSet(
        (1, "e", 1),
        (1, "e", 1),
        (11, "e", 11),
        (2, "e", 2),
        (3, "e", 3),
        (0, "e", 0))
      ), Example("set3", edgeSet(
        (0, "e", 20),
        (19, "e", 1),
        (31, "e", 32),
        (5, "e", 35),
        (31, "e", 33),
        (30, "e", 34))))

    examples.foreach(example => {
      it("should throw an IllegalArgumentException.  Used " + example.name) {
        evaluating {
          AdjacencyStore(example.set)
        } should produce[IllegalArgumentException]
      }
    })
  }

  describe("Inserting an EdgeTriple set resulting in a CONNECTED Graph") {
    val initialInsertSet =
      Set(Edge(1, "e", 2),
        Edge(2, "e", 3),
        Edge(3, "e", 4),
        Edge(4, "e", 5),
        Edge(5, "e", 6),
        Edge(6, "e", 7),
        Edge(7, "e", 8))

    val examples = Set(
      Example("set1", edgeSet(
        (1, "e", 2),
        (1, "e", 3),
        (1, "e", 4),
        (1, "e", 5),
        (1, "e", 6),
        (10, "e", 1))
      ), Example("set2", edgeSet(
        (1, "e", 1),
        (1, "e", 0),
        (0, "e", 11),
        (2, "e", 11),
        (3, "e", 2),
        (0, "e", 3))
      ), Example("set3", edgeSet(
        (3, "e", 30),
        (35, "e", 1),
        (31, "e", 30),
        (5, "e", 35),
        (31, "e", 30),
        (31, "e", 5))))

    examples.foreach(example => {

      it("should not throw an exception when given " + example.name) {
        AdjacencyStore(initialInsertSet) += AdjacencyStore(example.set)
      }
    })

    describe("Inserting multiple times in an AdjacencyList") {
      val adjacencyList = AdjacencyStore[Int, String]

      case class InsertionTest(name: String,
                               numberOfUniqueVerticesAfterInsert: Int,
                               edges: Set[Edge[Int, String]])

      val tests = Set(
        InsertionTest("set1", 8,
          edgeSet(
            (10, "e", 11),
            (10, "f", 12),
            (10, "g", 13),
            (13, "h", 14),
            (13, "i", 15),
            (13, "j", 16),
            (20, "k", 16))),
        InsertionTest("set2", 12,
          edgeSet(
            (1, "e", 10),
            (2, "e", 10),
            (3, "f", 10),
            (4, "f", 10),
            (1, "e", 16),
            (2, "e", 16),
            (3, "f", 16),
            (4, "f", 16)))
      )

      tests.foreach(insertionTest => {
        it("should result in expected state after inserting " + insertionTest.name) {
          adjacencyList += AdjacencyStore(insertionTest.edges)
          adjacencyList.order should be(insertionTest.numberOfUniqueVerticesAfterInsert)
        }
      })
    }
  }


}