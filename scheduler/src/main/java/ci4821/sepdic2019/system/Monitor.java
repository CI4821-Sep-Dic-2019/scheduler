package ci4821.sepdic2019.system;

import java.util.TreeSet;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Monitor {
    private final TreeSet<CPU> cpuTree;

    private final Map<Process, CPU> allocatedCPUMap;
    private final Map<Process, Status> statusMap;
    private final Log log;
    public Monitor(Log log) {
        log.add("Monitor constructor");
        this.log = log;
        this.cpuTree = new TreeSet<CPU>(new Comparator<CPU> () {
            @Override
            public int compare(CPU cpu1, CPU cpu2) {
                int comp = cpu1.getProcessTree().size() - cpu2.getProcessTree().size();
                log.add("Comp: " + comp + ", " + (cpu1.getId()) + ", " + (cpu2.getId()));
                if (comp == 0) {
                    return cpu1.getId() - cpu2.getId();
                }
                return comp;
            }
        });

        this.allocatedCPUMap = new HashMap<>();
        this.statusMap = new HashMap<>();
    }

    public synchronized CPU pollIdle() {
        while(cpuTree.isEmpty()) {
            try {
                log.add("CPU TREE: " + cpuTree.size());
                wait();
            } catch (InterruptedException e) {
                log.add("Monitor interrupted.");
            }
        }
        CPU cpu = cpuTree.pollFirst();
        log.add("Poll CPU " + cpu.getId());
        return cpu;
    }

    public synchronized void addCPU(CPU cpu) {
        log.add("Add CPU " + cpu.getId());
        
        cpuTree.add(cpu);
        notifyAll();

    }

    public synchronized void removeCPU(CPU cpu) {
        boolean removed = cpuTree.remove(cpu);
        if (removed) {
            log.add("Remove CPU " + cpu.getId());
        }
    }

    public synchronized int size() {
        return cpuTree.size();
    }

    public synchronized CPU getAllocatedCPU(Process process) {
        return allocatedCPUMap.get(process);
    }

    public synchronized void setAllocatedCPU(Process process, CPU cpu) {
        log.add("Set CPU " + cpu.getId() + " for " + process.getPid());

        allocatedCPUMap.put(process, cpu);
        if (statusMap.get(process).equals(Status.READY)) {
            cpu.addProcess(process);
        }
        removeCPU(cpu);
        addCPU(cpu);
    }

    public synchronized void removeAllocatedCPU(Process process, CPU cpu) {
        cpu.getProcessTree().removeProcess(process);
        allocatedCPUMap.remove(process);
        removeCPU(cpu);
        addCPU(cpu);
    }

    public synchronized void setStatus(Process process, Status status) {
        log.add("Set " + process.getPid() + " to " + status);
        statusMap.put(process, status);
    }
}