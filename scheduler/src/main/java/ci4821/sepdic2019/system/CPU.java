package ci4821.sepdic2019.system;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CPU implements Runnable {
    private final int id;
    private final CPUTreeMonitor cpuTreeMonitor;
    private final StatusMapMonitor statusMapMonitor;
    private final Log log;
    private ProcessTree processTree;
    private Thread t;
    private final String logName;
    public CPU(int id, CPUTreeMonitor cpuTreeMonitor, StatusMapMonitor statusMapMonitor, Log log) {
        processTree = new ProcessTree(log);
        this.id = id;
        this.logName = "[CPU " + id + "]";
        this.cpuTreeMonitor = cpuTreeMonitor;
        this.statusMapMonitor = statusMapMonitor;
        this.log = log;
        t = new Thread(this, "CPU: " + id);
        t.start();
    }

    public void addProcess(Process process) {
        log.add(logName + " add process " + process.getPid());
        processTree.addProcess(process);
    }

    public void removeProcess(Process process) {
        log.add(logName + " remove process " + process.getPid());
        processTree.removeProcess(process);
    }

    public Process pollProcess() {
        Process process = processTree.getProcess();
        log.add(logName + " poll process " + process.getPid());        
        cpuTreeMonitor.removeCPU(this);
        cpuTreeMonitor.addCPU(this);
        return process;
    }

    public void run() {
        while(true) {
            Process process = pollProcess();
            log.add(logName + "  start running process " + process.getPid());
            statusMapMonitor.setStatus(process, Status.RUNNING);
            process.run();
        }
    }
}