package cs455.harvester;

import cs455.threadpool.ThreadPoolManager;
import cs455.transport.TCPServerThread;
import cs455.util.ConfigUtil;
import cs455.util.EventHandler;
import cs455.wireformat.Event;
import cs455.wireformat.Protocol;

/**
 * Created by Qiu on 3/7/15.
 */
public class Crawler implements Node {

    private int port;
    private String rootURL;
    private int poolSize;
    private String confPath;
    private TCPServerThread serverThread;

    private ConfigUtil configUtil;
    private ThreadPoolManager threadPoolManager;
    private EventHandler eventHandler;

    public Crawler(int port, String rootURL, int poolSize, String confPath) {
        this.port = port;
        this.rootURL = rootURL;
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
        Crawler crawler = new Crawler(port, root, poolSize, confPath);
    }

    public void init() {
        serverThread = new TCPServerThread(this, port);
        threadPoolManager = new ThreadPoolManager(poolSize);
        configUtil = new ConfigUtil(confPath, this);
        eventHandler = new EventHandler(this);

        serverThread.start();
        threadPoolManager.init();
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
