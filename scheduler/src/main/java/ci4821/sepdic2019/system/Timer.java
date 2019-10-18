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
    public Timer (Clock clock) {
        this.clock = clock;
        t = new Thread(this, "Timer");
        t.start();
    }

    public void run() {
        while(true) {
            clock.increment();
        }
    }
}