package cs455.threadpool;

import cs455.harvester.Crawler;
import cs455.util.TransportUtil;
import cs455.wireformat.proto.NodeReportStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
//                System.out.println(e.getMessage());
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

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }

            System.out.println("Worker thread " + id + " starts");
            Task task;
            while (true) {
                try {
                    synchronized (taskQueue) {
//                        System.out.println("polling task, task queue size:" + taskQueue.size());
                        task = taskQueue.poll();
                        crawler.incrementProcessingTaskCount();
                    }
                    if (task != null) {
                        System.out.println("Doing task:" + task.toString());
                        task.doTask();
                        crawler.decrementProcessingTaskCount();
                        continue;
                    }
                    crawler.decrementProcessingTaskCount();
//                    System.out.println("--------------------------" + crawler.getProcessingTaskCount() + " tasks------------------------------");
                    synchronized (taskQueue) {
                        if (taskQueue.isEmpty() && !crawler.hasProcessingTasks()) {
                            System.out.println("Task queue is empty, waiting for other crawlers...");
                            NodeReportStatus reportStatus = new NodeReportStatus(NodeReportStatus.CRAWLER_REPORT_FINISHED);
                            try {
                                TransportUtil.sendToAll(reportStatus);
                                taskQueue.wait();
                            } catch (IOException e) {
                                System.out.println("IO Expection in sending report status");
                                System.out.println(e.getMessage());
//                            try {
//                                crawler.writeToFile();
//                            } catch (MalformedURLException e1) {
//                                System.out.println(e1.getMessage());
//                            } catch (FileNotFoundException e1) {
//                                System.out.println(e1.getMessage());
//                            }
//                            break;
//                            e.printStackTrace();
//                            Iterator<String> iterator = Graph.getInstance().getGraph().keySet().iterator();
//                            for (Map.Entry<String, Vertex> entry : Graph.getInstance().getGraph().entrySet()) {
//                                Set<String> inLinks = entry.getValue().getInLinks();
//                                Set<String> outLinks = entry.getValue().getOutLinks();
//                                System.out.println(entry.getKey() + " --------> " + "inlinks: " + inLinks.size() + " outlinks: " + outLinks.size());
//                            }


//                            Graph.getInstance().getDisjointSubGraphSet();
                            }
                        } else {
                            continue;
                        }
                    }

                    if (crawler.isFinished()) {

                        System.out.println("All task finished! Writing to file...");
                        try {
                            crawler.writeToFile();
                        } catch (MalformedURLException e1) {
                            System.out.println(e1.getMessage());
                        } catch (FileNotFoundException e1) {
                            System.out.println(e1.getMessage());
                        }
                        break;
                    }

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
