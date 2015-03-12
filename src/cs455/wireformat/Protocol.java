package cs455.wireformat;

/**
 * Created by Qiu on 3/8/15.
 */
public interface Protocol {

    static final int NODE_HANDOFF_TASK = 0;
    static final int NODE_REPORT_STATUS = 1;
    static final int NODE_REPORT_HANDOFF_TASK_FINISHED=2;
}
