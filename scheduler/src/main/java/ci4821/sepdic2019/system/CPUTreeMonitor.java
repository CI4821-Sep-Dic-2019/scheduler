package ci4821.sepdic2019.system;

import java.util.TreeSet;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Getter
public class CPUTreeMonitor {
    private final TreeSet<CPU> cpuTree;
    private final String logName = "[CPUTreeMonitor]";
    private final Log log;
    
    public CPUTreeMonitor(Log log) {
        this.log = log;
        this.cpuTree = new TreeSet<CPU>(new Comparator<CPU> () {
            @Override
            public int compare(CPU cpu1, CPU cpu2) {
                int comp = cpu1.getProcessTree().size() - cpu2.getProcessTree().size();
                if (comp == 0) {
                    return cpu1.getId() - cpu2.getId();
                }
                return comp;
            }
        });
    }

    /**
     * Get and pop {@code CPU} with less processes assigned to it.
     * @return      CPU with less load.
     */
    public synchronized CPU pollIdle() {
        while(cpuTree.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.add(logName + " Interrupted.");
            }
        }
        CPU cpu = cpuTree.pollFirst();
        log.add(logName + " Poll CPU with id " + cpu.getId());
        return cpu;
    }

    public synchronized CPU pollLast() {
        while(cpuTree.isEmpty()) {
            try{
                wait();
            } catch (InterruptedException e) {
                log.add(logName + " Interrupted.");
            }
        }
        CPU cpu = cpuTree.pollLast();
        log.add(logName + " Poll CPU with id " + cpu.getId());
        return cpu;
    }
    /**
     * Add a {@code CPU} to the {@code TreeSet<CPU>} to be tracked.
     * @param cpu       CPU to be added.
     */
    public synchronized void addCPU(CPU cpu) {        
        cpuTree.add(cpu);
        notifyAll();
    }

    /**
     * Remove a {@code CPU} from the {@code TreeSet<CPU>}.
     * @param cpu
     */
    public synchronized void removeCPU(CPU cpu) {
        cpuTree.remove(cpu);
    }

    /**
     * Size of the {@code TreeSet<CPU>}
     */
    public synchronized int size() {
        return cpuTree.size();
    }

    /**
     * Updates a {@code CPU} in the {@code TreeSet<CPU>}.
     * For updating, the CPU will be first removed and then
     * it will be added.
     * @param cpu
     */
    public synchronized void updateCPU(CPU cpu) {
        log.add(logName + " Update CPU with id " + cpu.getId());
        removeCPU(cpu);
        addCPU(cpu);
    }

    /**
     * Applies Push Load Balancing, given a {@code Set} of processes to migrate,
     * an expected size for each {@code CPU} and a {@code AllocatedCPUMonitor} for
     * modifying CPUs.
     * @param allocatedCPUMonitor   Monitor for updating CPUs.
     */
    public synchronized void pushLoadBalancing(AllocatedCPUMonitor allocatedCPUMonitor) {
        int totalProcesses =
            getCpuTree()
                .stream()
                .map(cpu -> cpu.getProcessTree().size())
                .map(i -> Integer.valueOf(i))
                .reduce((x,y) -> x + y)
                .orElse(0);
        int totalCPUs = size();
        double expectedDoubleSize = ((double)totalProcesses)/((double)totalCPUs);
        Integer expectedSize = (int)Math.ceil(expectedDoubleSize);

        // First extract processes to move from each CPU red black tree
        Set<Process> processesToMove = new HashSet<>();
        for (CPU cpu : getCpuTree()) {
            while(cpu.getProcessTree().size() > expectedSize) {
                processesToMove.add(cpu.getProcessTree().getProcess());
            }
        }
        Iterator<Process> processIterator = processesToMove.iterator();
        for (CPU cpu : new ArrayList<CPU>(cpuTree)) {
            while(cpu.getProcessTree().size() < expectedSize && 
                processIterator.hasNext()
            ) {
                allocatedCPUMonitor.setAllocatedCPU(processIterator.next(), cpu);
            }
        }
    }
}