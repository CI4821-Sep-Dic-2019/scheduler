package ci4821.sepdic2019.system;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CPU implements Runnable {
    private final int id;
    private final AllocatedCPUMonitor allocatedCPUMonitor;
    private final CPUsMonitor cpusMonitor;
    private final StatusMapMonitor statusMapMonitor;
    private final Log log;
    private ProcessTree processTree;
    private Thread t;
    private final String logName;
    private final Clock clock;
    private boolean busy;
    private int usage;

    /**
     * @param id                    Identificador del CPU
     * @param log                   Estructura para reportar las acciones
     * @param cpusMonitor           Monitor del árbol de CPUs ordenado por carga
     * @param allocatedCPUMonitor   Monitor del mapa Proceso -> CPU asignado
     * @param statusMapMonitor      Monitor del mapa Proceso -> Status
     * @param clock                 Estructura para simular al reloj.
     */
    public CPU(
        int id,
        AllocatedCPUMonitor allocatedCPUMonitor,
        CPUsMonitor cpusMonitor,
        StatusMapMonitor statusMapMonitor,
        Log log,
        Clock clock
    ) {
        processTree = new ProcessTree(log);
        this.id = id;
        this.logName = "[CPU " + id + "]";
        this.allocatedCPUMonitor = allocatedCPUMonitor;
        this.cpusMonitor = cpusMonitor;
        this.statusMapMonitor = statusMapMonitor;
        this.log = log;
        this.clock = clock;
        this.busy = false;
        this.usage = 0;
        t = new Thread(this, "CPU: " + id);
        t.start();
    }

    /**
     * Obtener id del proceso.
     * @return process       Id del proceso
     */
    public int getId() {
        return id;
    }

    /**
     * Agregar proceso al árbol rojo negro de este CPU.
     * @param process       Proceso a agregar
     */
    public void addProcess(Process process) {
        log.add(logName + " add process " + process.getPid());
        processTree.addProcess(process);
        cpusMonitor.notifyAddProcess();
    }

    /**
     * Eliminar un proceso del árbol rojo negro de este CPU.
     * @param process       Proceso a eliminar
     */
    public void removeProcess(Process process) {
        log.add(logName + " remove process " + process.getPid());
        processTree.removeProcess(process);
    }

    /**
     * Obtener y sacar del árbol el próximo proceso a ejecutar, determinado
     * por su vruntime.
     * @return      próximo proceso a ejecutar
     */
    public Process pollProcess() {
        Process process = processTree.getProcess();
        log.add(logName + " poll process " + process.getPid());
        return process;
    }

    /**
     * Migrar un proceso del CPU con más carga a este.
     */
    public void pullLoadBalancing() {
        CPU cpu = cpusMonitor.getMaxCPU();
        Process process = cpu.pollProcess();
        allocatedCPUMonitor.setAllocatedCPU(process, this);
    }

    public void updateUsage() {
        usage += (isBusy() ? 1 : 0);
    }

    public int workingTime() {
        return usage;
    }

    public int sleepingTime() {
        return clock.getClock() - usage;
    }

    public double usagePercentage() {
        double total = clock.getClock();
        double percentage = (double) usage/total;
        return percentage;
    }

    public boolean isBusy() {
        return busy || !processTree.isEmpty();
    }

    public int processesNumber() {
        int processesNum = (busy ? 1 : 0) + processTree.size();
        return processesNum;
    }

    public void run() {
        while(true) {
            Process process = pollProcess();
            busy = true;
            int procLastTime = process.getLastTime();

            // Time waiting divided by current number of processes.
            int treeSize = getProcessTree().size();
            int timeWaiting = clock.getClock() - procLastTime;
            Integer maxTimeToRun = (treeSize > 0 && timeWaiting > 0) ? ((int) Math.ceil(timeWaiting/treeSize)) : null;

            statusMapMonitor.setStatus(process, Status.RUNNING);
            log.add(logName + "  start running process " + process.getPid());
            process.run(maxTimeToRun);
            busy = false;
            if (processTree.isEmpty()) {
                log.add(logName + " Pull load balancing from CPU: ");
                pullLoadBalancing();
            }
        }
    }
}