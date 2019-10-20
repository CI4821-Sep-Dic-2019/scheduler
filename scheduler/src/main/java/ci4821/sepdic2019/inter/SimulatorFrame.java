package ci4821.sepdic2019.inter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;


public class SimulatorFrame {

    /**
     * Creates new form SimulatorFrame
     */

    JFrame frame;
    JTable logs_table;
    DefaultTableModel model_log;
    JTable proc_table;
    DefaultTableModel model_proc;
    JTable cpu_table;
    DefaultTableModel model_cpu;
    JPanel main_panel;
    // Array of processes IDs, the position of the process in the array
    // corresponds to the rown number of the process in the table
    ArrayList<String> proc_row;
    ArrayList<String> cpu_row;

    JLabel num_proc_run;
    JLabel num_proc_ready;
    JLabel num_proc_block;
    JLabel num_cpu_busy;
    JLabel num_cpu_free;
    JLabel cpu_usage;

    public SimulatorFrame() {

        proc_row = new ArrayList<String>();
        cpu_row = new ArrayList<String>();

        // Main window
        frame = new JFrame();
        frame.setTitle("LINUX SCHEDULER SIMULATOR");
        frame.setSize(800, 600);
        frame.setLocation(200, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        main_panel = new JPanel();
        main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));
        JTabbedPane tabbedPane = new JTabbedPane();

        //STATISTICS
        JPanel panel_stat = new JPanel();
        panel_stat.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50),
        "Statistics", TitledBorder.CENTER, TitledBorder.TOP));
        panel_stat.setLayout(new GridLayout(0,2));
        panel_stat.add(new JLabel("Processes", JLabel.CENTER));
        panel_stat.add(new JLabel("CPUs", JLabel.CENTER));

        // process statistics
        JPanel proc_stats = new JPanel();
        proc_stats.setLayout(new GridLayout(0,2));
        proc_stats.add(new JLabel("Running: ", JLabel.LEFT));
        num_proc_run = (new JLabel("0", JLabel.LEFT));
        proc_stats.add(num_proc_run);

        proc_stats.add(new JLabel("Ready: ", JLabel.LEFT));
        num_proc_ready = (new JLabel("0", JLabel.LEFT));
        proc_stats.add(num_proc_ready);

        proc_stats.add(new JLabel("Bloked: ", JLabel.LEFT));
        num_proc_block = (new JLabel("0", JLabel.LEFT));
        proc_stats.add(num_proc_block);

        // cpu statistics
        JPanel cpu_stats = new JPanel();
        cpu_stats.setLayout(new GridLayout(0,2));
        cpu_stats.add(new JLabel("Busy: ", JLabel.LEFT));
        num_cpu_busy = (new JLabel("0", JLabel.LEFT));
        cpu_stats.add(num_cpu_busy);

        cpu_stats.add(new JLabel("Free: ", JLabel.LEFT));
        num_cpu_free = (new JLabel("0", JLabel.LEFT));
        cpu_stats.add(num_cpu_free);

        cpu_stats.add(new JLabel("Usage: ", JLabel.LEFT));
        cpu_usage = (new JLabel("0 %", JLabel.LEFT));
        cpu_stats.add(cpu_usage);

        panel_stat.add(proc_stats);
        panel_stat.add(cpu_stats);
        

        // PROCESS
        JPanel panel_proc = new JPanel();
        panel_proc.setLayout(new BorderLayout());
        panel_proc.setPreferredSize(new Dimension(500, 500));
        proc_table = new JTable();
        proc_table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        model_proc = new DefaultTableModel();
        String header_proc[] = new String[] { "ID", "PRIORITY", "VRUNTIME", "STATUS", "CPU" };
        model_proc.setColumnIdentifiers(header_proc);
        proc_table.setModel(model_proc);
        panel_proc.add(new JScrollPane(proc_table), BorderLayout.CENTER);

        // CPUs
        JPanel panel_cpu = new JPanel();
        panel_cpu.setLayout(new BorderLayout());
        panel_cpu.setPreferredSize(new Dimension(500, 500));
        cpu_table = new JTable();
        cpu_table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        model_cpu = new DefaultTableModel();
        String header_cpu[] = new String[] { "ID", "BUSY", "PROCESS NUMBER", "WORKING TIME", "SLEEP TIME", "USAGE" };
        model_cpu.setColumnIdentifiers(header_cpu);
        cpu_table.setModel(model_cpu);
        panel_cpu.add(new JScrollPane(cpu_table), BorderLayout.CENTER);

        // LOGS
        JPanel panel_log = new JPanel();
        panel_log.setLayout(new BorderLayout());
        panel_log.setPreferredSize(new Dimension(500, 500));
        logs_table = new JTable();
        logs_table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        model_log = new DefaultTableModel();
        String header_log[] = new String[] { "EVENT" };
        model_log.setColumnIdentifiers(header_log);
        logs_table.setModel(model_log);
        panel_log.add(new JScrollPane(logs_table), BorderLayout.CENTER);

        main_panel.add(panel_stat);
        tabbedPane.add("Processes", panel_proc);
        tabbedPane.add("CPUs", panel_cpu);
        tabbedPane.add("System Logs", panel_log);
        main_panel.add(tabbedPane);
        frame.add(main_panel);
        this.makeVisible();
    }

    public void addlogRow(String log) {
        model_log.addRow(new Object[] { log });

    }

    public void addprocRow(String process_id, String prio, String time, String stat, String cpu) {

        for (int i = 0; i < proc_row.size(); i++) {
            if (proc_row.get(i).equals(process_id)) {
                proc_table.setValueAt(process_id, i, 0);
                proc_table.setValueAt(prio, i, 1);
                proc_table.setValueAt(time, i, 2);
                proc_table.setValueAt(stat, i, 3);
                proc_table.setValueAt(cpu, i, 4);
                return;
            }
        }
        model_proc.addRow(new Object[] {process_id, prio, time, stat, cpu});
        proc_row.add(process_id);

    }
    
    public void addcpuRow(String cpu_id, String busy, String proc_number, String work_time, String sleep_time, String usage) {

        for (int i = 0; i < cpu_row.size(); i++) {
            if (cpu_row.get(i).equals(cpu_id)) {
                cpu_table.setValueAt(cpu_id, i, 0);
                cpu_table.setValueAt(busy, i, 1);
                cpu_table.setValueAt(proc_number, i, 2);
                cpu_table.setValueAt(work_time, i, 3);
                cpu_table.setValueAt(sleep_time, i, 4);
                cpu_table.setValueAt(usage, i, 5);
                return;
            }
        }
        model_cpu.addRow(new Object[] {cpu_id, busy, proc_number, work_time, sleep_time, usage});
        cpu_row.add(cpu_id);

    }

    public void update_proc_stats(String run, String ready, String block) {
        num_proc_run.setText(run);
        num_proc_ready.setText(ready);
        num_proc_block.setText(block);
    }

    public void update_cpu_stats(String busy, String free, String usage) {
        num_cpu_busy.setText(busy);
        num_cpu_free.setText(free);
        cpu_usage.setText(usage + " %");
    }

    public void makeVisible() {
        frame.setVisible(true);
    }
    
}