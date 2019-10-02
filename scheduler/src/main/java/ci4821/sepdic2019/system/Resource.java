package ci4821.sepdic2019.system;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;

@Data
public class Resource implements Runnable {
    private final String name;
    private final Log log;
    private Queue<Process> pQueue;
    private Thread t;

    public Resource(String name, Log log) {
        this.name = name;
        this.log = log;
        this.pQueue = new ConcurrentLinkedDeque<Process>();
        t = new Thread(this, name);
        t.start();
    }

    public synchronized void enqueue(Process process) {
        pQueue.add(process);
        log.add("Resource " + name + ": enqueue process " + process.getPid());
        if (pQueue.size() == 1) {
            // notify queue is not empty
            notifyAll();
        }
    }

    public synchronized void run() {
        while (true) {
            while (pQueue.isEmpty()) {
                try {
                    // wait for element
                    wait();
                } catch (InterruptedException e) {
                    log.add("Resource " + name + " interrupted.");
                }
            }
            Process process = pQueue.poll();
            log.add("Resource " + name + ": dequeue process " + process.getPid());
            process.waitForCPU();
        }
    }
}