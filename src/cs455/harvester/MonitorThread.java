package cs455.harvester;

import cs455.threadpool.ThreadPoolManager;

/**
 * Created by Qiu on 3/11/2015.
 */


public class MonitorThread extends Thread {

    private volatile int finishedCrawlerCount;

    public MonitorThread() {

    }

    public synchronized void updateFinishCrawlerCount(){
        finishedCrawlerCount++;
    }

    @Override
    public void run() {

    }

}
