package cs455.transport;

import cs455.harvester.Node;
import cs455.wireformat.Event;
import cs455.wireformat.EventFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Qiu on 3/9/2015.
 */
public class TCPReceiverThread extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream;
    private Node node;

    public TCPReceiverThread(Socket socket, Node node) throws IOException {
        this.socket = socket;
        this.node = node;
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (socket != null) {
            try {
                int dataLength = dataInputStream.readInt();
                if (dataLength > 0) {
                    byte[] data = new byte[dataLength];
                    dataInputStream.readFully(data, 0, dataLength);
                    Event event = EventFactory.getInstance().getEvent(data);
                    node.onEvent(event);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
    }

}
