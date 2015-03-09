package cs455.util;

import cs455.harvester.Node;
import cs455.transport.ConnectionFactory;
import cs455.transport.TCPConnection;
import cs455.wireformat.Event;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Qiu on 3/9/2015.
 */
public class MessagingUtil {

    public static void sendMessage(String destAddress, int destPort, Event event, InetAddress srcAddr, Node node)
            throws IOException {
        TCPConnection conn = ConnectionFactory.getInstance().getConnection(destAddress, destPort, srcAddr, node);
        conn.sendData(event.getBytes());
    }
}
