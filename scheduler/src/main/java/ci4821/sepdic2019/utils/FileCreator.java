package ci4821.sepdic2019.utils;

import java.io.File;
import java.io.PrintWriter;
import java.lang.Math;

public class FileCreator {
    static String path = "src/main/resources/";
    static String dash = "  - ";
    static String tab = "    ";

    public static void createFile(String fileName, String[] args) {
        File file = new File(path + fileName);
        if (file.exists()) {
            return;
        }

        int n = args.length;
        int cores = n < 2 ? 4 : Integer.parseInt(args[1]);
        int processes = n < 3 ? 15 : Integer.parseInt(args[2]);
        int loadBalancer = n < 4 ? 5000 : Integer.parseInt(args[3]);
        int cycle = n < 5 ? 300 : Integer.parseInt(args[4]);

        getRandomContent(fileName, cores, processes, loadBalancer, cycle);
    }
    private static int getRandomInt(int a, int b) {
        b = Math.max(a, b);
        double z = Math.random();
        int value = (int) (z*(b-a+1)) + a;
        return value;
    }
    private static void getRandomContent(
        String fileName,
        int cores,
        int processes,
        int loadBalancer,
        int cycle
    ) {
        try {
            PrintWriter file = new PrintWriter(path + fileName);
            file.println("cores: \"" + cores + "\"");
            file.println("load-balancer: \"" + loadBalancer + "\"");
            file.println("cycle: \"" + cycle + "\"");
            file.println("processes:");
            for (int i = 0; i < processes; i++) {
                file.println(dash + "pid: \"" + i + "\"");
                file.println(tab + "time: \"" + getRandomInt(1, 3*processes/2) + "\"");
                file.println(tab + "priority: \"" + getRandomInt(1, 10) + "\"");
                file.println(tab + "tasks:");
                int tasks = getRandomInt(3, 10);
                for (int j = 0; j < tasks; j++) {
                    int taskTime = getRandomInt(5, 25);
                    if (j%2 == 1){
                        taskTime /= cores;
                    }
                    file.println(tab + dash + "\"" + taskTime + "\"");
                }
                file.println();
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
