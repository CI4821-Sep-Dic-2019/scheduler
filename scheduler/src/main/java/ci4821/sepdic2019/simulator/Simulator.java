package ci4821.sepdic2019.simulator;

import ci4821.sepdic2019.system.OperatingSystem;
import ci4821.sepdic2019.utils.Parser;
import ci4821.sepdic2019.ds.Log;
import ci4821.sepdic2019.system.AllocatedCPUMonitor;
import ci4821.sepdic2019.system.CPU;
import ci4821.sepdic2019.system.CPUsMonitor;
import ci4821.sepdic2019.system.Resource;
import ci4821.sepdic2019.system.StatusMapMonitor;
import ci4821.sepdic2019.system.Clock;

import java.util.Map;
import java.util.ArrayList;

public class Simulator {
    
    OperatingSystem system = null;
    Map<String, Object> data = null;
    Log log;
    CPUsMonitor cpusMonitor;
    AllocatedCPUMonitor allocatedCPUMonitor;
    StatusMapMonitor statusMapMonitor;
    Clock clock;

    public Simulator() {

        Parser parseObj = new Parser("params.yml");
        this.data = parseObj.parseFile();

        this.log = new Log();
        
        cpusMonitor = new CPUsMonitor(log);

        statusMapMonitor = new StatusMapMonitor(log);

        allocatedCPUMonitor = new AllocatedCPUMonitor(log, cpusMonitor, statusMapMonitor);
        
        Integer loadBalancerTime = Integer.parseInt(data.get("load-balancer").toString());
        log.add("Load Balancer intervals time: " + loadBalancerTime);
        
        Integer cpuTime = Integer.parseInt(data.get("cycle").toString());
        log.add("CPU cycles time: " + cpuTime);
        this.clock = new Clock(cpuTime, log);
        
        generateCPUs(cpusMonitor);

        log.add("Initializing operating system...");
        this.system = new OperatingSystem(
            cpusMonitor,
            allocatedCPUMonitor,
            statusMapMonitor,
            new Resource("I/O", this.log),
            loadBalancerTime,
            log,
            clock);
        
    }

    private void generateCPUs(CPUsMonitor cpusMonitor) {
        Integer cores = Integer.parseInt(data.get("cores").toString());

        for (int i = 0; i < cores; i++) {
            CPU cpu = new CPU(
                i,
                allocatedCPUMonitor,
                cpusMonitor,
                statusMapMonitor,
                log,
                clock);
            log.add("Creating cpu: " + i);
            cpusMonitor.addCPU(cpu);
        }
    }

    public void startSimulation() {
        

        // Call createProcess from here
        log.add("Start simulation");
        ArrayList<Object> procs = (ArrayList<Object>)data.get("processes");
        system.createProcess(procs);
    }
}
