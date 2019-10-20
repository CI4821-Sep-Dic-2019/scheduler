package ci4821.sepdic2019.system;

public class GeneralStatistics {
    private final StatusMapMonitor processes;
    private final CPUsMonitor cpus;
    private int running;
    private int blocked;
    private int ready;
    private int busy;
    private int free;
    private double busyPercentage;

    public GeneralStatistics(StatusMapMonitor processes, CPUsMonitor cpus) {
        this.processes = processes;
        this.cpus = cpus;
    }

    public void calculateProcessesStatics() {
        running = 0;
        blocked = 0;
        ready = 0; 
        for (Status status : processes.getStatusMap().values()) {
            if (status == Status.RUNNING) {
                running++;
            } else if (status == Status.BLOCKED) {
                blocked++;
            } else {
                ready++;
            }
        }
    }

    public void calculateCPUsStatics() {
        busy = 0;
        int total = cpus.getCpus().size();
        for (CPU cpu : cpus.getCpus()) {
            if (cpu.isBusy()){
                busy++;
            }
        }
        free = total - busy;
        busyPercentage = (double) busy / (double) total * 100;
    }

    public int runningProcesses() {
        return running;
    }

    public int blockedProcesses() {
        return blocked;
    }

    public int readyProcesses() {
        return ready;
    }

    public int busyCPUs() {
        return busy;
    }

    public int freeCPUs() {
        return free;
    }

    public double busyCPUsPercentage() {
        return busyPercentage;
    }
}