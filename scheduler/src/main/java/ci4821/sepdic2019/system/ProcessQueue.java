package ci4821.sepdic2019.system;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class ProcessQueue {
    private Queue<Process> pQueue;

    public ProcessQueue() {
        this.pQueue = new ConcurrentLinkedDeque<Process>();
    }

    public synchronized void add(Process process) {
        pQueue.add(process);
        notifyAll();
    }

    public synchronized Process poll() {
        while (pQueue.isEmpty()) {
            try {
                // wait for element
                wait();
            } catch (InterruptedException e) {
                System.out.println("ProcessQueue interrupted");
            }
        }
        return pQueue.poll();
    }

    public synchronized int size() {
        return pQueue.size();
    }

    public synchronized boolean isEmpty() {
        return pQueue.isEmpty();
    }
}