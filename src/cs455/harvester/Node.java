package cs455.harvester;

import cs455.wireformat.Event;

/**
 * Created by Qiu on 3/9/2015.
 */
public interface Node {

    public void onEvent(Event event, String srcAddr);
}
