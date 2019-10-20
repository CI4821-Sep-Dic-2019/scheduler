package ci4821.sepdic2019.system;

import java.util.HashMap;
import java.util.Map;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StatusMapMonitor {
    private final Map<Process, Status> statusMap;
    private final Log log;
    
    private final String logName = "[StatusMapMonitor]";

    public StatusMapMonitor (Log log) {
        this.log = log;
        this.statusMap = new HashMap<>();
    }

    /**
     * Set the {@code Status} to the {@code Process}.
     * @param process   Process whose status is going to be set.
     * @param status    Status of the process.
     */
    public synchronized void setStatus(Process process, Status status) {
        log.add(logName + " Set process " + process.getPid() + " to " + status);
        CPU cpu = process.getCPU_or_null();
        String cpu_id = "Not allocated";
        if (cpu != null) {
            cpu_id = Integer.toString(cpu.getId());
        }
        log.add_proc(
            Integer.toString(process.getPid()), 
            Double.toString(process.getPriority()), 
            Double.toString(process.getVruntime()), 
            status.name(), 
            cpu_id
        );
        statusMap.put(process, status);
        notifyAll();
    }

    /**
     * Get corresponding {@code Status} to the {@code Process}.
     * @param process   Process whose status is going to be returned.
     * @return          Process' status.
     */
    public synchronized Status getStatus(Process process) {
        while (statusMap.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.add(logName + "Interrupted");
            }
        }
        log.add(logName + " Get status of process " + process.getPid());
        Status status = statusMap.get(process);
        return status;
    }

    public synchronized void removeProcess(Process process) {
        log.add(logName + " Remove process " + process.getPid());
        statusMap.remove(process);
    }
}