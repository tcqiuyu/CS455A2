package cs455.harvester;

import cs455.graph.Graph;
import cs455.graph.Vertex;
import cs455.threadpool.CrawlingTask;
import cs455.threadpool.TaskQueue;
import cs455.threadpool.ThreadPoolManager;
import cs455.transport.ConnectionFactory;
import cs455.transport.TCPServerThread;
import cs455.util.ConfigUtil;
import cs455.util.EventHandler;
import cs455.wireformat.Event;
import cs455.wireformat.Protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

/**
 * Created by Qiu on 3/7/15.
 */
public class Crawler implements Node {

    public static String rootURL;

    private int port;
    private int poolSize;
    private String confPath;
    private TCPServerThread serverThread;

    private ConfigUtil configUtil;
    private ThreadPoolManager threadPoolManager;
    private EventHandler eventHandler;

    public Crawler(int port, int poolSize, String confPath) {
        this.port = port;
        this.poolSize = poolSize;
        this.confPath = confPath;
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java cs455.harvester.Crawler portnum thread-pool-size root-url path-to-config-file");
        }

        int port = Integer.parseInt(args[0]);
        int poolSize = Integer.parseInt(args[1]);
        String root = args[2];
        String confPath = args[3];
        rootURL = root;
        Crawler crawler = new Crawler(port, poolSize, confPath);

        crawler.init();
    }

    public void init() {
        serverThread = new TCPServerThread(this, port);
        configUtil = new ConfigUtil(confPath, this);
        eventHandler = new EventHandler(this);
        serverThread.start();
        try {
            Thread.sleep(10000);
            System.out.println("HERE");
            initConnection();

        } catch (IOException e) {
//            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CrawlingTask initTask = new CrawlingTask(null, rootURL, 1, configUtil);
        TaskQueue.getInstance().addTask(initTask);

        Vertex root = new Vertex(rootURL);
        Graph.getInstance().addVertex(root);

        threadPoolManager = new ThreadPoolManager(poolSize);

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

    @Override
    public void onEvent(Event event, String srcAddr) {

        int type = event.getType();
        switch (type) {
            case Protocol.NODE_HANDOFF_TASK:
                eventHandler.handleHandoffTask(event);
            case Protocol.NODE_RESPOND_TASK_FINISH:
                eventHandler.handleStatusReport(event);
            default:
                System.out.println("Unrecognized message type");
        }

    }
}
