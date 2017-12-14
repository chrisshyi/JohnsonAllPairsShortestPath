package main.test;

import static org.junit.jupiter.api.Assertions.*;

import main.java.BellmanFord;
import main.java.Edge;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;


class BellmanFordTest {
    private BellmanFord bmFord;
    private Map<Integer, List<Integer>> edgeMappings;
    private Map<Edge, Integer> edgeCosts;
    private Map<Integer, List<Integer>> reverseMappings;

    /**
     *  Tests if the graph data is set up correctly for Johnson's algorithm
     */
    @Test
    void johnsonSetup() {
        initializeBMFord(System.getProperty("user.dir") + "/src/main/test/testinput/bellmanford/test1.txt");

        assertEquals(edgeMappings.get(1).size(), 1);
        assertEquals(edgeMappings.get(2).size(), 2);

        assertEquals(edgeMappings.get(0).size(), bmFord.getNumVertices());
        for (int i = 1; i <= bmFord.getNumVertices(); i++) {
            Edge edge = new Edge(0, 1);
            assertEquals((int) edgeCosts.get(edge), 0);
        }
    }

    /**
     * Tests if the algorithm correctly recognizes the presence of a negative cycle in the input graph
     */
    @Test
    void testForNegativeCycle() {
        initializeBMFord
                (System.getProperty("user.dir") + "/src/main/test/testinput/bellmanford/negativecycle.txt");
        assertFalse(bmFord.calculateShortestPaths(0));
    }

    /**
     * Tests if the algorithm correctly recognizes the absence of a negative cycle in the input graph
     */
    @Test
    void testForNoNegativeCycle() {
        initializeBMFord(System.getProperty("user.dir") + "/src/main/test/testinput/bellmanford/test1.txt");
        assertTrue(bmFord.calculateShortestPaths(0));
    }

    /**
     * Tests if correct shortest paths are computed using test1.txt as input
     */
    @Test
    void testShortestPathLengths() {
        initializeBMFord(System.getProperty("user.dir") + "/src/main/test/testinput/bellmanford/test1.txt");
        bmFord.calculateShortestPaths(0);
        Map<Integer, Integer> shortestPathLengths = bmFord.getShortestPathLengths();
        assertEquals((int) shortestPathLengths.get(1), -1);
        assertEquals((int) shortestPathLengths.get(2), -3);
        assertEquals((int) shortestPathLengths.get(3), 0);
        assertEquals((int) shortestPathLengths.get(4), 0);
    }

    /* Tests if correct shortest paths are computed using test2.txt as input */
    @Test
    void testShortestPathLengthsTwo() {
        initializeBMFord(System.getProperty("user.dir") + "/src/main/test/testinput/bellmanford/test2.txt");
        bmFord.calculateShortestPaths(0);
        Map<Integer, Integer> shortestPathLengths = bmFord.getShortestPathLengths();
        assertEquals((int) shortestPathLengths.get(1), 0);
        assertEquals((int) shortestPathLengths.get(2), -3);
        assertEquals((int) shortestPathLengths.get(3), 0);
        assertEquals((int) shortestPathLengths.get(4), -10);
        assertEquals((int) shortestPathLengths.get(5), 0);
    }

    /**
     * Initializes a new BellmanFord object using an input graph
     * @param filePath the path to the input graph file
     */
    private void initializeBMFord(String filePath) {
        try {
            this.bmFord = new BellmanFord(filePath, true);
        } catch (FileNotFoundException e) {
            fail("File not found...");
        }
        this.edgeMappings = bmFord.getEdgeMappings();
        this.edgeCosts = bmFord.getEdgeToCost();
        this.reverseMappings = bmFord.getReverseEdgeMappings();
    }


}