package cs455.transport;

import cs455.harvester.Node;
import cs455.util.ConfigUtil;
import cs455.util.URLUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qiu on 3/9/2015.
 */
public class ConnectionFactory {

    private static ConnectionFactory instance = new ConnectionFactory();

    private Map<String, TCPConnection> connectionMap = new HashMap<String, TCPConnection>();

    private ConnectionFactory() {
    }

    public static ConnectionFactory getInstance() {
        return instance;
    }

    public synchronized void register(TCPConnection connection, String hostname, int port) {
        connectionMap.put(genKey(hostname, port), connection);
    }


    public synchronized TCPConnection getConnection(String url) throws MalformedURLException {
        String key = ConfigUtil.getCrawlerMap().get(url);
        return connectionMap.get(key);
    }

    public synchronized TCPConnection getConnection(String hostname, int port, InetAddress srcAddr, Node node) {
        String key = genKey(hostname, port);
        TCPConnection connection = null;
        if (connectionMap.containsKey(key)) {
            connection = connectionMap.get(key);
        } else {
            Socket socket = null;
            try {
                System.out.println("Establishing connection to: " + hostname + ":" + port);
                socket = new Socket(hostname, port);
                connection = new TCPConnection(node, socket);
                register(connection, hostname, port);
                System.out.println("Establishing connection successful: " + hostname + ":" + port);
            } catch (IOException e) {
                System.out.println("Error establishing connection to:" + hostname + ":" + port);
                System.out.println(e.getMessage());
            }
        }
        return connection;
    }

    private String genKey(String host, int port) {
        return host + ":" + port;
    }

}
