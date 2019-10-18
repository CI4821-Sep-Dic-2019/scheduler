package ci4821.sepdic2019.system;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;

@Data
public class Clock {
    private int clock;
    private final int slice;
    private final Log log;
    private final String logName;

    public Clock(int slice, Log log) {
        this.clock = 0;
        this.slice = slice;
        this.log = log;
        logName = "[Clock]";
    }

    /**
     * @return clock counter value
     */
    public synchronized int getClock() {
        return clock;
    }

    /**
     * Increment clock counter by one.
     */
    public synchronized void increment() {
        try {
            wait(slice);
        } catch (InterruptedException e) {
            log.add(logName + " Interrupted");
        }
        clock++;
    }
}