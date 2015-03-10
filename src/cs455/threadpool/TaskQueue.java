package cs455.threadpool;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Qiu on 3/7/15.
 */
public class TaskQueue {

    private static final TaskQueue instance = new TaskQueue();
    private final Queue<Task> taskQueue = new LinkedList<Task>();

    private TaskQueue() {
    }

    public static TaskQueue getInstance() {
        return instance;
    }

    public void addTask(Task task) {
        synchronized (taskQueue) {
            taskQueue.offer(task);
            taskQueue.notifyAll();
        }
    }

    public Task getTask() throws InterruptedException {
        synchronized (taskQueue) {
            while (taskQueue.isEmpty()) {
//                System.out.println("size:" + taskQueue.size());
                taskQueue.wait();
            }
            return taskQueue.poll();
        }
    }

    public int getTaskCount() {
        return taskQueue.size();
    }

    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }
}
