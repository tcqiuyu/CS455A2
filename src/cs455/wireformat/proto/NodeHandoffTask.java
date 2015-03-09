package cs455.wireformat.proto;

import cs455.wireformat.Event;
import cs455.wireformat.Protocol;

import java.io.*;

/**
 * Created by Qiu on 3/8/15.
 */
public class NodeHandoffTask implements Event {

    private String srcURL;
    private String destURL;
    private final int type = Protocol.NODE_HANDOFF_TASK;

    public NodeHandoffTask(String srcURL, String destURL) {
        this.srcURL = srcURL;
        this.destURL = destURL;
    }

    public NodeHandoffTask(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if (this.type == type) {
            int srcLen = din.readInt();
            byte[] srcBytes = new byte[srcLen];
            din.readFully(srcBytes);
            this.srcURL = new String(srcBytes);

            int destLen = din.readInt();
            byte[] destBytes = new byte[destLen];
            din.readFully(destBytes);
            this.destURL = new String(destBytes);
        } else {
            System.out.println("Message type does not match");
        }

        din.close();
    }

    public String getDestURL() {
        return destURL;
    }

    public String getSrcURL() {
        return srcURL;
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
        dout.writeInt(srcURL.length());
        byte[] srcByte = srcURL.getBytes();
        dout.write(srcByte);
        dout.writeInt(destURL.length());
        byte[] destByte = destURL.getBytes();
        dout.write(destByte);

        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        return marshalledBytes;
    }
}
