package cs455.harvester.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Qiu on 3/7/15.
 */
public class TaskQueue {

    private static final TaskQueue instance = new TaskQueue();
    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingDeque<Runnable>();

    private TaskQueue(){}

    public static TaskQueue getInstance() {
        return instance;
    }

    public synchronized void addTask(Runnable runnable) {
        taskQueue.add(runnable);
    }

    public Runnable getTask() throws InterruptedException {
        return taskQueue.take();
    }

    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }
}
