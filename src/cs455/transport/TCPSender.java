package cs455.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Qiu on 3/9/2015.
 */
public class TCPSender {
    private DataOutputStream dataOutputStream;

    protected TCPSender(Socket socket) throws IOException {
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    protected synchronized void sendData(byte[] data) throws IOException {
        int dataLength = data.length;
        dataOutputStream.writeInt(dataLength);
        dataOutputStream.write(data);
        dataOutputStream.flush();
        dataOutputStream.close();
    }

}
