package cs455.threadpool;

import cs455.graph.Graph;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Qiu on 3/7/15.
 */
public class TaskQueue {

    private static final TaskQueue instance = new TaskQueue();
    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingDeque<Runnable>();

    private TaskQueue() {
    }

    public static TaskQueue getInstance() {
        return instance;
    }

    public synchronized void addTask(Task task) {
        if (!Graph.getInstance().containsURL(task.getURL())) {
            taskQueue.add(task);
        }
    }

    public Runnable getTask() throws InterruptedException {
        return taskQueue.take();
    }

    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }
}
