package main;

import java.io.*;
import java.util.*;

public class BellmanFord {
    /* Maps the edges in a graph, tail -> head vertices */
    private Map<Integer, List<Integer>> edgeMappings;
    /**
     *  Maps the edges but in reverse, head -> tail vertices
     *  Need this to find where all inbound edges to a vertex come from,
     *  in the second case of Bellman-Ford
     */
    private Map<Integer, List<Integer>> reverseEdgeMappings;
    /* Maps an edge to its cost */
    private Map<Edge, Integer> edgeToCost;
    /* weights of each vertex used to reweigh edges for Johnson's algorithm */
    private Map<Integer, Integer> shortestPathLengths;
    /* number of vertices */
    private int numVertices;
    /* true if the computation results will be passed on to Dijkstra's algorithm to form
     * Johnson's all pairs shortest paths algorithm
     */
    private boolean johnson;

    /**
     * Constructs a new BellmanFord object using a path string to a graph file
     * @param graphFilePath path to the graph file
     * @param johnson true if using this object as the first step in Johnson's algorithm
     * @throws FileNotFoundException thrown if the file was not found
     */
    public BellmanFord(String graphFilePath, boolean johnson) throws FileNotFoundException {
        this.johnson = johnson;
        File file = new File(graphFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            /* Consume the first line */
            line = br.readLine();
            String[] splitLine = line.split(" ");
            /*
             ** Assume the graph is stored in the format:
             *  tail_vertex head_vertex edge_length
             *
             *  With the first line: number_of_vertices number_of_edges
             */
            numVertices = Integer.parseInt(splitLine[0]);
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
                }
                headVertices.add(head);
                edgeMappings.put(tail, headVertices);

                List<Integer> tailVertices;
                if (reverseEdgeMappings.containsKey(head)) {
                    tailVertices = reverseEdgeMappings.get(head);
                } else {
                    tailVertices = new ArrayList<>();
                }
                tailVertices.add(tail);
                reverseEdgeMappings.put(head, tailVertices);
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
     *
     * @param source the source vertex
     * @return false if a negative cycle exists in the graph, true if otherwise
     */
    public boolean calculateShortestPaths(int source) {
        int[][] constrainedPathLengths;
        /*
         * outer array is of length numVertices (numVertices + 1) so that we can run it one more time
         * to check for negative cycles
         */
        constrainedPathLengths = new int[numVertices][numVertices + 1];

        /* if running Johnson's algorithm, then source will be 0. If not, starting from 0
         * doesn't hurt
         */
        for (int i = 0; i <= numVertices; i++) {
            if (i == source) {
                constrainedPathLengths[0][i] = 0;
            } else {
                constrainedPathLengths[0][i] = Integer.MAX_VALUE;
            }
        }
        for (int i = 1; i < numVertices; i++) {
            for (int vert = 1; vert <= numVertices; vert++) {
                System.out.printf("Bellman-Ford, i = %d, v = %d\n", i, vert);
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
        /*
         * Run one more time to find negative cost cycles
         */
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

            /* if an even shorter path is found on the extra iteration of Bellman-Ford,
            ** the graph contains one or more negative cycles
             */
            /* TODO: Not sure if the outer index should be numVertices - 1 or numVertices */
            if (secondCase < constrainedPathLengths[numVertices - 1][vert]) {
                return false;
            }
        }
        for (int i = 1; i <= numVertices; i++) {
            if (i == source) {
                shortestPathLengths.put(i, 0);
            } else {
                shortestPathLengths.put(i, constrainedPathLengths[numVertices - 1][i]);
            }
        }
        /* Reweigh the edges if Johnson's algorithm is to be carried out */
        if (this.johnson) {
            for (Map.Entry<Edge, Integer> entry : edgeToCost.entrySet()) {
                Edge edge = entry.getKey();
                System.out.println("Head: " + edge.getHead());
                System.out.println("Tail: " + edge.getTail());
                /* Don't need to reweight edges originating from 0 */
                if (edge.getTail() == 0) {
                    continue;
                }
                int newCost = entry.getValue() + shortestPathLengths.get(edge.getHead())
                        - shortestPathLengths.get(edge.getTail());
                edgeToCost.put(edge, newCost);
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

    public boolean getJohnson() {
        return this.johnson;
    }

    public int getNumVertices() {
        return this.numVertices;
    }
}
