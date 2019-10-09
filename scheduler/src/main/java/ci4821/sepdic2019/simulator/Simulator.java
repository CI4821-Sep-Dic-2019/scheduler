package ci4821.sepdic2019.simulator;

import ci4821.sepdic2019.system.OperatingSystem;
import ci4821.sepdic2019.utils.Parser;
import ci4821.sepdic2019.ds.Log;
import ci4821.sepdic2019.system.CPU;
import ci4821.sepdic2019.system.Resource;

import java.util.Map;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Comparator;

public class Simulator {
    
    OperatingSystem system = null;
    Map<String, Object> data = null;
    Log log;

    public Simulator() {

        Parser parseObj = new Parser("params.yml");
        this.data = parseObj.parseFile();

        this.log = new Log();
        
        TreeSet<CPU> cpuSet = generateCPUs();
        
        Integer loadBalancerTime = Integer.parseInt(data.get("load-balancer").toString());
        log.add("Load Balancer intervals time: " + loadBalancerTime);
        
        Integer cpuTime = Integer.parseInt(data.get("cycle").toString());
        log.add("CPU cycles time: " + cpuTime);
        
        log.add("Initializing operating system...");
        this.system = new OperatingSystem(cpuSet, new Resource("I/O", this.log), loadBalancerTime, cpuTime, log);
        
    }

    private TreeSet<CPU> generateCPUs() {
        TreeSet<CPU> cpuSet = new TreeSet<CPU>(new Comparator<CPU> () {
            @Override
            public int compare(CPU cpu1, CPU cpu2) {
                int comp = cpu1.getProcessTree().size() - cpu2.getProcessTree().size();
                if (comp == 0) {
                    return cpu1.getId() - cpu2.getId();
                }
                return comp;
            }
        });
        Integer cores = Integer.parseInt(data.get("cores").toString());

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
        system.createProcess(procs);
    }
}
