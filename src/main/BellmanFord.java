package main;

import java.io.*;
import java.util.*;

public class BellmanFord {
    /* Maps the edges in a graph, tail -> head vertices */
    private Map<Integer, List<Integer>> edgeMappings;
    /* Maps the edges but in reverse, head -> tail vertices */
    private Map<Integer, List<Integer>> reverseEdgeMappings;
    /* Maps an edge to its cost */
    private Map<Edge, Integer> edgeToCost;
    /* weights of each vertex used to reweigh edges for Johnson's algorithm */
    private Map<Integer, Integer> shortestPathLengths;

    /**
     * Constructs a new BellmanFord object using a path string to a graph file
     * @param graphFilePath path to the graph file
     * @param johnson true if using this object as the first step in Johnson's algorithm
     * @throws FileNotFoundException thrown if the file was not found
     */
    public BellmanFord(String graphFilePath, boolean johnson) throws FileNotFoundException {
        File file = new File(graphFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            /* Consume the first line */
            line = br.readLine();
            String[] splitLine = line.split(" ");
            int numVertices = Integer.parseInt(splitLine[0]);
            int numEdges = Integer.parseInt(splitLine[1]);
            this.edgeMappings = new HashMap<>(numVertices);
            this.edgeToCost = new HashMap<>(numEdges);
            this.reverseEdgeMappings = new HashMap<>(numVertices);
            this.shortestPathLengths = new HashMap<>(numVertices);

            /* Populate data structures with graph information */
            while ((line = br.readLine()) != null) {
                splitLine = line.split(" ");
                int tail = Integer.parseInt(splitLine[0]);
                int head = Integer.parseInt(splitLine[1]);
                int cost = Integer.parseInt(splitLine[2]);

                List<Integer> headVertices;
                if (edgeMappings.containsKey(tail)) {
                    headVertices = edgeMappings.get(tail);
                } else {
                    headVertices = new ArrayList<>();
                    edgeMappings.put(tail, headVertices);
                }
                headVertices.add(head);

                List<Integer> tailVertices;
                if (reverseEdgeMappings.containsKey(head)) {
                    tailVertices = reverseEdgeMappings.get(head);
                } else {
                    tailVertices = new ArrayList<>();
                    reverseEdgeMappings.put(head, tailVertices);
                }
                tailVertices.add(tail);
                Edge edge = new Edge(tail, head);
                edgeToCost.put(edge, cost);

                /*
                 * Add vertex 0 if running Johnson's algorithm
                 */
                if (johnson) {
                    List<Integer> additionalEdges = new ArrayList<>(numVertices);
                    for (int i = 1; i <= numVertices; i++) {
                        additionalEdges.add(i);
                        Edge addEdge = new Edge(0, i);
                        edgeToCost.put(addEdge, 0);
                    }
                    edgeMappings.put(0, additionalEdges);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate shortest path lengths from a source vertex, returns false if a negative cycle exists
     * @param source the source vertex
     * @param johnson true if passing the results on to Dijkstra's algorithm, to carry out Johnson's algorithm
     * @return false if a negative cycle exists in the graph, true if otherwise
     */
    public boolean calculateShortestPaths(int source, boolean johnson) {
        int numVertices = this.edgeMappings.size();
        int[][] constrainedPathLengths;
        if (johnson) {
            /* outer array is of length numVertices (numVertices + 1) so that we can run it one more time
            ** to check for negative cycles
            */
            constrainedPathLengths = new int[numVertices + 1][numVertices];
        } else {
            constrainedPathLengths = new int[numVertices + 1][numVertices + 1];
        }
        for (int i = 0; i <= numVertices; i++) {
            if (i == source) {
                constrainedPathLengths[0][i] = 0;
            } else {
                constrainedPathLengths[0][i] = Integer.MAX_VALUE;
            }
        }
        for (int i = 1; i < numVertices; i++) {
            for (int vert = 1; vert <= numVertices; vert++) {
                if (vert == source) {
                    continue;
                }
                /* case where we don't just inherit A[i, v] from A[i - 1, v] */
                int secondCase = Integer.MAX_VALUE;
                for (Integer tailVertex : reverseEdgeMappings.get(vert)) {
                    Edge edge = new Edge(tailVertex, vert);
                    int candidate = constrainedPathLengths[i - 1][tailVertex] + edgeToCost.get(edge);
                    if (candidate < secondCase) {
                        secondCase = candidate;
                    }
                }
                constrainedPathLengths[i][vert] = Math.min(constrainedPathLengths[i - 1][vert], secondCase);
            }
        }
        for (int vert = 1; vert <= numVertices; vert++) {
            if (vert == source) {
                continue;
            }
                /* case where we don't just inherit A[i, v] from A[i - 1, v] */
            int secondCase = Integer.MAX_VALUE;
            for (Integer tailVertex : reverseEdgeMappings.get(vert)) {
                Edge edge = new Edge(tailVertex, vert);
                int candidate = constrainedPathLengths[numVertices - 1][tailVertex] + edgeToCost.get(edge);
                if (candidate < secondCase) {
                    secondCase = candidate;
                }
            }
            if (secondCase < constrainedPathLengths[numVertices][vert]) {
                return false;
            }
        }
        return true;
    }

    public Map<Integer, List<Integer>> getEdgeMappings() {
        return edgeMappings;
    }

    public Map<Edge, Integer> getEdgeToCost() {
        return edgeToCost;
    }

    public Map<Integer, Integer> getShortestPathLengths() {
        return shortestPathLengths;
    }
}
