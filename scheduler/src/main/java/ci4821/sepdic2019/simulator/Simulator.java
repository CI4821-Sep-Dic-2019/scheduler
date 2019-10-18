package ci4821.sepdic2019.simulator;

import ci4821.sepdic2019.system.OperatingSystem;
import ci4821.sepdic2019.utils.Parser;
import ci4821.sepdic2019.ds.Log;
import ci4821.sepdic2019.system.AllocatedCPUMonitor;
import ci4821.sepdic2019.system.CPU;
import ci4821.sepdic2019.system.CPUTreeMonitor;
import ci4821.sepdic2019.system.Resource;
import ci4821.sepdic2019.system.StatusMapMonitor;
import ci4821.sepdic2019.system.Clock;

import java.util.Map;
import java.util.ArrayList;

public class Simulator {
    
    OperatingSystem system = null;
    Map<String, Object> data = null;
    Log log;
    CPUTreeMonitor cpuTreeMonitor;
    AllocatedCPUMonitor allocatedCPUMonitor;
    StatusMapMonitor statusMapMonitor;
    Clock clock;

    public Simulator() {

        Parser parseObj = new Parser("params.yml");
        this.data = parseObj.parseFile();

        this.log = new Log();
        
        cpuTreeMonitor = new CPUTreeMonitor(log);

        statusMapMonitor = new StatusMapMonitor(log);

        allocatedCPUMonitor = new AllocatedCPUMonitor(log, cpuTreeMonitor, statusMapMonitor);
        
        Integer loadBalancerTime = Integer.parseInt(data.get("load-balancer").toString());
        log.add("Load Balancer intervals time: " + loadBalancerTime);
        
        Integer cpuTime = Integer.parseInt(data.get("cycle").toString());
        log.add("CPU cycles time: " + cpuTime);
        this.clock = new Clock(cpuTime, log);
        
        generateCPUs(cpuTreeMonitor);

        log.add("Initializing operating system...");
        this.system = new OperatingSystem(
            cpuTreeMonitor, 
            allocatedCPUMonitor, 
            statusMapMonitor, 
            new Resource("I/O", this.log), 
            loadBalancerTime, 
            log,
            clock);
        
    }

    private void generateCPUs(CPUTreeMonitor cpuTreeMonitor) {
        Integer cores = Integer.parseInt(data.get("cores").toString());

        for (int i = 0; i < cores; i++) {
            CPU cpu = new CPU(
                i,
                allocatedCPUMonitor,
                cpuTreeMonitor,
                statusMapMonitor,
                log,
                clock);
            log.add("Creating cpu: " + i);
            cpuTreeMonitor.addCPU(cpu);
        }
    }

    public void startSimulation() {
        // Call createProcess from here
        log.add("Start simulation");
        ArrayList<Object> procs = (ArrayList<Object>)data.get("processes");
        system.createProcess(procs);
    }
}
