package cs455.harvester.graph;

/**
 * Created by Qiu on 3/8/15.
 */
public class Link {

    private Vertex vertex;

    public Link(Vertex vertex) {
        this.vertex = vertex;
    }

    public Vertex getLink() {
        return vertex;
    }

    @Override
    public String toString() {
        return vertex.toString();
    }
}
