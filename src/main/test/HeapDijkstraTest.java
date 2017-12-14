package main.test;

import static org.junit.jupiter.api.Assertions.*;

import main.java.Edge;
import main.java.HeapDijkstra;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

class HeapDijkstraTest {
    private Map<Integer, List<Integer>> edgeMappings;
    private Map<Edge, Integer> edgeCosts;
    private HeapDijkstra dijkstra;

    /**
     * Tests whether the HeapDijkstra object is set up properly or not
     */
    @Test
    void testDijkstraSetup() {
        initializeDijkstra(System.getProperty("user.dir")
                + "/src/main/test/testinput/dijkstra/test1.txt");
        assertEquals(this.edgeMappings.get(1).size(), 1);
        assertEquals(this.edgeMappings.get(2).size(), 2);
        assertEquals(this.edgeMappings.get(3).size(), 1);
        assertEquals(this.edgeMappings.get(4).size(), 2);

        Edge edge = new Edge(1, 2);
        assertEquals((int) edgeCosts.get(edge), 1);
        edge = new Edge(4, 2);
        assertEquals((int) edgeCosts.get(edge), 3);
    }

    /**
     * Tests if the correct shortest paths lengths are computed, using
     * test1.txt for the graph and source = 1
     */
    @Test
    void testShortestPathLengths() {
        initializeDijkstra(System.getProperty("user.dir")
                + "/src/main/test/testinput/dijkstra/test1.txt");

        int[] spLengths = dijkstra.calculateShortestPaths(1);
        assertEquals(spLengths[2], 1);
        assertEquals(spLengths[3], 6);
        assertEquals(spLengths[4], 12);
    }

    /**
     * Tests if the correct shortest paths lengths are computed, using
     * test1.txt for the graph and source = 2
     */
    @Test
    void testShortestPathLengthsDiffSource() {
        initializeDijkstra(System.getProperty("user.dir")
                + "/src/main/test/testinput/dijkstra/test1.txt");

        int[] spLengths = dijkstra.calculateShortestPaths(2);
        assertEquals(spLengths[1], 2);
        assertEquals(spLengths[3], 5);
        assertEquals(spLengths[4], 11);
    }

    /**
     * Tests if the correct shortest paths lengths are computed, using
     * test3.txt for the graph and source = 1
     */
    @Test
    void testShortestPathLengthsYetAnotherSource() {
        initializeDijkstra(System.getProperty("user.dir")
                + "/src/main/test/testinput/dijkstra/test3.txt");

        int[] spLengths = dijkstra.calculateShortestPaths(1);
        assertEquals(spLengths[2], 2);
        assertEquals(spLengths[3], 4);
        assertEquals(spLengths[4], 5);
    }

    /**
     * Tests if the correct shortest paths lengths are computed, using
     * test2.txt for the graph and source = 5
     */
    @Test
    void testShortestPathLengthsDiffInput() {
        initializeDijkstra(System.getProperty("user.dir")
                + "/src/main/test/testinput/dijkstra/test2.txt");

        int[] spLengths = dijkstra.calculateShortestPaths(5);
        assertEquals(spLengths[1], 50);
        assertEquals(spLengths[2], 19);
        assertEquals(spLengths[3], 30);
        assertEquals(spLengths[4], 10);
    }


    /**
     * Initializes a new HeapDijkstra object using an input graph file
     * @param filePath the path to the input graph file
     */
    private void initializeDijkstra(String filePath) {
        try {
            dijkstra = new HeapDijkstra(filePath);
        } catch (FileNotFoundException e) {
            fail("file not found...");
        }
        this.edgeCosts = dijkstra.getEdgeToCost();
        this.edgeMappings = dijkstra.getEdgeMappings();
    }
}