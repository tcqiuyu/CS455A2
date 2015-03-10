package cs455.threadpool;

import cs455.util.URLUtil;

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

    public static void main(String[] args) {
        String root = "http://psy.psych.colostate.edu/";
        CrawlingTask task = new CrawlingTask("", 0);

    }

    @Override
    public void doTask() {
        if (depth > 5) {
            return;
        }

        //URL in same domain
        if (URLUtil.withinDomain(url)) {
            depth++;
            for (String extractedUrl : URLUtil.getInstance().extractUrl(url)) {
                CrawlingTask newTask = new CrawlingTask(extractedUrl, depth);
                TaskQueue.getInstance().addTask(newTask);
                System.out.println(extractedUrl);

            }
            return;
        }

        //URL in other domain
        if (URLUtil.isTargetDomain(url)) {


        }
    }

}
