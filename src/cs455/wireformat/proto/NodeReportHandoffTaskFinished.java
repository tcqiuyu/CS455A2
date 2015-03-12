package cs455.wireformat.proto;

import cs455.wireformat.Event;
import cs455.wireformat.Protocol;

import java.io.*;

/**
 * Created by Qiu on 3/12/2015.
 */
public class NodeReportHandoffTaskFinished implements Event{

    private final int type = Protocol.NODE_REPORT_HANDOFF_TASK_FINISHED;

    public NodeReportHandoffTaskFinished() {

    }

    public NodeReportHandoffTaskFinished(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if (type == this.type) {
        } else {
            System.out.println("Message Type does not match");
        }
        din.close();
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
        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        return marshalledBytes;
    }
}
