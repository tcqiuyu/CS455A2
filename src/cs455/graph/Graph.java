package cs455.graph;

import cs455.harvester.Crawler;

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

    public synchronized void addLink(String srcUrl, String destUrl, int depth) {
        try {
            getVertex(destUrl).addInLink(srcUrl);
        } catch (NullPointerException e) {
            Vertex newVert = new Vertex(destUrl, depth + 1);
            System.out.println("Dest Node " + destUrl + " does not exist: creating one...   Depth is " + (depth + 1));
            addVertex(newVert);
            newVert.addInLink(srcUrl);
        }

        try {
            getVertex(srcUrl).addOutLink(destUrl);
        } catch (NullPointerException e) {
            Vertex newVert = new Vertex(srcUrl, depth);
            System.out.println("Source Node " + srcUrl + " does not exist: creating one...   Depth is " + depth);
            addVertex(newVert);
            newVert.addOutLink(destUrl);
        }
    }

    public Set<String> getVertices() {
        return graph.keySet();
    }



    public synchronized Set<Set<String>> getDisjointSubGraphSet() {
        Set<Set<String>> output = new HashSet<Set<String>>();
        vertices.remove(Crawler.getRootUrl());

        while (vertices.size() != 0) {
            for (String vertexStr : vertices) {
                Set<String> singleOutput = new HashSet<String>();
                getDisjointSubGraph(vertexStr, singleOutput);
//                System.out.println("Subgraph size---------------->" + singleOutput.size());
                output.add(singleOutput);
            }

        }
        System.out.println("OUTPUT SIZE-------------->" + output.size());
        return output;
    }

    private synchronized Set<String> getDisjointSubGraph(String vertexStr, Set<String> output) {


        Vertex vertex = graph.get(vertexStr);
        if (vertex != null) {
            output.add(vertexStr);
            vertices.remove(vertexStr);
            HashSet<String> outLinks = vertex.getOutLinks();
            if (outLinks.isEmpty() || vertex.getDepth() == Crawler.MAX_DEPTH) {
                return output;
            } else {
                vertices.removeAll(outLinks);
//                output.addAll(outLinks);
                for (String outVertices : outLinks) {
                    output.addAll(getDisjointSubGraph(outVertices, output));
                }
            }
        }
        return output;
    }
}
