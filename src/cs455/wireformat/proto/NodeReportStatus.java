package cs455.wireformat.proto;

import cs455.wireformat.Event;
import cs455.wireformat.Protocol;

import java.io.*;

/**
 * Created by Qiu on 3/8/15.
 */
public class NodeReportStatus implements Event {

    private final int type = Protocol.NODE_RESPOND_TASK_FINISH;
    private int status;

    public NodeReportStatus(int status) {
        this.status = status;
    }

    public NodeReportStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if (type == this.type) {
            this.status = din.readInt();
        } else {
            System.out.println("Message Type does not match");
        }
        din.close();
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(type);
        dout.write(status);
        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        return marshalledBytes;
    }
}
