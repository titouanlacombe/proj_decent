import java.io.*;
import java.net.ServerSocket;

import ui.MainWindow;
import utils.FullAddress;

public class Simulator {
    // Args: num_nodes => write to stdout the ip:port of manager
    public static void _main(String[] args) throws Exception {

        Config config = Config._default();

        // Create UI Window
        MainWindow window = new MainWindow(config.numNodes, config.roomCapacity);
        window.startWindow();

        // Create server
        ServerSocket serverSocket = new ServerSocket(0);
        FullAddress simulatorAddress = FullAddress.fromSocket(serverSocket);
        System.out.println("Simulator started at " + simulatorAddress);

        // Delete manager_address.txt
        File managerAddressFile = new File("./data/manager_address.txt");
        managerAddressFile.delete();

        // Start manager subprocess
        System.out.println("Starting manager");
        ProcessBuilder managerBuilder = new ProcessBuilder(
                "java", "-cp", "bin", "Manager",
                String.valueOf(config.numNodes),
                String.valueOf(config.roomCapacity),
                simulatorAddress.toString());
        managerBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        managerBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process manager = managerBuilder.start();

        // Wait for newline in manager_address.txt
        System.out.println("Waiting for manager address");
        while (!managerAddressFile.exists()) {
            Thread.sleep(100);
            if (!manager.isAlive()) {
                System.out.println("Manager exited unexpectedly");
                System.exit(1);
            }
        }
        BufferedReader reader = new BufferedReader(new FileReader(managerAddressFile));
        String managerAddressString = reader.readLine();
        reader.close();
        FullAddress managerAddress = FullAddress.fromString(managerAddressString);

        // Start nodes subprocesses
        System.out.println("Starting " + config.numNodes + " nodes");
        ProcessBuilder nodeBuilder = new ProcessBuilder(
                "java", "-cp", "bin", "Node",
                managerAddress.toString());
        nodeBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        nodeBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        Process[] nodes = new Process[config.numNodes];
        for (int i = 0; i < config.numNodes; i++) {
            nodes[i] = nodeBuilder.start();
        }

        // Wait manager to finish
        System.out.println("Waiting for manager to finish");
        int code = manager.waitFor();
        if (code != 0) {
            System.out.println("Manager exited with code " + code);
            System.exit(code);
        }

        window.clearWindow();
        window.simulationWindow(config.numNodes);
        window.updateBar(0, 10);
        window.updateBar(1, 20);
        window.updateBar(2, 30);

        // Start simulation
        System.out.println("Startup complete, starting simulation");
        Simulator.serve(serverSocket, config);

        // Kill nodes
        System.out.println("Killing nodes");
        for (int i = 0; i < config.numNodes; i++) {
            nodes[i].destroy();
        }

        System.out.println("Done");
    }

    public static void serve(ServerSocket serverSocket, Config config) throws Exception {
        // TODO
    }

    public static void main(String[] args) {
        try {
            _main(args);
        } catch (Exception e) {
            System.out.println("Error in main thread");
            e.printStackTrace();
        }
    }
}
