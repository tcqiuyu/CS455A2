package cs455.threadpool;

import cs455.graph.Graph;
import cs455.graph.Vertex;
import cs455.transport.TCPConnection;
import cs455.util.ConfigUtil;
import cs455.util.URLUtil;
import cs455.wireformat.proto.NodeHandoffTask;

import java.io.IOException;

/**
 * Created by Qiu on 3/9/2015.
 */
public class CrawlingTask implements Task {

    private String srcUrl;
    private String destUrl;
    private int depth;
    private ConfigUtil configUtil;

    public CrawlingTask(String srcUrl, String destUrl, int depth, ConfigUtil configUtil) {
        this.srcUrl = srcUrl;
        this.destUrl = destUrl;
        this.depth = depth;
        this.configUtil = configUtil;

    }

    @Override
    public void doTask() {
        if (depth > 5) {
            System.out.println("Depth reached");
            return;
        }

        //URL in local domain
        if (URLUtil.withinDomain(destUrl)) {
//            System.out.println(URLUtil.withinDomain("http://www.biology.colostate.edu/"));
            depth++;
//            System.out.println("HERE: Depth = " + depth);
            for (String extractedUrl : URLUtil.getInstance().extractUrl(destUrl)) {
                if (srcUrl != null) {
                    Vertex vertex = new Vertex(destUrl);
                    Graph.getInstance().addVertex(vertex);
//                    Graph.getInstance().

                }
                CrawlingTask newTask = new CrawlingTask(destUrl, extractedUrl, depth, configUtil);
                TaskQueue.getInstance().addTask(newTask);
                System.out.println(destUrl + " ------> " + extractedUrl);
            }
        } else if (URLUtil.isTargetDomain(destUrl)) {//URL in other target domain
            try {
                TCPConnection connection = configUtil.getConnectionToCrawler(destUrl);
                NodeHandoffTask handoffTask = new NodeHandoffTask(srcUrl, destUrl);
                connection.sendData(handoffTask.getBytes());
            } catch (IOException e) {
//                e.printStackTrace();
            }

        }

    }

}
