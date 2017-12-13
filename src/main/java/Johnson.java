package main.java;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for carrying out Johnson's all pairs shortest paths algorithm
 */
public class Johnson {
    /* maps all pairs shortest path lengths */
    Map<Integer, Integer> allPairsSP;

    public static void main(String[] args) throws FileNotFoundException {
        List<String> graphFiles = Arrays.asList("g1.txt", "g2.txt", "g3.txt");
        int shortestShortest = Integer.MAX_VALUE;

        for (String file : graphFiles) {
            String filePath = "/home/chris/WorkSpace/Java/JohnsonAPSP/" + file;
            BellmanFord  bmFord
                    = new BellmanFord(filePath, true);

            if (!bmFord.calculateShortestPaths(0)) {
                System.out.printf("Negative cycle detected in file %s. Abort Johnson\n", file);
            } else {
                Map<Integer, List<Integer>> edgeMappings = bmFord.getEdgeMappings();
                Map<Edge, Integer> edgeCosts = bmFord.getEdgeToCost();
                Map<Integer, Integer> johnsonWeights = bmFord.getShortestPathLengths();
                int numVertices = bmFord.getNumVertices();

                for (int i = 1; i <= numVertices; i++) {
                    HeapDijkstra dijkstra
                            = new HeapDijkstra(edgeMappings, edgeCosts, johnsonWeights, numVertices);
                    int[] shortestPaths = dijkstra.calculateShortestPaths(i);
                    for (int shortestPath : shortestPaths) {
                        if (shortestPath < shortestShortest) {
                            shortestShortest = shortestPath;
                        }
                    }
                }
            }
        }
        System.out.println("The shortest shortest in all three files is " + shortestShortest);
    }
}
