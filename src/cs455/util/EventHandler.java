package cs455.util;

import cs455.harvester.Crawler;
import cs455.threadpool.CrawlingTask;
import cs455.wireformat.Event;
import cs455.wireformat.proto.NodeHandoffTask;
import cs455.wireformat.proto.NodeReportStatus;

/**
 * Created by Qiu on 3/9/2015.
 */
public class EventHandler {
    private Crawler crawler;
    private ConfigUtil configUtil;

    public EventHandler(ConfigUtil configUtil, Crawler crawler) {
        this.configUtil = configUtil;
        this.crawler = crawler;
    }

    public void handleHandoffTask(Event event) {
        System.out.println("-----------------------------Received hand off task-----------------------");
        NodeHandoffTask handoffTask = (NodeHandoffTask) event;
        String srcUrl = handoffTask.getSrcURL();
        String destUrl = handoffTask.getDestURL();
        CrawlingTask newTask = new CrawlingTask(srcUrl, destUrl, 0, configUtil, true, crawler);
        crawler.getThreadPoolManager().addTask(newTask);

    }

    public void handleStatusReport(Event event) {
        System.out.println("-----------------------------Received status report-----------------------");

        NodeReportStatus statusReport = (NodeReportStatus) event;
        switch (statusReport.getStatus()) {
            case NodeReportStatus.CRAWLER_REPORT_FINISHED:
                crawler.incrementfinishedCrawlersCount();
            case NodeReportStatus.CRAWLER_REPORT_UNFINISHED:
                crawler.decrementfinishedCrawlersCount();
        }

    }
}
