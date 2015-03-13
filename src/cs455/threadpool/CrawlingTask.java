package cs455.threadpool;

import cs455.harvester.Crawler;
import cs455.util.ConfigUtil;
import cs455.util.TransportUtil;
import cs455.util.URLUtil;
import cs455.wireformat.proto.NodeHandoffTask;
import cs455.wireformat.proto.NodeReportHandoffTaskFinished;

import java.io.IOException;
import java.util.Set;

/**
 * Created by Qiu on 3/9/2015.
 */
public class CrawlingTask implements Task {

    private String srcUrl;
    private String destUrl;
    private int depth;
    private boolean isRelayed;

    private ConfigUtil configUtil;
    private Crawler crawler;
    private ThreadPoolManager threadPoolManager;

    public CrawlingTask(String srcUrl, String destUrl, int depth, ConfigUtil configUtil, boolean isRelayed, Crawler crawler) {
        this.srcUrl = srcUrl;
        this.destUrl = destUrl;
        this.depth = depth;
        this.configUtil = configUtil;
        this.isRelayed = isRelayed;
        this.crawler = crawler;
        threadPoolManager = crawler.getThreadPoolManager();
    }

    @Override
    public void doTask() {
//        if (depth > Crawler.MAX_DEPTH) {
//            return;
//        }

        //URL in local domain
        if (URLUtil.withinDomain(destUrl)) {
            Set<String> extractedUrls = URLUtil.getInstance().extractUrl(srcUrl, destUrl, depth);
            depth++;
            if (extractedUrls != null) {
                for (String extractedUrl : extractedUrls) {
//                    if (!Graph.getInstance().getGraph().containsKey(destUrl)) {
//                        Vertex vertex = new Vertex(destUrl);
//                        Graph.getInstance().addVertex(vertex);
//                    }
                    CrawlingTask newTask = new CrawlingTask(destUrl, extractedUrl, depth, configUtil, false, crawler);
                    threadPoolManager.addTask(newTask);
//                    System.out.println(destUrl + " ------> " + extractedUrl);
                }
            } else {
                return;
            }
        } else if (URLUtil.isTargetDomain(destUrl)) {//URL in other target domain
            try {
                NodeHandoffTask handoffTask = new NodeHandoffTask(srcUrl, destUrl);
                System.out.println("Sending relayed message to:" + destUrl);
                TransportUtil.sendMessage(URLUtil.getDomain(destUrl), handoffTask);
                crawler.incrementRelayMessageCount();
            } catch (IOException e) {
                System.out.println("Error when sending relayed message......");
                System.out.println(e.getMessage());
//                System.out.println(e.getMessage());
            }

        }

        if (isRelayed) {
            NodeReportHandoffTaskFinished taskFinished = new NodeReportHandoffTaskFinished();
            try {
                TransportUtil.sendMessage(URLUtil.getDomain(srcUrl), taskFinished);
            } catch (IOException e) {
                System.out.println(e.getMessage());
//                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return " from " + srcUrl + " ------> " + destUrl;
    }

}
