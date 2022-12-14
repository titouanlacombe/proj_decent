package sim;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import sim.protocol.ExitRequest;
import sim.protocol.Protocol;
import sim.protocol.Request;
import sim.protocol.SimulationUpdateRequest;
import ui.MainWindow;
import config.Config;
import utils.FullAddress;
import utils.NormalGenerator;

public class Simulator {

    private Config config;
    private Process[] nodes_procs;
    private ServerSocket serverSocket;
    private long lastUpdate;
    private Room room;

    public Simulator(Config config) {
        this.config = config;
        this.lastUpdate = System.currentTimeMillis();
        this.room = new Room(
                config.timeScale, config.entryRate,
                new NormalGenerator(config.visitTimeMean, config.visitTimeStdDev, config.randSeed));
    }

    public Simulator() {
        this(Config._default());
    }

    // Args: num_nodes => write to stdout the ip:port of manager
    public void start() throws Exception {

        // Create server
        serverSocket = new ServerSocket(0);
        FullAddress simulatorAddress = FullAddress.fromSocket(serverSocket);
        System.out.println("Simulator started at " + simulatorAddress);

        // Delete files from previous runs
        File managerAddressFile = new File("./data/manager_address.txt");
        File nodesFile = new File("./data/nodes_addresses.txt");
        managerAddressFile.delete();
        nodesFile.delete();

        // Start manager subprocess
        System.out.println("Starting manager");
        ProcessBuilder managerBuilder = new ProcessBuilder(
                "java", "-cp", "src", "Manager",
                String.valueOf(config.numNodes),
                String.valueOf(config.roomCapacity));
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
                "java", "-cp", "src", "Node",
                managerAddress.toString(),
                simulatorAddress.toString());
        nodeBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        nodeBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        nodes_procs = new Process[config.numNodes];
        for (int i = 0; i < config.numNodes; i++) {
            nodes_procs[i] = nodeBuilder.start();
        }

        // Wait manager to finish
        System.out.println("Waiting for manager to finish");
        int code = manager.waitFor();
        if (code != 0) {
            System.out.println("Manager exited with code " + code);
            System.exit(code);
        }

        // Recover nodes addresses from nodes_addresses.txt
        System.out.println("Recovering nodes addresses");
        reader = new BufferedReader(new FileReader(nodesFile));
        HashMap<String, FullAddress> nodes = new HashMap<String, FullAddress>();
        for (int i = 0; i < config.numNodes; i++) {
            String line = reader.readLine();
            String[] parts = line.split(" ");
            nodes.put(parts[0], FullAddress.fromString(parts[1]));
        }
        reader.close();

        // Set controllers in room
        room.setNodes(nodes);
    }

    public void startSim() throws Exception {
        System.out.println("Startup complete, starting simulation");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            boolean exit = this.handleRequest(clientSocket);
            clientSocket.close();

            if (exit) {
                break;
            }
        }
    }

    public void killNodes() {
        for (int i = 0; i < config.numNodes; i++) {
            nodes_procs[i].destroy();
        }
    }

    public void simulationUpdate(SimulationUpdateRequest request) throws Exception {
        room.update(request.sender_uuid, request.controller);
    }

    public boolean handleRequest(Socket clientSocket) throws Exception {
        Request request = Protocol.recv(clientSocket);
        System.out.println("\n[SIMULATOR] " + request);

        switch (request.getCode()) {
            case ExitRequest.CODE:
                return true;
            case SimulationUpdateRequest.CODE:
                simulationUpdate((SimulationUpdateRequest) request);
                break;
            default:
                System.out.println("Error: Invalid request code");
                break;
        }

        return false;
    }
}
