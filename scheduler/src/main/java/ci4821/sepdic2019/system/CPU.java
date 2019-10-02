package ci4821.sepdic2019.system;

import java.util.Optional;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;

@Getter
public class CPU implements Runnable {
    private final int id;
    private final Log log;
    private ProcessTree processTree;
    private Thread t;
    public CPU(int id, Log log) {
        processTree = new ProcessTree(log);
        this.id = id;
        this.log = log;
        t = new Thread(this, "Core: " + id);
        t.start();
    }

    public void addProcess(Process process) {
        log.add("CPU " + id + ": " + "add process " + process.getPid());
        processTree.addProcess(process);
    }

    public Process getProcess() {
        return processTree.getProcess();
    }

    public void run() {
        while(true) {
            Process process = getProcess();
            log.add("Core " + id + ": start running process " + process.getPid());
            process.run();
        }
    }
}