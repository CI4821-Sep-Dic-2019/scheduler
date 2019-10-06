package ci4821.sepdic2019;

import java.util.Arrays;
import java.util.Iterator;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

import ci4821.sepdic2019.ds.Log;
import ci4821.sepdic2019.system.CPU;
import ci4821.sepdic2019.system.Resource;
import ci4821.sepdic2019.system.Process;
import ci4821.sepdic2019.simulator.Simulator;
import ci4821.sepdic2019.utils.Parser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main (String[] args) throws InterruptedException {

        Simulator simulator = new Simulator();

        // Parser parseObj = new Parser("params.yml");
        // parseObj.parseFile();

        // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        // LocalDateTime now = LocalDateTime.now();  
        // System.out.println(dtf.format(now));  
        
        // Log log = new Log();
        // Resource resource = new Resource("I/O", log);
        // CPU core1 = new CPU(1, log);
        // CPU core2 = new CPU(2, log);

        // Iterator<Integer> itt1 = Arrays.asList(100, 10, 75, 23, 95).iterator();
        // Process process1 = new Process(201, itt1, 0.75, resource, log, core1);
        // core1.addProcess(process1);

        // Iterator<Integer> itt2 = Arrays.asList(25, 100, 15, 96, 23).iterator();
        // Process process2 = new Process(202, itt2, 0.5, resource, log, core2);
        // core2.addProcess(process2);

        // core1.getT().join();
        // core2.getT().join();
    }
}
