package cs455.graph;

import java.util.HashMap;

/**
 * Created by Qiu on 3/8/15.
 */
public class Vertex {

    private String url;

    private HashMap<String, Integer> links = new HashMap<String, Integer>();

    public Vertex(String url) {
        this.url = url;
    }

    public HashMap<String, Integer> getLinks() {
        return links;
    }

    //type 0: in, type 1: out
    protected boolean addLink(String url, int type) {
        Integer out = links.put(url, type);
        return out != null;
    }

    @Override
    public String toString() {
        return url;
    }

}
