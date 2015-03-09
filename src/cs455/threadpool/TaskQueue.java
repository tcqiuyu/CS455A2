package cs455.threadpool;

import cs455.graph.Graph;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Qiu on 3/7/15.
 */
public class TaskQueue {

    private static final TaskQueue instance = new TaskQueue();
    private final Queue<Runnable> taskQueue = new LinkedList<Runnable>();

    private TaskQueue() {
    }

    public static TaskQueue getInstance() {
        return instance;
    }

    public void addTask(Task task) {
        synchronized (taskQueue) {
            if (!Graph.getInstance().containsURL(task.getURL())) {
                taskQueue.add(task);
            }
        }
    }

    public Runnable getTask() throws InterruptedException {
        return taskQueue.poll();
    }

    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }
}
