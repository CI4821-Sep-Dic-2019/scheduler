package ci4821.sepdic2019.system;

import java.util.TreeSet;
import java.util.stream.Collectors;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Data
public class OperatingSystem {
    private final Monitor cpuMonitor;
    private final Resource resource;
    private final Log log;
    private final Integer loadBalancerTime;
    private final Integer cpuTime;
    private final LoadBalancer loadBalancer;
    
    public OperatingSystem(Monitor cpuMonitor, Resource resource, Integer loadBalancerTime, Integer cpuTime, Log log) {
        log.add("Operating System constructor");
        this.cpuMonitor = cpuMonitor;
        this.loadBalancerTime = loadBalancerTime;
        this.cpuTime = cpuTime;
        this.resource = resource;
        this.log = log;
        this.loadBalancer = new LoadBalancer(cpuMonitor, log, loadBalancerTime);
    }

    public void createProcess(ArrayList<Object> procs) {
        for (int i = 0; i < procs.size(); i++) {
            // Objeto con la información necesaria para simular al proceso
            Map<String, Object> process = (HashMap<String, Object>) procs.get(i);
            List<Object> tasks = (ArrayList<Object>) process.get("tasks");

            log.add("A");
            CPU nextCPU = cpuMonitor.pollIdle(); // CPU con menos carga
            log.add("B");
            int pid = Integer.parseInt(process.get("pid").toString());
            // Agregamos el proceso a los que esperan por este CPU
            Process newProcess = Process.builder()
                .pid(pid)
                .taskIterator(tasks.stream()
                    .map(Object::toString)
                    .map(x -> Integer.parseInt(x))
                    .collect(Collectors.toList())
                    .iterator())
                .priority(Double.parseDouble(process.get("pid").toString()))
                .resource(resource)
                .log(log)
                .build();
            
            cpuMonitor.setStatus(newProcess, Status.READY);
            cpuMonitor.setAllocatedCPU(newProcess, nextCPU);

            // Actualizamos el conjunto de CPUs
            cpuMonitor.addCPU(nextCPU);

            log.add("Process " + pid + " created");
        }
    }
}