package ci4821.sepdic2019.system;

import java.util.TreeSet;
import java.util.stream.Collectors;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

@Data
public class OperatingSystem {
    private final CPUTreeMonitor cpuTreeMonitor;
    private final AllocatedCPUMonitor allocatedCPUMonitor;
    private final StatusMapMonitor statusMapMonitor;
    private final Resource resource;
    private final Log log;
    private final Integer loadBalancerTime;
    private final Integer cpuTime;
    private final LoadBalancer loadBalancer;
    
    public OperatingSystem(CPUTreeMonitor cpuTreeMonitor, AllocatedCPUMonitor allocatedCPUMonitor,
        StatusMapMonitor statusMapMonitor, Resource resource, 
        Integer loadBalancerTime, Integer cpuTime, Log log
    ) {
        this.cpuTreeMonitor = cpuTreeMonitor;
        this.allocatedCPUMonitor = allocatedCPUMonitor;
        this.statusMapMonitor = statusMapMonitor;
        this.loadBalancerTime = loadBalancerTime;
        this.cpuTime = cpuTime;
        this.resource = resource;
        this.log = log;

        this.loadBalancer = new LoadBalancer(cpuTreeMonitor, allocatedCPUMonitor,
            log, loadBalancerTime);
    }

    public void createProcess(ArrayList<Object> procs) {
        for (int i = 0; i < procs.size(); i++) {
            // Objeto con la información necesaria para simular al proceso
            Map<String, Object> process = (HashMap<String, Object>) procs.get(i);

            List<Object> tasks = (ArrayList<Object>) process.get("tasks");
            Iterator<Integer> taskIterator = tasks.stream()
                .map(Object::toString)
                .map(x -> Integer.parseInt(x))
                .collect(Collectors.toList())
                .iterator();

            CPU nextCPU = cpuTreeMonitor.pollIdle(); // CPU con menos carga
            int pid = Integer.parseInt(process.get("pid").toString());

            Process newProcess = new Process(
                pid, taskIterator, 
                Double.parseDouble(process.get("pid").toString()), 
                resource, log, cpuTreeMonitor, allocatedCPUMonitor, 
                statusMapMonitor, cpuTime);

            // Agregamos el proceso a los que esperan por este CPU            
            statusMapMonitor.setStatus(newProcess, Status.READY);
            allocatedCPUMonitor.setAllocatedCPU(newProcess, nextCPU);

            // Actualizamos el conjunto de CPUs
            cpuTreeMonitor.addCPU(nextCPU);

            log.add("Process " + pid + " created");
        }
    }
}