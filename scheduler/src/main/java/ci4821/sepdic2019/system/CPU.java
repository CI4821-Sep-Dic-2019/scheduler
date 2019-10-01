package ci4821.sepdic2019.system;

import java.util.Optional;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;

@Getter
public class CPU implements Runnable {
    private final int id;
    private final Log log;
    private Optional<Process> processOptional = Optional.empty();
    private Thread t;
    public CPU(int id, Log log) {
        this.id = id;
        this.log = log;
        t = new Thread(this, "Core: " + id);
        t.start();
    }

    public synchronized void setProcess(Process process) {
        this.processOptional = Optional.ofNullable(process);
        log.add("CPU " + id + ": set process " + process.getPid());
        notify();
    }

    public synchronized void setProcessOptional(Optional<Process> procOptional) {
        this.processOptional = procOptional;
    }

    public synchronized Optional<Process> getProcessOptional() {
        return processOptional;
    }

    public synchronized void run() {
        while (!processOptional.isPresent()) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.add("Core " + id + " interrupted.");
            }
        }
        log.add("Core " + id + ": start running process " + processOptional.map(Process::getPid).get());
        processOptional.get().run();
        setProcessOptional(Optional.empty());
        notify();
        run();
    }
}