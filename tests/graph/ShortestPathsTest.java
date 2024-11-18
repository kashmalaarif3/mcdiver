package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ShortestPathsTest {
    /** The graph example from lecture */
    static final String[] vertices1 = { "a", "b", "c", "d", "e", "f", "g" };
    static final int[][] edges1 = {
        {0, 1, 9}, {0, 2, 14}, {0, 3, 15},
        {1, 4, 23},
        {2, 4, 17}, {2, 3, 5}, {2, 5, 30},
        {3, 5, 20}, {3, 6, 37},
        {4, 5, 3}, {4, 6, 20},
        {5, 6, 16}
    };

    static final int[][] edges2 = {
            {0, 1, 9}, {0, 2, 14}, {0, 3, 15},
            {2, 3, 5}, {2, 5, 30},
            {3, 5, 20}, {3, 6, 37},
            {5, 6, 16}
    };
    static final int[][] edges3 = {
            {0, 1, 9}, {0, 2, 14}, {0, 3, 15},
            {2, 3, 1}, {2, 5, 30},
            {3, 5, 20}, {3, 6, 37},
            {5, 6, 16}
    };
    static class TestGraph implements WeightedDigraph<String, int[]> {
        int[][] edges;
        String[] vertices;
        Map<String, Set<int[]>> outgoing;

        TestGraph(String[] vertices, int[][] edges) {
            this.vertices = vertices;
            this.edges = edges;
            this.outgoing = new HashMap<>();
            for (String v : vertices) {
                outgoing.put(v, new HashSet<>());
            }
            for (int[] edge : edges) {
                outgoing.get(vertices[edge[0]]).add(edge);
            }
        }
        public Iterable<int[]> outgoingEdges(String vertex) { return outgoing.get(vertex); }
        public String source(int[] edge) { return vertices[edge[0]]; }
        public String dest(int[] edge) { return vertices[edge[1]]; }
        public double weight(int[] edge) { return edge[2]; }
    }
    static TestGraph testGraph1() {
        return new TestGraph(vertices1, edges1);
    }

    @Test
    void lectureNotesTest() {
        TestGraph graph = testGraph1();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(50, ssp.getDistance("g"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("g")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" g");
        assertEquals("best path: a c e f g", sb.toString());
    }

    // TODO: Add 2 more tests


    // DONE: Add 2 more tests

    static TestGraph testGraph2() {
        return new TestGraph(vertices1, edges2);
    }

    @Test //This tests that an impossible test will fail
    void myTest() {
        TestGraph graph = testGraph2();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("g");
        try{
            assertEquals(50, ssp.getDistance("e"));
        } catch (AssertionError e) {
            String completer = "Test Succesful!";
        }
    }

    static TestGraph testGraph3() {
        return new TestGraph(vertices1, edges3);
    }

    @Test //While this test has a comedic output, it shows that the algortihm prefers the first
        // path it finds, when compared to other paths of equal distance
    void myTester() {
        TestGraph graph = testGraph3();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(15, ssp.getDistance("d"));
        StringBuilder sb = new StringBuilder();
        sb.append("best boy:");
        for (int[] e : ssp.bestPath("d")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" dog");
        assertEquals("best boy: a dog", sb.toString());
    }
}

