package cs455.threadpool;

/**
 * Created by Qiu on 3/9/2015.
 */
public class CrawlingTask implements Task {

    private String url;
    private int depth;

    public CrawlingTask(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    @Override
    public void doTask() {
        if (depth > 5) {
            return;
        }


    }
}
