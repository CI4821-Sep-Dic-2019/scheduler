package ci4821.sepdic2019.ds;
import ci4821.sepdic2019.inter.SimulatorFrame;

import java.text.DecimalFormat;

public class Log {
    
    SimulatorFrame simulator;
    private final DecimalFormat decimalFormat;
    private final DecimalFormat decimalFormatPercentage;

    public Log() {
        simulator = new SimulatorFrame();
        decimalFormat = new DecimalFormat("#0.00");
        decimalFormatPercentage = new DecimalFormat("#00.00%");
    }

    public String doubleToString(Double val) {
        return decimalFormat.format(val);
    }

    public String doubleToStringPercentage(Double val) {
        return decimalFormatPercentage.format(val);
    }

    public synchronized void add_proc(String process_id, String prio, String time, String stat, String cpu) {
        
        simulator.addprocRow(process_id, prio, time, stat, cpu);
        //System.out.println(record);
    }

    public synchronized void add_cpu(String cpu_id, String busy, String proc_number, String work_time, String sleep_time, String usage) {
        
        simulator.addcpuRow(cpu_id, busy, proc_number, work_time, sleep_time, usage);
        //System.out.println(record);
    }

    public synchronized void add_cpu_stats(String busy, String free, String usage) {
        
        simulator.update_cpu_stats(busy, free, usage);
        //System.out.println(record);
    }

    public synchronized void add_proc_stats(String run, String ready, String block) {
        
        simulator.update_proc_stats(run, ready, block);
        //System.out.println(record);
    }
    
    public synchronized void add(String record) {
        
        simulator.addlogRow(record);
        //System.out.println(record);
    }
}