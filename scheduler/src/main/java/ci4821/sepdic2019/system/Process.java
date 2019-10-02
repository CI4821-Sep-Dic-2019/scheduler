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
    private final int pid;
    private final Iterator<Integer> taskIterator;
    private final double priority;
    private final Resource resource;
    private final Log log;
    private CPU cpu;

    @Builder.Default
    private Long vruntime;

    @Builder.Default
    private boolean ioBurst = false;

    public Process(int pid, Iterator<Integer> taskIterator, double priority,
        Resource resource, Log log, CPU cpu
    ) {
        this.pid = pid;
        this.taskIterator = taskIterator;
        this.priority = priority;
        this.resource = resource;
        this.cpu = cpu;
        this.log = log;
        vruntime = 0L;
    }

    public void waitForResource() {
        resource.enqueue(this);
    }

    public void waitForCPU() {
        cpu.addProcess(this);
    }

    // TODO update vruntime
    public void run() {
        if (! taskIterator.hasNext()) {
            return;
        }
        Integer burst = taskIterator.next();
        String type = ioBurst ? "Resource " + resource.getName() : "CPU " + cpu.getId();
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