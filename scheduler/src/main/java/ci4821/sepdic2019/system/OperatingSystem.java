package ci4821.sepdic2019.system;

import java.util.Set;

import ci4821.sepdic2019.ds.Log;
import lombok.Data;

@Data
class OperatingSystem {
    private final Set<CPU> cpuSet;
    private final Set<Resource> resources;
    private final Log log;

    public void createProcess(Process process) {

    }
}