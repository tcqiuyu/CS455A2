package cs455.wireformat;

import java.io.*;

/**
 * Created by Qiu on 3/8/15.
 */
public class NodeRespondTaskFinish implements Event {

    private final int type = Protocol.NODE_RESPOND_TASK_FINISH;

    public NodeRespondTaskFinish() {
    }

    public NodeRespondTaskFinish(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if (type != this.type) {
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
