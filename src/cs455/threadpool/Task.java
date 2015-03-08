package cs455.threadpool;

import net.htmlparser.jericho.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Qiu on 3/8/15.
 */
public class Task implements Runnable {

    private final String url;
    private int count;

    public Task(String url, int count) {
        this.url = url;
        this.count = count;
    }

    public String getURL() {
        return url;
    }

    @Override
    public void run() {
        //disable verbose log statements
        Config.LoggerProvider = LoggerProvider.DISABLED;

        try {
            //web page that needs to be parsed
            Source source = new Source(new URL(url));

            //get all "a" tages
            List<Element> aTags = source.getAllElements(HTMLElementName.A);

            //get the URL ("href" attribute) in each 'a' tag
            for (Element aTag : aTags) {
                aTag.getAttributeValue("href");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
