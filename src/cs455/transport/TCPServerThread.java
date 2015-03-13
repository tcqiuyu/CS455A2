package cs455.transport;

import cs455.harvester.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Qiu on 3/9/2015.
 */
public class TCPServerThread extends Thread {

    private Node node;
    private ServerSocket serverSocket;
    private int localPort;

    public TCPServerThread(Node node, int localPort) {
        this.node = node;
        this.localPort = localPort;

        try {
            serverSocket = new ServerSocket(localPort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public TCPServerThread(Node node) {
        this.node = node;
        try {
            serverSocket = new ServerSocket(0);
            localPort = serverSocket.getLocalPort();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        Socket socket;
        try {
            while ((socket = serverSocket.accept()) != null) {

                System.out.println("Server is waiting at port: " + localPort);
                TCPConnection connection = new TCPConnection(node, socket);

            }
        } catch (IOException e) {
            System.out.println("-------------------------server error!!!!---------------------");
            System.err.println(e.getMessage());
        }
    }
}
