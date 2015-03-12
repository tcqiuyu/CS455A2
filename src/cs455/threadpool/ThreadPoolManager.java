package cs455.threadpool;

import java.util.ArrayList;

/**
 * Created by Qiu on 3/7/15.
 */
public class ThreadPoolManager {

    private final ArrayList<Thread> workerThreads;
    private int size;
    private volatile boolean isFinish;

    public ThreadPoolManager(int size) {
        this.size = size;
        workerThreads = new ArrayList<Thread>(size);
        init();
    }

//    public static void main(String[] args) {
//        ThreadPoolManager manager = new ThreadPoolManager(3);
//
//        for (int j = 0; j < 100; j++) {
//            Task task1 = new Task() {
//                @Override
//                public void doTask() {
//                    System.out.println("Thread" + " is executing task: ");
//                }
//            };
//            TaskQueue.getInstance().addTask(task1);
//        }
//
//        manager.shutdown();
//
//    }

    public void init() {
        isFinish = false;
        for (int i = 0; i < size; i++) {
            WorkerThread aThread = new WorkerThread(i);
            workerThreads.add(aThread);

            aThread.start();
        }
    }

    public void shutdown() {

        System.out.println("Shutting Down...");
        while (!TaskQueue.getInstance().isEmpty()) {
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }

        isFinish = true;
        for (Thread thread : workerThreads) {
            System.out.println("Interrupting thread: " + thread.getName());
            thread.interrupt();
        }


    }

    private class WorkerThread extends Thread {
        private int id;

        public WorkerThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {

            while (!isFinish) {
                try {
//                    System.out.println(TaskQueue.getInstance().getTaskCount());
                    Task task = TaskQueue.getInstance().getTask();
                    System.out.println("Thread " + id + " is executing task" + task.toString());
                    task.doTask();
                    System.out.println("Thread " + id + " is sleeping.......");
                    sleep(20000);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    System.out.println("Shutdown Thread " + id + "!");
                }
            }
        }
    }
}
