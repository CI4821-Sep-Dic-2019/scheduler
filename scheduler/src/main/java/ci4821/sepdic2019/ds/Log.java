package ci4821.sepdic2019.ds;
import ci4821.sepdic2019.inter.SimulatorFrame;

public class Log {
    SimulatorFrame simulator;

    public Log() {
        simulator = new SimulatorFrame();
    }

    public synchronized void add_proc(String process_id, String prio, String time, String stat, String cpu) {
        
        simulator.addprocRow(process_id, prio, time, stat, cpu);
        //System.out.println(record);
    }
    
    public synchronized void add(String record) {
        
        simulator.addlogRow(record);
        //System.out.println(record);
    }
}