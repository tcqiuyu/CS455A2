package cs455.graph;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Qiu on 3/8/15.
 */
public class Graph {

    private static final Graph instance = new Graph();
    private ConcurrentHashMap<String, Vertex> graph = new ConcurrentHashMap<String, Vertex>();

    private Graph() {
    }

    public static Graph getInstance() {
        return instance;
    }

    //REVIEW: need synchronize?
    public Vertex addVertex(Vertex vertex) {
        return graph.put(vertex.toString(), vertex);
    }

    public ConcurrentHashMap<String, Vertex> getGraph() {
        return graph;
    }

    public boolean containsURL(String url) {
        return graph.containsKey(url);
    }


}
