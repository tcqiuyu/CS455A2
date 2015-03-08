package cs455.harvester.graph;

import java.util.HashMap;

/**
 * Created by Qiu on 3/8/15.
 */
public class Graph {

    private static final Graph instance = new Graph();
    private HashMap<String, Vertex> graph = new HashMap<String, Vertex>();

    private Graph() {
    }

    public static Graph getInstance() {
        return instance;
    }

    public Vertex addVertex(Vertex vertex) {
        return graph.put(vertex.toString(), vertex);
    }

    public HashMap<String, Vertex> getGraph() {
        return graph;
    }


}
