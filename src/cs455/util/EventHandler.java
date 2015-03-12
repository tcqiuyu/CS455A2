package cs455.util;

import cs455.harvester.Crawler;
import cs455.harvester.Node;
import cs455.threadpool.CrawlingTask;
import cs455.threadpool.TaskQueue;
import cs455.wireformat.Event;
import cs455.wireformat.proto.NodeHandoffTask;

/**
 * Created by Qiu on 3/9/2015.
 */
public class EventHandler {
    private Crawler crawler;
    private ConfigUtil configUtil;

    public EventHandler(Crawler crawler, ConfigUtil configUtil) {
        this.crawler = crawler;
        this.configUtil = configUtil;
    }

    public void handleHandoffTask(Event event) {
        NodeHandoffTask handoffTask = (NodeHandoffTask) event;
        String srcUrl = handoffTask.getSrcURL();
        String destUrl = handoffTask.getDestURL();
        CrawlingTask newTask = new CrawlingTask(srcUrl, destUrl, 0, crawler.getThreadPoolManager(), configUtil);
        TaskQueue.getInstance().addTask(newTask);

    }

    public void handleStatusReport(Event event) {

    }
}
