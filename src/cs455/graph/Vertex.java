package cs455.graph;

import java.util.HashSet;

/**
 * Created by Qiu on 3/8/15.
 */
public class Vertex {

    private String url;

    private final HashSet<String> inLinks = new HashSet<String>();
    private final HashSet<String> outLinks = new HashSet<String>();

    public Vertex(String url) {
        this.url = url;
    }

    public HashSet<String> getInLinks() {
        return inLinks;
    }

    public HashSet<String> getOutLinks() {
        return outLinks;
    }

    protected boolean addInLink(String inUrl) {
        synchronized (inLinks) {
            return inLinks.add(inUrl);
        }
    }

    protected synchronized boolean addOutLink(String outUrl) {
        synchronized (outLinks) {
            return outLinks.add(outUrl);
        }
    }

    @Override
    public String toString() {
        return url;
    }

}
