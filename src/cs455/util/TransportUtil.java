package cs455.util;

import cs455.harvester.Node;
import cs455.transport.ConnectionFactory;
import cs455.transport.TCPConnection;
import cs455.wireformat.Event;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

/**
 * Created by Qiu on 3/9/2015.
 */
public class TransportUtil {

    public static void sendMessage(String destAddress, int destPort, Event event, InetAddress srcAddr, Node node)
            throws IOException {
        TCPConnection conn = ConnectionFactory.getInstance().getConnection(destAddress, destPort, srcAddr, node);
//        TCPConnection connection = ConnectionFactory.getInstance().
        conn.sendData(event.getBytes());
    }

    public static void sendMessage(String url, Event event)
            throws IOException {
        TCPConnection conn = ConnectionFactory.getInstance().getConnection(url);
//        TCPConnection connection = ConnectionFactory.getInstance().
        if (conn != null) {
            conn.sendData(event.getBytes());
        } else {
            System.out.println("No Connection found for ------------->" + url);
        }
    }

    public static void sendToAll(Event event) throws IOException {
        Set<String> crawlerSet = ConfigUtil.getCrawlerMap().keySet();
        for (String crawler : crawlerSet) {
            sendMessage(crawler, event);
        }
    }
}
