package cs455.threadpool;

import cs455.graph.Graph;
import cs455.graph.Vertex;
import cs455.harvester.Crawler;
import cs455.util.TransportUtil;
import cs455.wireformat.proto.NodeReportStatus;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by Qiu on 3/7/15.
 */
public class ThreadPoolManager {

    private final ArrayList<Thread> workerThreads;
    private Queue<Task> taskQueue = new LinkedList<Task>();
    private int size;
    private volatile boolean isFinish;
    private Crawler crawler;

    public ThreadPoolManager(int size, Crawler crawler) {
        this.size = size;
        workerThreads = new ArrayList<Thread>(size);
        this.crawler = crawler;
//        init();
    }

    public void addTask(Task task) {
        synchronized (taskQueue) {
//            System.out.println("Adding task: " + task.toString());
            taskQueue.add(task);
            taskQueue.notifyAll();
        }
    }

    public void init() {
        isFinish = false;
        for (int i = 0; i < size; i++) {
            WorkerThread aThread = new WorkerThread(i);
            workerThreads.add(aThread);

            aThread.start();
        }

        for (Thread thread : workerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }


    private class WorkerThread extends Thread {
        private int id;

        public WorkerThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {

            System.out.println("Worker thread " + id + " starts");
            Task task;
            while (true) {
                try {
                    synchronized (taskQueue) {
//                        System.out.println("polling task, task queue size:" + taskQueue.size());
                        task = taskQueue.poll();
                    }

                    if (task != null) {
                        System.out.println("Doing task:" + task.toString());
                        task.doTask();
                        continue;
                    }

                    synchronized (taskQueue) {
                        if (!taskQueue.isEmpty()) {
                            continue;
                        }
                        NodeReportStatus reportStatus = new NodeReportStatus(NodeReportStatus.CRAWLER_REPORT_FINISHED);
                        try {
                            TransportUtil.sendToAll(reportStatus);
                        } catch (IOException e) {
//                            e.printStackTrace();
                            Iterator<String> iterator = Graph.getInstance().getGraph().keySet().iterator();
                            for (Map.Entry<String, Vertex> entry : Graph.getInstance().getGraph().entrySet()) {
                                Set<String> inLinks = entry.getValue().getInLinks();
                                Set<String> outLinks = entry.getValue().getOutLinks();
                                System.out.println(entry.getKey() + " --------> " + "inlinks: " + inLinks.size() + " outlinks: " + outLinks.size());
                            }

                            try {
                                crawler.writeToFile();
                            } catch (MalformedURLException e1) {
//                                e1.printStackTrace();
                            }
//                            Graph.getInstance().getDisjointSubGraphSet();
                        }
                    }

                    if (crawler.isFinished()) {
                        System.out.println("All task finished!");
                        break;
                    }

                    sleep(5000);
                    return;
                } catch (InterruptedException e) {
//                    e.printStackTrace();

                }
            }
//            System.out.println(TaskQueue.getInstance().getTaskCount());
//            System.out.println("Shutdown Thread " + id + "!");
//            MonitorThread.updateFinishedWorkerThreadCount();
        }
    }
}
