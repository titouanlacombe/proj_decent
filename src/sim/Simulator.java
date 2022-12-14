package sim;

import java.io.*;
import java.net.ServerSocket;

import ui.MainWindow;
import utils.Config;
import utils.FullAddress;

public class Simulator {

    private Config config;
    private Process[] nodes;
    private ServerSocket serverSocket;

    public Simulator() {
        this(Config._default());
    }

    public Simulator(Config config) {
        this.config = config;
    }

    // Args: num_nodes => write to stdout the ip:port of manager
    public void start() throws Exception {

        // Create server
        serverSocket = new ServerSocket(0);
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

        nodes = new Process[config.numNodes];
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
    }

    public void startSim() throws Exception {
        // Start simulation
        System.out.println("Startup complete, starting simulation");
        Simulator.serve(serverSocket, config);
    }

    public void killNodes() {
        for (int i = 0; i < config.numNodes; i++) {
            nodes[i].destroy();
        }
    }

    public static void serve(ServerSocket serverSocket, Config config) throws Exception {
        // TODO
    }
}
