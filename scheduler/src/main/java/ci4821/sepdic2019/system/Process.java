package ci4821.sepdic2019.system;

import java.util.Deque;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Process {
    @EqualsAndHashCode.Include
    private final Integer pid;
    private final Deque<Integer> taskDeque;
    private final double priority;
    private final Resource resource;
    private final Log log;
    private final String logName;

    private int lastTime = 0;

    private final CPUsMonitor cpusMonitor;
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
     * @param cpusMonitor        Monitor del árbol de CPUs ordenado por carga
     * @param allocatedCPUMonitor   Monitor del mapa Proceso -> CPU asignado
     * @param statusMapMonitor      Monitor del mapa Proceso -> Status
     * @param firstTime             Tiempo de llegada del proceso
     * @param clock                 Estructura para simular al reloj.
     */
    public Process(
        Integer pid, 
        Deque<Integer> taskDeque, 
        double priority, 
        Resource resource, 
        Log log,
        CPUsMonitor cpusMonitor, 
        AllocatedCPUMonitor allocatedCPUMonitor, 
        StatusMapMonitor statusMapMonitor, 
        int firstTime,
        Clock clock
    ) {
        this.pid = pid;
        this.logName = "[Process " + pid + "]";

        this.taskDeque = taskDeque;
        this.priority = priority;
        this.resource = resource;
        this.log = log;

        this.cpusMonitor = cpusMonitor;
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
        setLastTime(clock.getClock());
        allocatedCPUMonitor.setAllocatedCPU(this, getCPU());
    }

    public CPU getCPU() {
        return allocatedCPUMonitor.getAllocatedCPU(this);
    }

    public CPU getCPU_or_null() {
        return allocatedCPUMonitor.getCPU(this);
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
    public int getLastTime() {
        return lastTime;
    }

    public synchronized void run(Integer maxTimeToRun) {
        Integer burst = taskDeque.removeFirst();
        String type = ioBurst ? " Waiting for resource " + resource.getName() : " Running at cpu " + getCPU().getId();

        if (!ioBurst) {
            statusMapMonitor.setStatus(this, Status.RUNNING);
        }

        int timeToRun = maxTimeToRun != null && maxTimeToRun != 0 ? Math.min(maxTimeToRun, burst) : burst;
        int initTime = clock.getClock();
        for (int i=0; clock.getClock() - initTime < timeToRun; i++) {
            clock.waitForClock();
            log.add(logName + type + " (" + i + ')');
        }
        log.add(logName + " Ran for " + timeToRun + " time units");

        // Aumentar vruntime
        if(!ioBurst) {
            vruntime +=  Double.valueOf(burst) / priority;
        }

        // Si la tarea no la terminó, la siguiente tarea será lo que faltó.
        if (timeToRun < burst) {
            log.add(logName + " Reached maximum execution time.");
            taskDeque.addFirst(burst - timeToRun);
        } else {
            ioBurst = !ioBurst;
        }

        // Check if process ended
        if (!taskDeque.isEmpty()) {
            if (!ioBurst) waitForCPU();
            else waitForResource();

        } else {
            CPU cpu = getCPU();
            cpu.removeProcess(this);

            allocatedCPUMonitor.removeProcess(this);
            statusMapMonitor.removeProcess(this);
        }
    }
}