package cs455.util;

import cs455.harvester.Crawler;
import cs455.wireformat.Event;

/**
 * Created by Qiu on 3/9/2015.
 */
public class EventHandler {
    private Crawler crawler;

    public EventHandler(Crawler crawler) {
        this.crawler = crawler;
    }

    public void handleHandoffTask(Event event) {

    }

    public void handleStatusReport(Event event) {

    }
}
