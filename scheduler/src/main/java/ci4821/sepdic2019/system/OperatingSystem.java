package ci4821.sepdic2019.system;

import java.util.TreeSet;
import java.util.stream.Collectors;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

@Data
public class OperatingSystem {
    private final CPUTreeMonitor cpuTreeMonitor;
    private final AllocatedCPUMonitor allocatedCPUMonitor;
    private final StatusMapMonitor statusMapMonitor;
    private final Resource resource;
    private final Log log;
    private final Integer loadBalancerTime;
    private final LoadBalancer loadBalancer;
    private final Timer timer;
    private final Clock clock;
    
    public OperatingSystem(
        CPUTreeMonitor cpuTreeMonitor, 
        AllocatedCPUMonitor allocatedCPUMonitor,
        StatusMapMonitor statusMapMonitor, 
        Resource resource, 
        Integer loadBalancerTime, 
        Log log,
        Clock clock
    ) {
        this.cpuTreeMonitor = cpuTreeMonitor;
        this.allocatedCPUMonitor = allocatedCPUMonitor;
        this.statusMapMonitor = statusMapMonitor;
        this.loadBalancerTime = loadBalancerTime;
        this.resource = resource;
        this.log = log;

        this.loadBalancer = new LoadBalancer(cpuTreeMonitor, allocatedCPUMonitor,
            log, loadBalancerTime);

        this.clock = clock;
        this.timer = new Timer(clock, cpuTreeMonitor);
    }

    public void createProcess(ArrayList<Object> procs) {

        ArrayList<Map<String, Object>> ordered_procs =
            new ArrayList<Map<String, Object>> (procs.stream()
                .map(p -> (HashMap<String, Object>) p)
                .collect(Collectors.toList()));

        Collections.sort(
            ordered_procs,
            new Comparator<Map<String, Object>> () {
                @Override
                public int compare(Map<String, Object> p1, Map<String, Object> p2) {
                    int comp = new Integer(Integer.parseInt(p1.get("time").toString()))
                                .compareTo(Integer.parseInt(p2.get("time").toString()));
                    if (comp == 0) {
                        return new Integer(Integer.parseInt(p1.get("pid").toString()))
                                .compareTo(Integer.parseInt(p2.get("pid").toString()));
                    }
                    return comp;
                }
            }
        );

        int i = 0;
        while (i < ordered_procs.size()) {
            // Objeto con la información necesaria para simular al proceso
            Map<String, Object> process = ordered_procs.get(i);
            while (clock.getClock() < Integer.parseInt(process.get("time").toString()));
            List<Object> tasks = (ArrayList<Object>) process.get("tasks");
            Deque<Integer> taskIterator = new LinkedList<> (tasks.stream()
                .map(Object::toString)
                .map(x -> Integer.parseInt(x))
                .collect(Collectors.toList()));

            CPU nextCPU = cpuTreeMonitor.pollIdle(); // CPU con menos carga
            int pid = Integer.parseInt(process.get("pid").toString());

            Process newProcess = new Process(
                pid, 
                taskIterator, 
                Double.parseDouble(process.get("pid").toString()), 
                resource, 
                log, 
                cpuTreeMonitor, 
                allocatedCPUMonitor, 
                statusMapMonitor,
                clock.getClock(),
                clock
            );

            log.add_proc(process.get("pid").toString(), process.get("priority").toString(), process.get("time").toString(), "CREATED", "");

            // Agregamos el proceso a los que esperan por este CPU            
            statusMapMonitor.setStatus(newProcess, Status.READY);
            log.add_proc(process.get("pid").toString(), process.get("priority").toString(), process.get("time").toString(), "READY", Integer.toString(nextCPU.getId()));
            allocatedCPUMonitor.setAllocatedCPU(newProcess, nextCPU);

            // Actualizamos el conjunto de CPUs
            cpuTreeMonitor.addCPU(nextCPU);

            i++;
        }
    }
}