package cs455.graph;

import java.util.HashSet;

/**
 * Created by Qiu on 3/8/15.
 */
public class Vertex {

    private String url;
    private HashSet<Link> links = new HashSet<Link>();

    public Vertex(String url) {
        this.url = url;
    }

    public boolean addLink(Link link) {
        return links.add(link);
    }

    public HashSet<Link> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return url;
    }
}
