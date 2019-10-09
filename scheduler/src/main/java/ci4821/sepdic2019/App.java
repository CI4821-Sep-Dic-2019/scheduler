package ci4821.sepdic2019;

// import java.util.Arrays;
// import java.util.Iterator;
// import java.time.format.DateTimeFormatter;  
// import java.time.LocalDateTime;    

// import ci4821.sepdic2019.ds.Log;
// import ci4821.sepdic2019.system.CPU;
// import ci4821.sepdic2019.system.Resource;
// import ci4821.sepdic2019.system.Process;
// import ci4821.sepdic2019.utils.Parser;
import ci4821.sepdic2019.simulator.Simulator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main (String[] args) throws InterruptedException {

        Simulator simulator = new Simulator();
        simulator.startSimulation();
    }
}
