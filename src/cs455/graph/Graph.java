package cs455.graph;

import cs455.harvester.Crawler;
import cs455.util.URLUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Qiu on 3/8/15.
 */
public class Graph {

    private static final Graph instance = new Graph();
    private final HashMap<String, Vertex> graph = new HashMap<String, Vertex>();
    Set<String> vertices = this.getGraph().keySet();

    private Graph() {
    }

    public static Graph getInstance() {
        return instance;
    }

    public boolean addVertex(Vertex vertex) {
        synchronized (graph) {
            if (graph.containsKey(vertex.toString())) {
                return false;
            } else {
                graph.put(vertex.toString(), vertex);
                return true;
            }
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

    public synchronized void addLink(String srcUrl, String destUrl, int depth) {

        try {
            getVertex(destUrl).addInLink(srcUrl);
        } catch (NullPointerException e) {
            Vertex newVert = new Vertex(destUrl, depth + 1);
//            System.out.println("Dest Node " + destUrl + " does not exist: creating one...   Depth is " + (depth + 1));
            addVertex(newVert);
            newVert.addInLink(srcUrl);
        }

        try {
            getVertex(srcUrl).addOutLink(destUrl);
        } catch (NullPointerException e) {
            Vertex newVert = new Vertex(srcUrl, depth);
//            System.out.println("Source Node " + srcUrl + " does not exist: creating one...   Depth is " + depth);
            addVertex(newVert);
            newVert.addOutLink(destUrl);
        }
    }

    public Set<String> getVertices() {
        return graph.keySet();
    }

//    public synchronized Set<String> getDisjointSubGraph(Set<String> vertexSet, String vertexStr, Set<String> output) {
//        Vertex vertex = graph.get(vertexStr);
//        if (vertex != null) {
//            output.add(vertexStr);
//            vertexSet.remove(vertexStr);
//
//            HashSet<String> inLinks = vertex.getInLinks();
//            if (!inLinks.isEmpty()) {
//                for (String inVertex : inLinks) {
//                    if (!inVertex.equals(Crawler.getRootUrl())) {
//                        output.addAll(getDisjointSubGraph(vertexSet, inVertex, output));
//                    }
//                }
//            }
//            HashSet<String> outLinks = vertex.getOutLinks();
//            if (outLinks.isEmpty()) {
//                return output;
//            } else {
//
//                for (String outVertex : outLinks) {
//                    if (!outVertex.equals(Crawler.getRootUrl())) {
//                        output.addAll(getDisjointSubGraph(vertexSet, outVertex, output));
//                    }
//                }
//            }
//
//        }
//        return output;
//    }
}
