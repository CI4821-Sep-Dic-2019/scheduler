package ci4821.sepdic2019;
import ci4821.sepdic2019.simulator.Simulator;
import ci4821.sepdic2019.utils.FileCreator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main (String[] args) throws InterruptedException {

        int n = args.length;
        String fileName = n < 1 ? "params.yml" : args[0];

        FileCreator.createFile(fileName, args);
        Simulator simulator = new Simulator(fileName);
        simulator.startSimulation();
    }
}
