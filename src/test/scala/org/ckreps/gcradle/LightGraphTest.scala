package org.ckreps.gcradle

import adjacencylist.{Edge, AdjacencyStore}
import adjacencylist.Edge.edgeSet
import org.scalatest.Spec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Assertions._

/**
 * @author ckreps
 */
@RunWith(classOf[JUnitRunner])
class LightGraphTest
  extends Spec
  with ShouldMatchers {

  case class Example(name: String, set: Set[Edge[Int, String]])

  describe("Getting a subgraph") {

    val blogPostGraph = LightGraph(AdjacencyStore(edgeSet(
      (10, "title", 20),
      (10, "date", 30),
      (10, "content", 40),
      (10, "previous", 11),
      (11, "title", 21),
      (11, "date", 31),
      (11, "content", 41),
      (11, "previous", 12),
      (12, "title", 22),
      (12, "date", 32),
      (12, "content", 42))))

    it("Should return None when the pattern starts with an name type not in the Graph.") {

      val pattern = Pattern(Map(1 -> Map("foo" -> Set(2))))

      val subGraph = blogPostGraph.sub(10, pattern)
      assert(subGraph == None, "Subgraph was not None.")
    }

    it("Should work for repeated single name types and mixed name types.") {

      val pattern =
        Pattern(
          Map(
            1 -> Map("previous" -> Set(2)),
            2 -> Map("previous" -> Set(3)),
            3 -> Map(
              "title" -> Set(4),
              "date" -> Set(5),
              "content" -> Set(6)
            )
          ))

      val expected = Map(
        10 -> Map("previous" -> Set(11)),
        11 -> Map("previous" -> Set(12)),
        12 -> Map(
          "title" -> Set(22),
          "date" -> Set(32),
          "content" -> Set(42)),
        22 -> Map(),
        32 -> Map(),
        42 -> Map())

      blogPostGraph.sub(10, pattern) match {
        case None => fail("Should not be none.")
        case Some(subGraph) => {
          subGraph.compress should equal (expected)
        }
      }
    }

    it("Should handle a cycle without blowing up.") {

      val smallLoopGraph = LightGraph(AdjacencyStore(edgeSet(
        (11, "a", 22),
        (22, "a", 33),
        (33, "a", 11),
        (11, "a", 33))))

      val pattern =
        Pattern(
          Map(
            1 -> Map("a" -> Set(2,3)),
            2 -> Map("a" -> Set(3)),
            3 -> Map("a" -> Set(1))))

      val expected = Map(
        11 -> Map("a" -> Set(22,33)),
        22 -> Map("a" -> Set(33)),
        33 -> Map("a" -> Set(11)))

      smallLoopGraph.sub(11, pattern) match {
        case None => fail("Should not be none.")
        case Some(subGraph) => {
          subGraph.compress should equal (expected)
        }
      }
    }

    it("Should be possible to get a subgraph from a subgraph.") {

      val rootGraph = LightGraph(AdjacencyStore(edgeSet(
        (333, "a", 444),
        (444, "a", 555),
        (555, "a", 666),
        (666, "a", 777))))

      val rootPattern =
        Pattern(
          Map(
            1 -> Map("a" -> Set(2)),
            2 -> Map("a" -> Set(3)),
            3 -> Map("a" -> Set(4))))

      val subgraphPattern =
        Pattern(
          Map(
            1 -> Map("a" -> Set(2)),
            2 -> Map("a" -> Set(3))))


      val expected = Map(
        333 -> Map("a" -> Set(444)),
        444 -> Map("a" -> Set(555)),
        555 -> Map())

      rootGraph
        .sub(333, rootPattern)
        .map(_.sub(333, subgraphPattern) match {
        case None => fail("Should not be none.")
        case Some(subGraph) => {
          subGraph.compress should equal (expected)
        }
      })
    }
  }

  describe("Getting a union") {

    it("Union resulting in a disconnected graph should throw an exception.") {

      val gA = LightGraph(AdjacencyStore(edgeSet(
        (333, "a", 444),
        (444, "a", 555))))

      val gB = LightGraph(AdjacencyStore(edgeSet(
        (999, "a", 101010),
        (101010, "a", 111111))))

      evaluating {gA union gB} should produce [Throwable]
    }

    it("Union resulting in a graph connected with all different edges should succeed.") {

      val gA = LightGraph(AdjacencyStore(edgeSet(
        (11, "a", 22),
        (22, "b", 33),
        (33, "c", 44))))

      val gB = LightGraph(AdjacencyStore(edgeSet(
        (44, "d", 55),
        (55, "e", 66),
        (66, "f", 77))))

      val expected = Map(
        11 -> Map("a" -> Set(22)),
        22 -> Map("b" -> Set(33)),
        33 -> Map("c" -> Set(44)),
        44 -> Map("d" -> Set(55)),
        55 -> Map("e" -> Set(66)),
        66 -> Map("f" -> Set(77)),
        77 -> Map())

      val graphUnion = gA union gB
      graphUnion.compress should equal (expected)
    }

    it("Union whose connection is via two tails should succeed.") {

      val gA = LightGraph(AdjacencyStore(edgeSet(
        (11, "a", 22))))

      val gB = LightGraph(AdjacencyStore(edgeSet(
        (33, "d", 22))))

      val expected = Map(
        11 -> Map("a" -> Set(22)),
        33 -> Map("d" -> Set(22)),
        22 -> Map())

      val graphUnion = gA union gB
      graphUnion.compress should equal (expected)
    }
  }
}
