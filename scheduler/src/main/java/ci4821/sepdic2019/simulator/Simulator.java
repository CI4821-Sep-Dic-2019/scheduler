package ci4821.sepdic2019.simulator;

import ci4821.sepdic2019.system.OperatingSystem;
import ci4821.sepdic2019.system.Process;
import ci4821.sepdic2019.utils.Parser;
import ci4821.sepdic2019.ds.Log;
import ci4821.sepdic2019.system.CPU;
import ci4821.sepdic2019.system.Resource;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class Simulator {
    
    OperatingSystem system = null;
    Map<String, Object> data = null;
    Log log;

    public Simulator() {

        Parser parseObj = new Parser("params.yml");
        this.data = parseObj.parseFile();

        this.log = new Log();
        
        Set<CPU> cpuSet = generateCPUs();
        
        Integer loadBalancerTime = Integer.parseInt(""+data.get("load-balancer"));
        log.add("Load Balancer intervals time: " + loadBalancerTime);
        
        Integer cpuTime = Integer.parseInt(""+data.get("cycle"));
        log.add("CPU cycles time: " + cpuTime);

        Set<Resource> resources = new HashSet<>();
        resources.add(new Resource("I/O", this.log));
        
        log.add("Initializing operating system...");
        this.system = new OperatingSystem(cpuSet, resources, loadBalancerTime, cpuTime, log);
        
    }

    private Set<CPU> generateCPUs() {
        Set<CPU> cpuSet = new HashSet<>();
        Integer cores = Integer.parseInt(""+data.get("cores"));

        for (int i = 0; i < cores; i++) {
            CPU cpu = new CPU(i, log);
            cpuSet.add(cpu);
            log.add("Creating cpu: " + i);
        }

        return cpuSet;
    }

    public void startSimulation() {
        // Call createProcess from here
        ArrayList<Object> procs = (ArrayList<Object>)data.get("processes");
        system.createProcess((procs));
    }
}
