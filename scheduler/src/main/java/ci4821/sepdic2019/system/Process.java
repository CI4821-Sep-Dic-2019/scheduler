package ci4821.sepdic2019.system;

import java.util.Iterator;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Process {
    @EqualsAndHashCode.Include
    private final Integer pid;
    private final Iterator<Integer> taskIterator;
    private final double priority;
    private final Resource resource;
    private final Log log;
    private final String logName;

    private int lastTime = 0;

    private final CPUTreeMonitor cpuTreeMonitor;
    private final AllocatedCPUMonitor allocatedCPUMonitor;
    private final StatusMapMonitor statusMapMonitor;

    private final Clock clock;

    private Double vruntime;

    private boolean ioBurst = false;

    /**
     * @param pid                   Identificador del proceso
     * @param taskIterator          Iterador de cada tiempo de ejecución (en CPU o de I/O)
     * @param priority              Prioridad del proceso
     * @param resource              Recurso de I/O
     * @param log                   Estructura para reportar las acciones
     * @param cpuTreeMonitor        Monitor del árbol de CPUs ordenado por carga
     * @param allocatedCPUMonitor   Monitor del mapa Proceso -> CPU asignado
     * @param statusMapMonitor      Monitor del mapa Proceso -> Status
     * @param firstTime            Tiempo de llegada del proceso
     * @param clock                 Estructura para simular al reloj.
     */
    public Process(
        Integer pid, 
        Iterator<Integer> taskIterator, 
        double priority, 
        Resource resource, 
        Log log,
        CPUTreeMonitor cpuTreeMonitor, 
        AllocatedCPUMonitor allocatedCPUMonitor, 
        StatusMapMonitor statusMapMonitor, 
        int firstTime,
        Clock clock
    ) {
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
        lastTime = firstTime;
        this.clock = clock;
    }

    public void waitForResource() {
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

    /**
     * Set last unit of time that process was running.
     */
    public synchronized void setLastTime(int time) {
        lastTime = time;
    }

    /**
     * 
     * @param time  time to increment
     */
    public void incrementLastTime(int time) {
        setLastTime(getLastTime() + time);
    }
    
    /**
     * 
     * @return last unit of time that process was running.
     */
    public synchronized int getLastTime() {
        return lastTime;
    }

    // TODO: if current task is interrupted before finishing, next time process should finish it.
    public synchronized void run(Integer maxTimeToRun) {
        Integer burst = taskIterator.next();
        String type = ioBurst ? " Waiting for resource " + resource.getName() : " Running at cpu " + getCPU().getId();

        int timeToRun = maxTimeToRun != null ? Math.min(maxTimeToRun, burst) : burst;
        int initTime = clock.getClock();
        for (int i=0; clock.getClock() - initTime < timeToRun; i++) {
            log.add(logName + type + '(' + i + ')');
            clock.increment();
        }
        incrementLastTime(timeToRun);

        if(!ioBurst) {
            vruntime +=  Double.valueOf(burst) / priority;
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