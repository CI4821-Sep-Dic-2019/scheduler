package ci4821.sepdic2019.system;

import lombok.Data;

/**
 * This is the thread that keeps incrementing
 * clock.
 */

@Data
public class Timer implements Runnable {
    private final Clock clock;
    private Thread t;
    private final CPUsMonitor cpusMonitor;
    private GeneralStatistics generalStatistics;

    public Timer (Clock clock, CPUsMonitor cpusMonitor, GeneralStatistics generalStatistics) {
        this.clock = clock;
        this.cpusMonitor = cpusMonitor;
        this.generalStatistics = generalStatistics;
        t = new Thread(this, "Timer");
        t.start();
    }

    public void run() {
        while(true) {
            System.out.println("Time: " + clock.getClock());
            clock.increment();
            cpusMonitor.updateCPUsUsage();
            generalStatistics.calculateProcessesStatics();
            generalStatistics.calculateCPUsStatics();
            System.out.println(
                "Running Processes = " + generalStatistics.runningProcesses() +
                ", Blocked Processes= " + generalStatistics.blockedProcesses() +
                ", Ready Processes= " + generalStatistics.readyProcesses()
            );
            System.out.println(
                "Busy CPUs = " + generalStatistics.busyCPUs() +
                ", Free CPUs = " + generalStatistics.freeCPUs() +
                ", Percentage = " + generalStatistics.busyCPUsPercentage()
            );

        }
    }
}