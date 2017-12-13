package main.test;

import static org.junit.jupiter.api.Assertions.*;

import main.java.BellmanFord;
import main.java.Edge;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;


class BellmanFordTest {
    BellmanFord bmFord;
    Map<Integer, List<Integer>> edgeMappings;
    Map<Edge, Integer> edgeCosts;
    Map<Integer, List<Integer>> reverseMappings;

    /* Test if the graph data is set up correctly for Johnson's algorithm */
    @Test
    void johnsonSetup() throws FileNotFoundException {
        initializeBMFord("/home/chris/WorkSpace/Java/JohnsonAPSP/src/main/test/testinput/test1.txt");

        assertEquals(edgeMappings.get(1).size(), 1);
        assertEquals(edgeMappings.get(2).size(), 2);

        assertEquals(edgeMappings.get(0).size(), bmFord.getNumVertices());
        for (int i = 1; i <= bmFord.getNumVertices(); i++) {
            Edge edge = new Edge(0, 1);
            assertEquals((int) edgeCosts.get(edge), 0);
        }
    }

    @Test
    void testForNegativeCycle() throws FileNotFoundException {
        initializeBMFord
                ("/home/chris/WorkSpace/Java/JohnsonAPSP/src/main/test/testinput/bellmanford/negativecycle.txt");
        assertFalse(bmFord.calculateShortestPaths(0));
    }

    private void initializeBMFord(String filePath) throws FileNotFoundException {
        this.bmFord = new BellmanFord(filePath, true);
        this.edgeMappings = bmFord.getEdgeMappings();
        this.edgeCosts = bmFord.getEdgeToCost();
        this.reverseMappings = bmFord.getReverseEdgeMappings();
    }


}