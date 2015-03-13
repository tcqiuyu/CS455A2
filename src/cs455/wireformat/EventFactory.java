package cs455.wireformat;

import cs455.wireformat.proto.NodeHandoffTask;
import cs455.wireformat.proto.NodeReportHandoffTaskFinished;
import cs455.wireformat.proto.NodeReportStatus;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Qiu on 3/9/2015.
 */
public class EventFactory {

    private static final EventFactory instance = new EventFactory();

    private EventFactory() {
    }

    public static EventFactory getInstance() {
        return instance;
    }

    public Event getEvent(byte[] data) {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(
                baInputStream));
        try {
            int type = din.readInt();
            baInputStream.close();
            din.close();

            switch (type) {
                case Protocol.NODE_HANDOFF_TASK:
                    return new NodeHandoffTask(data);
                case Protocol.NODE_REPORT_STATUS:
                    return new NodeReportStatus(data);
                case Protocol.NODE_REPORT_HANDOFF_TASK_FINISHED:
                    return new NodeReportHandoffTaskFinished(data);
                default:
                    System.err.println("No such message type: " + type);
                    return null;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

}
