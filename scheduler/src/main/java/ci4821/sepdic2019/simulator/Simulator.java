package ci4821.sepdic2019.simulator;

import ci4821.sepdic2019.system.OperatingSystem;
import ci4821.sepdic2019.utils.Parser;
import ci4821.sepdic2019.ds.Log;
import ci4821.sepdic2019.system.CPU;
import ci4821.sepdic2019.system.Monitor;
import ci4821.sepdic2019.system.Resource;

import java.util.Map;
import java.util.ArrayList;

public class Simulator {
    
    OperatingSystem system = null;
    Map<String, Object> data = null;
    Log log;
    Monitor cpuMonitor;

    public Simulator() {

        Parser parseObj = new Parser("params.yml");
        this.data = parseObj.parseFile();

        this.log = new Log();
        
        cpuMonitor = new Monitor(log);
        generateCPUs(cpuMonitor);
        
        Integer loadBalancerTime = Integer.parseInt(data.get("load-balancer").toString());
        log.add("Load Balancer intervals time: " + loadBalancerTime);
        
        Integer cpuTime = Integer.parseInt(data.get("cycle").toString());
        log.add("CPU cycles time: " + cpuTime);
        
        log.add("Initializing operating system...");
        this.system = new OperatingSystem(cpuMonitor, new Resource("I/O", this.log), loadBalancerTime, cpuTime, log);
        
    }

    private void generateCPUs(Monitor cpuMonitor) {
        Integer cores = Integer.parseInt(data.get("cores").toString());

        for (int i = 0; i < cores; i++) {
            CPU cpu = new CPU(i, cpuMonitor, log);
            log.add("Creating cpu: " + i);
            cpuMonitor.addCPU(cpu);
        }
    }

    public void startSimulation() {
        // Call createProcess from here
        log.add("Start simulation");
        ArrayList<Object> procs = (ArrayList<Object>)data.get("processes");
        system.createProcess(procs);
    }
}
