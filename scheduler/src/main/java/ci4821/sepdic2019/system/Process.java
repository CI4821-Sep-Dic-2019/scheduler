package ci4821.sepdic2019.system;

import java.util.Iterator;

import ci4821.sepdic2019.ds.Log;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Process {
    private final Integer pid;
    private final Iterator<Integer> taskIterator;
    private final double priority;
    private final Resource resource;
    private final Log log;
    private final Monitor cpuMonitor;

    @Builder.Default
    private Long vruntime = 0L;

    @Builder.Default
    private boolean ioBurst = false;

    public Process(Integer pid, Iterator<Integer> taskIterator, double priority,
        Resource resource, Log log, Monitor cpuMonitor
    ) {
        this.pid = pid;
        this.taskIterator = taskIterator;
        this.priority = priority;
        this.resource = resource;
        this.log = log;
        this.cpuMonitor = cpuMonitor;
        vruntime = 0L;
    }

    public void waitForResource() {
        cpuMonitor.setStatus(this, Status.BLOCKED);
        resource.enqueue(this);
    }

    public void waitForCPU() {
        cpuMonitor.setStatus(this, Status.READY);
        cpuMonitor.setAllocatedCPU(this, getCPU());
    }

    public CPU getCPU() {
        return cpuMonitor.getAllocatedCPU(this);
    }

    // TODO update vruntime
    public void run() {
        Integer burst = taskIterator.next();
        String type = ioBurst ? "Resource " + resource.getName() : "CPU " + getCPU().getId();
        for (Integer i=0; i < burst; i++) {
            log.add("Process " + pid + ": " + type + " (" + i + ")");
        }
        if (taskIterator.hasNext()) {
            if (ioBurst) waitForCPU();
            else waitForResource();
            ioBurst = !ioBurst;
        }
    }
}