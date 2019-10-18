package ci4821.sepdic2019.system;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CPU implements Runnable {
    private final int id;
    private final AllocatedCPUMonitor allocatedCPUMonitor;
    private final CPUTreeMonitor cpuTreeMonitor;
    private final StatusMapMonitor statusMapMonitor;
    private final Log log;
    private ProcessTree processTree;
    private Thread t;
    private final String logName;
    private final Clock clock;

    /**
     * @param id                    Identificador del CPU
     * @param log                   Estructura para reportar las acciones
     * @param cpuTreeMonitor        Monitor del árbol de CPUs ordenado por carga
     * @param allocatedCPUMonitor   Monitor del mapa Proceso -> CPU asignado
     * @param statusMapMonitor      Monitor del mapa Proceso -> Status
     * @param clock                 Estructura para simular al reloj.
     */
    public CPU(
        int id,
        AllocatedCPUMonitor allocatedCPUMonitor,
        CPUTreeMonitor cpuTreeMonitor,
        StatusMapMonitor statusMapMonitor,
        Log log,
        Clock clock
    ) {
        processTree = new ProcessTree(log);
        this.id = id;
        this.logName = "[CPU " + id + "]";
        this.allocatedCPUMonitor = allocatedCPUMonitor;
        this.cpuTreeMonitor = cpuTreeMonitor;
        this.statusMapMonitor = statusMapMonitor;
        this.log = log;
        this.clock = clock;
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
        cpuTreeMonitor.updateCPU(this);
        return process;
    }

    /**
     * Migrar un proceso del CPU con más carga a este.
     */
    public void pullLoadBalancing() {
        CPU cpu = cpuTreeMonitor.pollLast();
        Process process = cpu.pollProcess();
        log.add(logName + " pull load balancing from CPU: " + cpu.getId());
        allocatedCPUMonitor.setAllocatedCPU(process, this);
    }

    public void run() {
        while(true) {
            Process process = pollProcess();
            int procLastTime = process.getLastTime();

            // Time waiting divided by current number of processes.
            int treeSize = getProcessTree().size();
            int timeWaiting = clock.getClock() - procLastTime;
            Integer maxTimeToRun = (treeSize > 0 && timeWaiting > 0) ? ((int) Math.ceil(timeWaiting/treeSize)) : null;

            statusMapMonitor.setStatus(process, Status.RUNNING);
            log.add(logName + "  start running process " + process.getPid());
            log.add_proc(Integer.toString(process.getPid()), Double.toString(process.getPrio()), "", "RUNNING", Integer.toString(this.id));
            process.run(maxTimeToRun);

            if (processTree.isEmpty()) {
                pullLoadBalancing();
            }
        }
    }
}