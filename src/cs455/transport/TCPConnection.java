package cs455.transport;

import cs455.harvester.Node;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Qiu on 3/9/2015.
 */
public class TCPConnection {

    private TCPSender sender;
    private TCPReceiverThread receiverThread;
    private Node node;
    private Socket socket;

    public TCPConnection(Node node, Socket socket) throws IOException {
        this.node = node;
        this.socket = socket;
        sender = new TCPSender(socket);
        receiverThread = new TCPReceiverThread(socket, node);
        receiverThread.start();
    }

    public void sendData(byte[] data) throws IOException {
        sender.sendData(data);
    }


}
