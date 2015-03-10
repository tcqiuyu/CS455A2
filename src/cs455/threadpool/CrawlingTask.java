package cs455.threadpool;

import cs455.harvester.Crawler;
import cs455.transport.TCPConnection;
import cs455.util.ConfigUtil;
import cs455.util.URLUtil;
import cs455.wireformat.proto.NodeHandoffTask;

import java.io.IOException;

/**
 * Created by Qiu on 3/9/2015.
 */
public class CrawlingTask implements Task {

    private String url;
    private int depth;
    private ConfigUtil configUtil;

    public CrawlingTask(String url, int depth, ConfigUtil configUtil) {
        this.url = url;
        this.depth = depth;
        this.configUtil = configUtil;
    }

    @Override
    public void doTask() {
        if (depth > 5) {
            System.out.println();
            return;
        }

        //URL in local domain
        if (URLUtil.withinDomain(url)) {
            depth++;
            for (String extractedUrl : URLUtil.getInstance().extractUrl(url)) {
                CrawlingTask newTask = new CrawlingTask(extractedUrl, depth, configUtil);
                TaskQueue.getInstance().addTask(newTask);
                System.out.println(extractedUrl);
            }
        } else if (URLUtil.isTargetDomain(url)) {//URL in other target domain
            try {
                TCPConnection connection = configUtil.getConnectionToCrawler(url);
                NodeHandoffTask handoffTask = new NodeHandoffTask()
                connection.sendData();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
