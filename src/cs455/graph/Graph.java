package cs455.graph;

import java.util.HashMap;

/**
 * Created by Qiu on 3/8/15.
 */
public class Graph {

    private static final Graph instance = new Graph();
    private final HashMap<String, Vertex> graph = new HashMap<String, Vertex>();

    private Graph() {
    }

    public static Graph getInstance() {
        return instance;
    }

    public Vertex addVertex(Vertex vertex) {
        synchronized (graph) {
            return graph.put(vertex.toString(), vertex);
        }
    }

    public HashMap<String, Vertex> getGraph() {
        return graph;
    }

    public Vertex getVertex(String url) {
        synchronized (graph) {
            return graph.get(url);
        }
    }

    public void addLink(String srcUrl, String destUrl) {
        try {
            getVertex(destUrl).addInLink(srcUrl);
        } catch (NullPointerException e) {
            Vertex newVert = new Vertex(destUrl);
            addVertex(newVert);
            newVert.addInLink(srcUrl);
        }

        try {
            getVertex(srcUrl).addOutLink(destUrl);
        } catch (NullPointerException e) {
            Vertex newVert = new Vertex(srcUrl);
            addVertex(newVert);
            newVert.addOutLink(destUrl);
        }
    }

}
