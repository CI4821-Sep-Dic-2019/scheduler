package ci4821.sepdic2019.inter;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

public class SimulatorFrame {
    
    /**
     * Creates new form SimulatorFrame
     */
    JFrame frame;
    JTable logs_table;
    DefaultTableModel model_log;
    JTable proc_table;
    DefaultTableModel model_proc;		

    public SimulatorFrame() {
        frame= new JFrame();
        frame.setTitle("LINUX SCHEDULER");
		frame.setSize(800, 600);
		frame.setLocation(200, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        JPanel main_panel = new JPanel();
        main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));
        // PROCESS
        JPanel panel_proc = new JPanel();
        panel_proc.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEmptyBorder(20, 50, 50, 50), "System Processes", TitledBorder.LEFT,
        TitledBorder.TOP));
        proc_table = new JTable();
        proc_table.setBounds(0, 0, 200, 300);
        model_proc = new DefaultTableModel();
        String header_proc[] = new String[] { "ID", "PRIORITY", "TIME", "STATUS", "CPU"};
        model_proc.setColumnIdentifiers(header_proc);
        proc_table.setModel(model_proc);
        panel_proc.add(new JScrollPane(proc_table));
        // LOGS
        JPanel panel_log = new JPanel();
        panel_log.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEmptyBorder(20, 50, 50, 50), "System Logs", TitledBorder.LEFT,
        TitledBorder.TOP));
        logs_table = new JTable();
        logs_table.setBounds(30, 40, 200, 300);
        model_log = new DefaultTableModel();
        String header_log[] = new String[] { "EVENT"};
        model_log.setColumnIdentifiers(header_log);
        logs_table.setModel(model_log);
        panel_log.add(new JScrollPane(logs_table));

        

        main_panel.add(panel_proc);
        main_panel.add(panel_log);
        frame.add(main_panel);
        this.makeVisible();
    }

    public void addlogRow(String process_id) {
        model_log.addRow(new Object[] {process_id});
        
    }

    public void addprocRow(String process_id, String prio, String time, String stat, String cpu) {
        model_proc.addRow(new Object[] {process_id, prio, time, stat, cpu});
        
    }

    public void makeVisible() {
        frame.setVisible(true);
    }
    
}