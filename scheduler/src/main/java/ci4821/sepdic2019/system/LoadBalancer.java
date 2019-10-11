package ci4821.sepdic2019.system;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;

@Data
public class LoadBalancer implements Runnable {
    private final Monitor cpuMonitor;
    private final Log log;
    private final Integer slice; // in miliseconds
    private Thread t;

    public LoadBalancer(Monitor cpuMonitor, Log log, Integer slice) {
        log.add("Load balancer constructor");
        this.cpuMonitor = cpuMonitor;
        this.log = log;
        this.slice = slice;
        t = new Thread(this, "Load Balancer");
        t.start();
    }

    public synchronized void run() {
        while(true) {
            try {
                wait(slice);
            } catch (InterruptedException e) {
                log.add("Load balancer interrupted");
            }
            log.add("Push load balancing started");
            int totalProcesses =
                cpuMonitor.getCpuTree()
                    .stream()
                    .map(cpu -> cpu.getProcessTree().size())
                    .map(i -> Integer.valueOf(i))
                    .reduce((x,y) -> x + y)
                    .orElse(0);
            int totalCPUs = cpuMonitor.size();
            double expectedDoubleSize = ((double)totalProcesses)/((double)totalCPUs);
            Integer expectedSize = (int)Math.ceil(expectedDoubleSize);

            // First extract processes to move from each CPU red black tree
            Set<Process> processesToMove = new HashSet<>();
            for (CPU cpu : cpuMonitor.getCpuTree()) {
                while(cpu.getProcessTree().size() > expectedSize) {
                    processesToMove.add(cpu.getProcessTree().getProcess());
                }
            }

            Iterator<Process> processIterator = processesToMove.iterator();
            for (CPU cpu : cpuMonitor.getCpuTree()) {
                while(cpu.getProcessTree().size() < expectedSize && 
                    processIterator.hasNext()
                ) {
                    cpuMonitor.setAllocatedCPU(processIterator.next(), cpu);
                }
            }

        }
    }

}