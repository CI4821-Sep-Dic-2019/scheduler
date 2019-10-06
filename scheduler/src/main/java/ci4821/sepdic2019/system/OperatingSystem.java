package ci4821.sepdic2019.system;

import java.util.Set;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

@Data
public class OperatingSystem {
    private final Set<CPU> cpuSet;
    private final Set<Resource> resources;
    private final Log log;
    private final Integer loadBalancerTime;
    private final Integer cpuTime;
    
    public OperatingSystem(Set<CPU> cpuSet, Set<Resource> resources, Integer loadBalancerTime, Integer cpuTime, Log log) {
        this.cpuSet = cpuSet;
        this.loadBalancerTime = loadBalancerTime;
        this.cpuTime = cpuTime;
        this.resources = resources;;
        this.log = log;
    }

    public void createProcess(ArrayList<Object> procs) {
        // Aqui esdonde distribuyo procesos entre los cpu?
        for (int i = 0; i < procs.size(); i++) {
            Map<String, Object> process = (Map<String, Object>)procs.get(0);
            // pasarle un proceso a cada cpu alternativamente
        }
    }
}