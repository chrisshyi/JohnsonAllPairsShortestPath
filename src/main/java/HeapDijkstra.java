package main.java;

import java.io.*;
import java.util.*;

/**
 * Implements Dijkstra's Shortest Path Algorithm using a min heap
 */
public class HeapDijkstra {
    /* Maps the edges in a graph, tail -> head vertices */
    private Map<Integer, List<Integer>> edgeMappings;
    /* Maps an edge to its cost */
    private Map<Edge, Integer> edgeToCost;
    /* MinHeap where the magic takes place */
    private PriorityQueue<DijkVertex> heap;
    /* Shortest path length information to each vertex */
    private int[] shortestPathLengths;
    /* weights for each vertex that were used to reweigh edges for Johnson's algorithm */
    private Map<Integer, Integer> johnsonWeights;

    /**
     * Initializes a HeapDijkstra object from a graph file, use this constructor
     * if doing a standalone Dijkstra computation
     * @param graphFile the path string to the graph adjacency list file
     * @throws FileNotFoundException thrown if the graph file doesn't exist
     */
    public HeapDijkstra(String graphFile) throws FileNotFoundException {
        File file = new File(graphFile);
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
            int numVertices = Integer.parseInt(splitLine[0]);
            int numEdges = Integer.parseInt(splitLine[1]);
            edgeMappings = new HashMap<>(numVertices);
            edgeToCost = new HashMap<>(numEdges);
            heap = new PriorityQueue<>(numVertices);
            /* + 1 because we're not using the 0th index, starting at 1 instead (to avoid confusion) */
            shortestPathLengths = new int[numVertices + 1];

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
                Edge edge = new Edge(tail, head);
                edgeToCost.put(edge, cost);

                int tailScore = Integer.MAX_VALUE;
                int headScore = Integer.MAX_VALUE;

//                if (tail == source) {
//                    tailScore = 0;
//                }
//                if (head == source) {
//                    headScore = 0;
//                }
                DijkVertex tailVertex = new DijkVertex(tail, tailScore);
                DijkVertex headVertex = new DijkVertex(head, headScore);

                heap.add(tailVertex);
                heap.add(headVertex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a HeapDijkstra object with the mappings passed in,
     * use this constructor if using Dijkstra in conjunction with the
     * Bellman-Ford algorithm to form Johnson's algorithm
     * @param edgeMappings input graph represented as an adjacency list
     * @param edgeToCost mapping edges to their cost
     * @param numVertices number of vertices in the graph
     */
    public HeapDijkstra(Map<Integer, List<Integer>> edgeMappings
            , Map<Edge, Integer> edgeToCost, Map<Integer, Integer> johnsonWeights, int numVertices) {
        this.edgeMappings = edgeMappings;
        this.edgeToCost = edgeToCost;
        /* + 1 because we're not using the 0th index */
        this.shortestPathLengths = new int[numVertices + 1];
        this.johnsonWeights = johnsonWeights;
        this.heap = new PriorityQueue<>();

        /* key 0 is an artifact of Bellman-Ford, remove it */
        edgeMappings.remove(0);

        /* Initialize the heap */
        for (int i = 1; i <= numVertices; i++) {
            heap.add(new DijkVertex(i, Integer.MAX_VALUE));
        }
    }

    /**
     * Populates the shortestPathLengths array with shortest paths
     * @param source the designated source vertex
     */
    public int[] calculateShortestPaths(int source) {
        DijkVertex sourceVertex = new DijkVertex(source, 0);
        /* The source now has a Dijkstra score of +infinity, remove and reinsert with
        ** score of zero
         */
        heap.remove(sourceVertex);
        heap.add(sourceVertex);
        /* Main loop of Dijkstra's shortest path algorithm */
        while (heap.size() != 0) {
            DijkVertex minVertex = heap.poll();
            System.out.printf("Dijkstra, source: %d, minVertex: %d\n", source, minVertex.getVertex());
            shortestPathLengths[minVertex.vertex] = minVertex.dijkScore;
            for (Integer connectedVertex : edgeMappings.get(minVertex.vertex)) {
                DijkVertex dijkVertex = new DijkVertex(connectedVertex, Integer.MAX_VALUE);
                /* If the heap still contains the connected vertex, then its shortest path hasn't been
                ** calculated yet. Remove and reinsert to update its Dijkstra greedy score
                 */
                if (heap.contains(dijkVertex)) {
                    Edge edge = new Edge(minVertex.vertex, connectedVertex);
                    int edgeCost = edgeToCost.get(edge);
                    heap.remove(dijkVertex);
                    dijkVertex.setDijkScore(edgeCost);
                    heap.add(dijkVertex);
                }
            }
        }

        /* The calculated shortest path lengths are offset by p(source) - p(dest), subtract this quantity
        ** to get the real shortest path length
         */
        for (int i = 1; i < shortestPathLengths.length; i++) {
            int shortestPathLength = shortestPathLengths[i];
            shortestPathLengths[i] = shortestPathLength - (johnsonWeights.get(source) - johnsonWeights.get(i));
        }
        return this.shortestPathLengths;
    }
    /**
     * Combines a vertex with its Dijkstra greedy score
     */
    class DijkVertex implements Comparable<DijkVertex>{
        int vertex;
        int dijkScore;

        DijkVertex(int vertex, int dijkScore) {
            this.vertex = vertex;
            this.dijkScore = dijkScore;
        }

        public int getVertex() {
            return vertex;
        }

        public void setVertex(int vertex) {
            this.vertex = vertex;
        }

        public int getDijkScore() {
            return dijkScore;
        }

        public void setDijkScore(int dijkScore) {
            this.dijkScore = dijkScore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DijkVertex that = (DijkVertex) o;

            /*
             * We only care about the vertex for equality, not the Dijkstra greedy score
             */
            return vertex == that.vertex;
        }

        @Override
        public int hashCode() {
            /*
            ** Again, we only care about the vertex
             */
            return vertex;
        }

        @Override
        public int compareTo(DijkVertex vertex) {
            return Integer.compare(this.dijkScore, vertex.dijkScore);
        }
    }
}
