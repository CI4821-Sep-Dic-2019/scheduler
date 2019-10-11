package ci4821.sepdic2019.system;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CPU implements Runnable {
    private final int id;
    private final Monitor cpuMonitor;
    private final Log log;
    private ProcessTree processTree;
    private Thread t;
    public CPU(int id, Monitor cpuMonitor, Log log) {
        log.add("CPU " + id + " constructor");
        processTree = new ProcessTree(log);
        this.id = id;
        this.cpuMonitor = cpuMonitor;
        this.log = log;
        t = new Thread(this, "CPU: " + id);
        t.start();
    }

    public void addProcess(Process process) {
        log.add("CPU " + id + ": " + "add process " + process.getPid());
        processTree.addProcess(process);
    }

    public Process pollProcess() {
        Process process = processTree.getProcess();
        log.add("CPU " + id + ": " + "poll process " + process.getPid());        
        cpuMonitor.removeCPU(this);
        cpuMonitor.addCPU(this);
        return process;
    }

    public void run() {
        while(true) {
            Process process = pollProcess();
            log.add("CPU " + id + ": start running process " + process.getPid());
            process.run();
        }
    }
}