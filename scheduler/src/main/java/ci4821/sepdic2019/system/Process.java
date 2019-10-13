package ci4821.sepdic2019.system;

import java.util.Iterator;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;

@Data
public class Process {
    private final Integer pid;
    private final Iterator<Integer> taskIterator;
    private final double priority;
    private final Resource resource;
    private final Log log;
    private final String logName;

    private final CPUTreeMonitor cpuTreeMonitor;
    private final AllocatedCPUMonitor allocatedCPUMonitor;
    private final StatusMapMonitor statusMapMonitor;

    private final Integer cpuTime;

    private Double vruntime;

    private boolean ioBurst = false;

    public Process(Integer pid, Iterator<Integer> taskIterator, double priority, Resource resource, Log log,
            CPUTreeMonitor cpuTreeMonitor, AllocatedCPUMonitor allocatedCPUMonitor, StatusMapMonitor statusMapMonitor,
            Integer cpuTime) {
        this.pid = pid;
        this.logName = "[Process " + pid + "]";

        this.taskIterator = taskIterator;
        this.priority = priority;
        this.resource = resource;
        this.log = log;

        this.cpuTreeMonitor = cpuTreeMonitor;
        this.allocatedCPUMonitor = allocatedCPUMonitor;
        this.statusMapMonitor = statusMapMonitor;

        vruntime = 0D;

        this.cpuTime = cpuTime;
    }

    public void waitForResource() {
        log.add(logName + " Wait for resource");
        statusMapMonitor.setStatus(this, Status.BLOCKED);
        resource.enqueue(this);
    }

    public void waitForCPU() {
        log.add(logName + " Wait for CPU");
        statusMapMonitor.setStatus(this, Status.READY);
        allocatedCPUMonitor.setAllocatedCPU(this, getCPU());
    }

    public CPU getCPU() {
        return allocatedCPUMonitor.getAllocatedCPU(this);
    }

    // TODO update vruntime
    public synchronized void run() {
        Integer burst = taskIterator.next();
        String type = ioBurst ? " Waiting for resource " + resource.getName() : " Running at cpu " + getCPU().getId();
        for (Integer i = 0; i < burst; i++) {
            try {
                wait(cpuTime);
                log.add(logName + type + " (" + i + ")");
            } catch (InterruptedException e) {
                log.add(logName + " Interrupted");
            }
        }
        if(!ioBurst) {
            vruntime +=  Double.valueOf(burst)*priority;
        }
        // Check if process ended
        if (taskIterator.hasNext()) {
            ioBurst = !ioBurst;

            if (!ioBurst) waitForCPU();
            else waitForResource();

        } else {

            CPU cpu = getCPU();
            cpu.removeProcess(this);
            cpuTreeMonitor.updateCPU(cpu);

            allocatedCPUMonitor.removeProcess(this);
            statusMapMonitor.removeProcess(this);
        }
    }
}