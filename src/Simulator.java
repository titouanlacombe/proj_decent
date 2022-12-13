import java.io.*;
import java.net.*;

import sim.Room;
import sim.protocol.*;
import utils.FullAddress;
import utils.NormalGenerator;

public class Simulator {
	Config config;
	long lastUpdate;
	Room room;

	public Simulator(Config config) {
		this.config = config;
		this.lastUpdate = System.currentTimeMillis();
		this.room = new Room(
				config.timeScale, config.entryRate,
				new NormalGenerator(config.visitTimeMean, config.visitTimeStdDev, config.randSeed));
	}

	// Args: num_nodes => write to stdout the ip:port of manager
	public void _main(String[] args) throws Exception {
		// Create server
		ServerSocket serverSocket = new ServerSocket(0);
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
				"java", "-cp", "bin", "Manager",
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
				"java", "-cp", "bin", "Node",
				managerAddress.toString(),
				simulatorAddress.toString());
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

		// Recover nodes addresses from nodes_addresses.txt
		System.out.println("Recovering nodes addresses");
		reader = new BufferedReader(new FileReader(nodesFile));
		FullAddress[] nodesAddresses = new FullAddress[config.numNodes];
		for (int i = 0; i < config.numNodes; i++) {
			nodesAddresses[i] = FullAddress.fromString(reader.readLine());
		}
		reader.close();

		// Set controllers in room
		room.setControllers(nodesAddresses);

		// Start simulation
		System.out.println("Startup complete, starting simulation");
		while (true) {
			Socket clientSocket = serverSocket.accept();
			boolean exit = this.handleRequest(clientSocket);
			clientSocket.close();

			if (exit) {
				break;
			}
		}

		// Kill nodes
		System.out.println("Killing nodes");
		for (int i = 0; i < config.numNodes; i++) {
			nodes[i].destroy();
		}

		System.out.println("Done");
	}

	public boolean handleRequest(Socket clientSocket) throws Exception {
		Request request = Protocol.recv(clientSocket);
		System.out.println("[SIMULATOR] " + request);

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

	public void simulationUpdate(SimulationUpdateRequest request) throws Exception {
		room.update(request.sender_uuid, request.controller);
	}

	public static void main(String[] args) {
		Simulator simulator = new Simulator(Config._default());

		try {
			simulator._main(args);
		} catch (Exception e) {
			System.out.println("Error in main thread");
			e.printStackTrace();
		}
	}
}
