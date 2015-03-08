package cs455.wireformat;

import java.io.IOException;

/**
 * Created by Qiu on 3/8/15.
 */
public interface Event {

    int getType();

    byte[] getBytes() throws IOException;
}
