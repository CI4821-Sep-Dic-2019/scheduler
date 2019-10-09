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
    private final TreeSet<CPU> cpuSet;
    private final Resource resource;
    private final Log log;
    private final Integer loadBalancerTime;
    private final Integer cpuTime;
    
    public OperatingSystem(TreeSet<CPU> cpuSet, Resource resource, Integer loadBalancerTime, Integer cpuTime, Log log) {
        this.cpuSet = cpuSet;
        this.loadBalancerTime = loadBalancerTime;
        this.cpuTime = cpuTime;
        this.resource = resource;
        this.log = log;
    }

    public void createProcess(ArrayList<Object> procs) {
        for (int i = 0; i < procs.size(); i++) {
            // Objeto con la información necesaria para simular al proceso
            Map<String, Object> process = (HashMap<String, Object>) procs.get(i);
            List<Object> tasks = (ArrayList<Object>) process.get("tasks");

            CPU nextCPU = cpuSet.pollFirst(); // CPU con menos carga

            // Agregamos el proceso a los que esperan por este CPU
            nextCPU.addProcess(Process.builder()
                .cpu(nextCPU)
                .pid(Integer.parseInt(process.get("pid").toString()))
                .taskIterator(tasks.stream()
                    .map(Object::toString)
                    .map(x -> Integer.parseInt(x))
                    .collect(Collectors.toList())
                    .iterator())
                .priority(Double.parseDouble(process.get("pid").toString()))
                .resource(resource)
                .log(log)
                .build());

            // Actualizamos el conjunto de CPUs
            cpuSet.add(nextCPU);
        }
    }
}