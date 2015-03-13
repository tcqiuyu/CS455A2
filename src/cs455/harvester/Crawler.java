package cs455.harvester;

import cs455.graph.Graph;
import cs455.graph.Vertex;
import cs455.threadpool.CrawlingTask;
import cs455.threadpool.ThreadPoolManager;
import cs455.transport.ConnectionFactory;
import cs455.transport.TCPServerThread;
import cs455.util.ConfigUtil;
import cs455.util.EventHandler;
import cs455.util.FileProcessor;
import cs455.util.URLUtil;
import cs455.wireformat.Event;
import cs455.wireformat.Protocol;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Set;

/**
 * Created by Qiu on 3/7/15.
 */
public class Crawler implements Node {

    public static final int MAX_DEPTH = 5;
    private static String rootURL;

    private int port;
    private int poolSize;
    private String confPath;
    private TCPServerThread serverThread;

    private ConfigUtil configUtil;
    private ThreadPoolManager threadPoolManager;
    private EventHandler eventHandler;
    private volatile int relayMessageCount = 0;
    private volatile int finishedCrawlersCount = 0;

    public Crawler(int port, int poolSize, String confPath) {
        this.port = port;
        this.poolSize = poolSize;
        this.confPath = confPath;
    }

    public static String getRootUrl() {
        return rootURL;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: java cs455.harvester.Crawler portnum thread-pool-size root-url path-to-config-file");
        }

        int port = Integer.parseInt(args[0]);
        int poolSize = Integer.parseInt(args[1]);
        String root = args[2];
        String confPath = args[3];
        rootURL = URLUtil.getInstance().resolveRedirects(root);
        Crawler crawler = new Crawler(port, poolSize, confPath);

        crawler.init();

    }

    public synchronized void incrementRelayMessageCount() {
        relayMessageCount++;
    }

    public synchronized void decrementRelayMessageCount() {
        relayMessageCount--;
    }

    public synchronized void incrementfinishedCrawlersCount() {
        finishedCrawlersCount++;
    }

    public synchronized void decrementfinishedCrawlersCount() {
        finishedCrawlersCount--;
    }

    public synchronized int getFinishedCrawlersCount() {
        return finishedCrawlersCount;
    }

    public synchronized int getRelayMessageCount() {
        return relayMessageCount;
    }

    public boolean isFinished() {
        return getRelayMessageCount() == 0 && getFinishedCrawlersCount() == ConfigUtil.getCrawlerCount();
    }

    public void init() {
        serverThread = new TCPServerThread(this, port);
        configUtil = new ConfigUtil(confPath, this);

        serverThread.start();
        try {
            Thread.sleep(1000);
            System.out.println("Main Thread Starts...");
            initConnection();

        } catch (IOException e) {
        } catch (InterruptedException e) {
        }

        threadPoolManager = new ThreadPoolManager(poolSize, this);
        eventHandler = new EventHandler(configUtil, this);

        CrawlingTask initTask = new CrawlingTask(null, rootURL, 1, configUtil, false, this);
//        TaskQueue.getInstance().addTask(initTask);
        threadPoolManager.addTask(initTask);

        URLUtil.getInstance().addProcessedUrl(rootURL);

        Vertex root = new Vertex(rootURL, 1);
        Graph.getInstance().addVertex(root);
        threadPoolManager.init();
    }

    private void initConnection() throws IOException {
        Collection<String> crawlerInfos = ConfigUtil.getCrawlerMap().values();
        for (String crawlerInfo : crawlerInfos) {
            String hostname = crawlerInfo.split(":")[0];
            int port = Integer.parseInt(crawlerInfo.split(":")[1]);
            ConnectionFactory.getInstance().getConnection(hostname, port, getHostAddress(), this);
        }
    }

    public InetAddress getHostAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public void writeToFile() throws MalformedURLException {
        FileProcessor.createDir();
        String nodeDir = null;
        File dir1 = new File(FileProcessor.usrPath + "/" + URLUtil.getDomain(rootURL));
        dir1.mkdir();
        nodeDir = FileProcessor.usrPath + "/" + URLUtil.getDomain(rootURL) + "/nodes";
        File dir2 = new File(nodeDir);
        System.out.println("Creating dir: " + nodeDir + "--->" + dir2.mkdir());

        Set<String> VertexSet = Graph.getInstance().getVertices();
        for (String vertexStr : VertexSet) {
            Vertex vertex = Graph.getInstance().getVertex(vertexStr);
            if (!vertexStr.equalsIgnoreCase(rootURL) && vertex != null) {
                vertexStr = vertexStr.replaceAll("www.cs.colostate.edu/", "");
                vertexStr = FileProcessor.getValidPathname(vertexStr);
                String vertexDir = nodeDir + "/" + vertexStr;
                String inPath = vertexDir + "/in";
                String outPath = vertexDir + "/out";

                FileProcessor.output(inPath, vertex.getInLinks());
                FileProcessor.output(outPath, vertex.getOutLinks());
            }
        }

        String brokenLinkPath = dir1.getPath() + "/borken-links";
        FileProcessor.output(brokenLinkPath, URLUtil.getBadURLs().keySet());

        String disjointPath = dir1.getPath() + "/disjoint-subgraphs";
        new File(disjointPath).mkdir();


    }


    @Override
    public void onEvent(Event event) {

        int type = event.getType();
        switch (type) {
            case Protocol.NODE_HANDOFF_TASK:
                eventHandler.handleHandoffTask(event);
            case Protocol.NODE_REPORT_STATUS:
                eventHandler.handleStatusReport(event);
            case Protocol.NODE_REPORT_HANDOFF_TASK_FINISHED:
                eventHandler.handleReportHandoffTask(event);
            default:
                System.out.println("Unrecognized message type");
        }

    }

    public ThreadPoolManager getThreadPoolManager() {
        return threadPoolManager;
    }

}
