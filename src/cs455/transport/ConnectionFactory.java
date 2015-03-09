package cs455.transport;

import cs455.harvester.Node;

import java.io.IOException;
import java.net.InetAddress;
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

    public synchronized TCPConnection getConnection(String hostname, int port, InetAddress srcAddr, Node node) throws IOException {
        String key = genKey(hostname, port);
        TCPConnection connection;
        if (connectionMap.containsKey(key)) {
            connection = connectionMap.get(key);
        } else {
            Socket socket = new Socket(hostname, port, srcAddr, 0);
            connection = new TCPConnection(node, socket);
            register(connection, hostname, port);
        }
        return connection;
    }

    private String genKey(String host, int port) {
        return host + ":" + port;
    }

}
