package cs455.harvester.threadpool;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Qiu on 3/7/15.
 */
public class ThreadPool {

    private int size;
    private final ArrayList<Thread> workerThreads;

    private volatile boolean isFinish;

    private TaskQueue taskQueue = TaskQueue.getInstance();

    public ThreadPool(int size) {
        this.size = size;
        workerThreads = new ArrayList<Thread>(10);
        init();
    }

    public void init() {
        for (int i = 0; i < size; i++) {
            Thread aThread = new WorkerThread();
            workerThreads.add(aThread);
            aThread.run();
        }
    }


    public void shutdown() {
        while (!taskQueue.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            isFinish = true;
            for (Thread thread : workerThreads) {
                thread.interrupt();
            }
        }
    }

    private class WorkerThread extends Thread {

        @Override
        public void run() {
            while (!isFinish) {
                try {
                    Runnable runnable = taskQueue.getTask();
                    runnable.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
