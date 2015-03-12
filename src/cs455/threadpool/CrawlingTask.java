package cs455.threadpool;

import cs455.graph.Graph;
import cs455.graph.Vertex;
import cs455.harvester.Crawler;
import cs455.transport.TCPConnection;
import cs455.util.ConfigUtil;
import cs455.util.URLUtil;
import cs455.wireformat.proto.NodeHandoffTask;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Qiu on 3/9/2015.
 */
public class CrawlingTask implements Task {

    private String srcUrl;
    private String destUrl;
    private int depth;
    private ConfigUtil configUtil;
    private ThreadPoolManager threadPoolManager;

    public CrawlingTask(String srcUrl, String destUrl, int depth, ThreadPoolManager threadPoolManager, ConfigUtil configUtil) {
        this.srcUrl = srcUrl;
        this.destUrl = destUrl;
        this.depth = depth;
        this.configUtil = configUtil;
        this.threadPoolManager = threadPoolManager;
    }

    @Override
    public void doTask() {
        if (depth > Crawler.MAX_DEPTH) {
            System.out.println("Depth reached");
            Iterator<String> iterator = Graph.getInstance().getGraph().keySet().iterator();
            for (Map.Entry<String, Vertex> entry : Graph.getInstance().getGraph().entrySet()) {
                Set<String> inLinks = entry.getValue().getInLinks();
                Set<String> outLinks = entry.getValue().getOutLinks();
                System.out.println(entry.getKey() + " --------> " + "inlinks: " + inLinks.size() + " outlinks: " + outLinks.size());
            }
            threadPoolManager.shutdown();
            return;
        }

        //URL in local domain
        if (URLUtil.withinDomain(destUrl)) {
//            System.out.println(URLUtil.withinDomain("http://www.biology.colostate.edu/"));
            System.out.println("Current depth: -------->" + depth);
            depth++;
            Set<String> extractedUrls = URLUtil.getInstance().extractUrl(destUrl);
            if (extractedUrls != null) {
                for (String extractedUrl : extractedUrls) {
//                    if (srcUrl != null) {
                    if (!Graph.getInstance().getGraph().containsKey(destUrl)) {
                        Vertex vertex = new Vertex(destUrl);
                        Graph.getInstance().addVertex(vertex);
//                        }
                    }
                    CrawlingTask newTask = new CrawlingTask(destUrl, extractedUrl, depth, threadPoolManager, configUtil);
                    TaskQueue.getInstance().addTask(newTask);
                    System.out.println(destUrl + " ------> " + extractedUrl);
                    System.out.println("Task queue empty??------>"+TaskQueue.getInstance().getTaskCount());

                }

            }
        } else if (URLUtil.isTargetDomain(destUrl)) {//URL in other target domain
            try {
                TCPConnection connection = configUtil.getConnectionToCrawler(destUrl);
                NodeHandoffTask handoffTask = new NodeHandoffTask(srcUrl, destUrl);
                connection.sendData(handoffTask.getBytes());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    @Override
    public String toString() {
        return " from " + srcUrl + " ------> " + destUrl;
    }

}
