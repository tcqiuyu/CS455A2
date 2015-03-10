package cs455.threadpool;

import java.util.ArrayList;

/**
 * Created by Qiu on 3/7/15.
 */
public class ThreadPoolManager {

    private int size;
    private final ArrayList<Thread> workerThreads;

    private volatile boolean isFinish;

    public ThreadPoolManager(int size) {
        this.size = size;
        workerThreads = new ArrayList<Thread>(size);
        init();
    }

    public void init() {
        isFinish = false;
        for (int i = 0; i < size; i++) {
            WorkerThread aThread = new WorkerThread();
            workerThreads.add(aThread);
            aThread.run();
        }
    }


    public void shutdown() {

        while (!TaskQueue.getInstance().isEmpty()) {
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
//                    System.out.println(TaskQueue.getInstance().getTaskCount());
                    Task task = TaskQueue.getInstance().getTask();
                    task.doTask();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
