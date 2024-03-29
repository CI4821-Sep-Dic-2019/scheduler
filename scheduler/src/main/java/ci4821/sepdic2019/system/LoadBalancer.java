package ci4821.sepdic2019.system;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;

@Data
public class LoadBalancer implements Runnable {
    private final CPUsMonitor cpusMonitor;
    private final AllocatedCPUMonitor allocatedCPUMonitor;
    private final Log log;
    private final Integer cpuTime; // in miliseconds
    private Thread t;
    private final String logName = "[LoadBalancer]";

    public LoadBalancer(CPUsMonitor cpusMonitor, AllocatedCPUMonitor allocatedCPUMonitor,
        Log log, Integer cpuTime
    ) {
        this.cpusMonitor = cpusMonitor;
        this.allocatedCPUMonitor = allocatedCPUMonitor;
        this.log = log;
        this.cpuTime = cpuTime;
        t = new Thread(this, "Load Balancer");
        t.start();
    }

    public synchronized void run() {
        while(true) {
            try {
                wait(cpuTime);
            } catch (InterruptedException e) {
                log.add(logName + " Interrupted");
            }
            log.add(logName + " Push load balancing started");
            cpusMonitor.pushLoadBalancing(allocatedCPUMonitor);

        }
    }

}