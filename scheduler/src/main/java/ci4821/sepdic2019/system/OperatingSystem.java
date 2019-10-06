package ci4821.sepdic2019.system;

import java.util.Set;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;

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

    public void createProcess(Process process) {
        // Aqui esdonde distribuyo procesos entre los cpu?
    }
}