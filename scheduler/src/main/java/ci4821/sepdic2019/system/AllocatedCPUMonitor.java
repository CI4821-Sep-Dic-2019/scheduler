package ci4821.sepdic2019.system;
import java.util.HashMap;
import java.util.Map;
import ci4821.sepdic2019.ds.Log;
import lombok.Getter;

@Getter
public class AllocatedCPUMonitor {
    private final Map<Process, CPU> map;
    private final Log log;
    private final CPUTreeMonitor cpuTree;
    private final StatusMapMonitor statusMap;
    private final String logName = "[AllocatedCPUMonitor]";

    public AllocatedCPUMonitor(Log log, CPUTreeMonitor cpuTree, StatusMapMonitor statusMap) {
        this.log = log;
        this.map = new HashMap<>();
        this.cpuTree = cpuTree;
        this.statusMap = statusMap;
    }

    /**
     * Get {@code CPU} allocated to a {@code Process}.
     * @param process       whose allocated CPU is going to be returned
     * @return              Allocated CPU to the process.
     */
    public synchronized CPU getAllocatedCPU(Process process) {
        while (map.isEmpty() || map.get(process) == null) {
            System.out.println("contains key " + map.containsKey(process));
            try {
                wait();
            } catch (InterruptedException e) {
                log.add(logName + " Interrupted");
            }
        }
        return map.get(process);
    }

    /**
     * Allocates one {@code CPU} to a {@code Process},
     * assigning it to the pid in a {@code Map<Process, CPU>}.
     * If process is in {@code Status#READY} status, the process will
     * be added to the Red Black Tree of the CPU.
     * @param process   Process to assign to the cpu.
     * @param cpu       CPU to allocate.
     */
    public synchronized void setAllocatedCPU(Process process, CPU cpu) {
        log.add(logName + " Assign CPU " + cpu.getId() + " to process " + process.getPid());

        map.put(process, cpu);
        if (statusMap.getStatus(process).equals(Status.READY)) {
            cpu.addProcess(process);
        }
        cpuTree.updateCPU(cpu);

        notifyAll();
    }

    public synchronized void removeProcess(Process process) {
        log.add(logName + " Remove process " + process.getPid());
        map.remove(process);
    }
}