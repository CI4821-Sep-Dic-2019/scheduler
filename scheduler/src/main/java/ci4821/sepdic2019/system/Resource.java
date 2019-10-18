package ci4821.sepdic2019.system;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;

@Data
public class Resource implements Runnable {
    private final String name;
    private final Log log;
    private ProcessQueue pQueue;
    private Thread t;
    private final String logName = "[I/O]";

    public Resource(String name, Log log) {
        this.name = name;
        this.log = log;
        this.pQueue = new ProcessQueue();
        t = new Thread(this, name);
        t.start();
    }

    public void enqueue(Process process) {
        pQueue.add(process);
        log.add(logName + "  enqueue process " + process.getPid());
    }

    public synchronized void run() {
        while (true) {
            Process process = pQueue.poll();
            log.add(logName + "  dequeue process " + process.getPid());
            process.run(null);
        }
    }
}