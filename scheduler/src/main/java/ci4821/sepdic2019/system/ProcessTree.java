package ci4821.sepdic2019.system;

import java.util.TreeSet;
import java.util.Comparator;

import ci4821.sepdic2019.ds.Log;
import lombok.Getter;

@Getter
public class ProcessTree {
    private TreeSet<Process> tree;
    private final Log log;

    ProcessTree(Log log) {
        this.log = log;
        tree = new TreeSet<Process>(new Comparator<Process> () {
            @Override
            public int compare(Process p1, Process p2) {
                int comp = p1.getVruntime().compareTo(p2.getVruntime());
                if (comp == 0) {
                    return (new Integer(p1.getPid())).compareTo(p2.getPid());
                }
                return comp;
            }
        });
    }

    public synchronized void addProcess(Process process) {
        tree.add(process);
        notify();
    }

    public synchronized Process getProcess() {
        while(tree.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.add("ProcessTree interrupted.");
            }
        }
        return tree.pollFirst();
    }

    public int size() {
        return tree.size();
    }
}
