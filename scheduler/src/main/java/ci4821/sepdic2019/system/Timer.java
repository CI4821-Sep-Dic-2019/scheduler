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
    private final CPUTreeMonitor cpuTreeMonitor;

    public Timer (Clock clock, CPUTreeMonitor cpuTreeMonitor) {
        this.clock = clock;
        this.cpuTreeMonitor = cpuTreeMonitor;
        t = new Thread(this, "Timer");
        t.start();
    }

    public void run() {
        while(true) {
            System.out.println("Time: " + clock.getClock());
            clock.increment();
            cpuTreeMonitor.updateCPUsUsage();
        }
    }
}