package ci4821.sepdic2019.simulator;

import ci4821.sepdic2019.system.OperatingSystem;
import ci4821.sepdic2019.utils.Parser;

import java.util.Map;

public class Simulator {
    
    OperatingSystem system = null;
    Map<String, Object> data = null;

    public Simulator() {
        this.system = new OperatingSystem();
        Parser parseObj = new Parser("params.yml");
        this.data = parseObj.parseFile();
    }
}
