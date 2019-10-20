package ci4821.sepdic2019.system;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;

@Getter
public class CPUsMonitor {
    private final ArrayList<CPU> cpus;
    private final String logName = "[CPUsMonitor]";
    private final Log log;
    
    public CPUsMonitor(Log log) {
        this.log = log;
        this.cpus = new ArrayList<CPU>();
    }

    /**
     * Get {@code CPU} with less processes assigned to it.
     * @return      CPU with less load.
     */
    public synchronized CPU getMinCPU() {
        while(cpus.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.add(logName + " Interrupted.");
            }
        }

        CPU min_cpu = cpus.get(0);
        for (CPU cpu : cpus) {
            if (cpu.processesNumber() < min_cpu.processesNumber()) {
                min_cpu = cpu;
            }
        }
        log.add(logName + " Get CPU with id " + min_cpu.getId());
        return min_cpu;
    }

    public synchronized void notifyAddProcess() {
        notifyAll();
    }

    public synchronized CPU getMaxCPU() {
        while(cpus.isEmpty()) {
            try{
                wait();
            } catch (InterruptedException e) {
                log.add(logName + " Interrupted.");
            }
        }
        CPU max_cpu = cpus.get(0);
        for (CPU cpu : cpus) {
            if (cpu.processesNumber() > max_cpu.processesNumber()) {
                max_cpu = cpu;
            }
        }

        while (max_cpu.getProcessTree().size() == 0) {
            try{
                wait();
            } catch (InterruptedException e) {
                log.add(logName + " Interrupted.");
            }
            for (CPU cpu : cpus) {
                if (cpu.processesNumber() > max_cpu.processesNumber()) {
                    max_cpu = cpu;
                }
            }
        }

        log.add(logName + " Get CPU with id " + max_cpu.getId());
        return max_cpu;
    }
    /**
     * Add a {@code CPU} to the {@code ArrayList<CPU>} to be tracked.
     * @param cpu       CPU to be added.
     */
    public synchronized void addCPU(CPU cpu) {        
        cpus.add(cpu);
        notifyAll();
        this.update_cpu_log(cpu);
        
    }

    public void update_cpu_log(CPU cpu) {
        log.add_cpu(
            Integer.toString(cpu.getId()), 
            cpu.isBusy() ? "YES" : "NO", 
            Integer.toString(cpu.processesNumber()), 
            Integer.toString(cpu.workingTime()), 
            Integer.toString(cpu.sleepingTime()), 
            Double.toString(cpu.usagePercentage())
        );
    }

    /**
     * Size of the {@code ArrayList<CPU>}
     */
    public synchronized int size() {
        return cpus.size();
    }


    /**
     * Applies Push Load Balancing, given a {@code Set} of processes to migrate,
     * an expected size for each {@code CPU} and a {@code AllocatedCPUMonitor} for
     * modifying CPUs.
     * @param allocatedCPUMonitor   Monitor for updating CPUs.
     */
    public synchronized void pushLoadBalancing(AllocatedCPUMonitor allocatedCPUMonitor) {
        int totalProcesses = cpus
                .stream()
                .map(cpu -> cpu.processesNumber())
                .map(i -> Integer.valueOf(i))
                .reduce((x,y) -> x + y)
                .orElse(0);
        int totalCPUs = size();
        double expectedDoubleSize = ((double)totalProcesses)/((double)totalCPUs);
        Integer expectedSize = (int)Math.ceil(expectedDoubleSize);

        // First extract processes to move from each CPU red black tree
        Set<Process> processesToMove = new HashSet<>();
        for (CPU cpu : cpus) {
            while(cpu.processesNumber() > expectedSize) {
                processesToMove.add(cpu.getProcessTree().getProcess());
            }
        }
        Iterator<Process> processIterator = processesToMove.iterator();
        for (CPU cpu : cpus) {
            while(cpu.processesNumber() < expectedSize && 
                processIterator.hasNext()
            ) {
                allocatedCPUMonitor.setAllocatedCPU(processIterator.next(), cpu);
            }
        }
    }

    public synchronized void updateCPUsUsage() {
        System.out.println("CPU's = " + cpus.size());
        for (CPU cpu : cpus) {
            cpu.updateUsage();
            this.update_cpu_log(cpu);
            System.out.print("CPU " + cpu.getId());
            System.out.println(" : busy = " + cpu.isBusy() +
                ", process = " + cpu.processesNumber() +
                ", working time = " + cpu.workingTime() +
                ", sleeping time = " + cpu.sleepingTime() +
                ", usage = " + cpu.usagePercentage()
            );
        }
    }
}
