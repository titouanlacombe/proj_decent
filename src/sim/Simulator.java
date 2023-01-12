package sim;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import sim.protocol.ExitRequest;
import sim.protocol.Protocol;
import sim.protocol.Request;
import sim.protocol.SimulationUpdateRequest;
import config.Config;
import utils.FullAddress;
import utils.Logging;
import utils.NormalGenerator;

public class Simulator {
	private Config config;
	private Process[] nodes_procs;
	private ServerSocket serverSocket;
	private Room room;

	private String activeUuid = "";

	public Simulator(Config config) throws Exception {
		this.config = config;
		this.room = new Room(config.entryRate,
				new NormalGenerator(config.visitTimeMean, config.visitTimeStdDev, config.randSeed),
				new Clock(config.simulationTimeFactor));
	}

	public Simulator() throws Exception {
		this(Config._default());
	}

	// Args: num_nodes => write to stdout the ip:port of manager
	public void start() throws Exception {
		// Get bin dir from env
		String binDir = System.getenv("BIN_DIR");
		Logging.debug("got BIN_DIR: " + binDir);

		// Create server
		serverSocket = new ServerSocket(0);
		FullAddress simulatorAddress = FullAddress.fromSocket(serverSocket);
		Logging.info("Simulator started at " + simulatorAddress);

		// Delete files from previous runs
		File managerAddressFile = new File("./data/manager_address.txt");
		File nodesFile = new File("./data/nodes_addresses.txt");
		managerAddressFile.delete();
		nodesFile.delete();

		// Start manager subprocess
		Logging.info("Starting manager");
		ProcessBuilder managerBuilder = new ProcessBuilder(
				"java", "-cp", binDir, "Manager",
				String.valueOf(config.numNodes),
				String.valueOf(config.roomCapacity));
		managerBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		managerBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
		Process manager = managerBuilder.start();

		// Wait for newline in manager_address.txt
		Logging.debug("Waiting for manager address");
		while (!managerAddressFile.exists()) {
			Thread.sleep(100);
			if (!manager.isAlive()) {
				Logging.error("Manager exited unexpectedly");
				System.exit(1);
			}
		}
		BufferedReader reader = new BufferedReader(new FileReader(managerAddressFile));
		String managerAddressString = reader.readLine();
		reader.close();
		FullAddress managerAddress = FullAddress.fromString(managerAddressString);

		// Start nodes subprocesses
		Logging.info("Starting " + config.numNodes + " nodes");
		ProcessBuilder nodeBuilder = new ProcessBuilder(
				"java", "-cp", binDir, "Node",
				managerAddress.toString(),
				simulatorAddress.toString(),
				String.valueOf(config.nodeSleepTime));
		nodeBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		nodeBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

		nodes_procs = new Process[config.numNodes];
		for (int i = 0; i < config.numNodes; i++) {
			nodes_procs[i] = nodeBuilder.start();
		}

		// Wait manager to finish
		Logging.debug("Waiting for manager to finish");
		int code = manager.waitFor();
		if (code != 0) {
			Logging.error("Manager exited with code " + code);
			System.exit(code);
		}

		// Recover nodes addresses from nodes_addresses.txt
		Logging.debug("Recovering nodes addresses");
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
		Logging.info("Startup complete, starting simulation");

		// Start server
		Thread serverThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Socket clientSocket = serverSocket.accept();
						boolean exit = handleRequest(clientSocket);
						clientSocket.close();

						if (exit) {
							break;
						}
					}
				} catch (Exception e) {
					Logging.exception(e);
				}
			}
		});

		serverThread.start();

		// Start simulation
		Thread simulationThread = new Thread(new Runnable() {
			public void run() {
				try {
					room.init_sim();
					while (true) {
						// Logging.debug("Room update tick");

						room.update_people();
						Thread.sleep(config.simulationUpdateInterval);
					}
				} catch (Exception e) {
					Logging.exception(e);
				}
			}
		});

		simulationThread.start();
	}

	public void killNodes() {
		if (nodes_procs != null) {
			Logging.debug("Killing nodes");
			for (int i = 0; i < config.numNodes; i++) {
				nodes_procs[i].destroy();
			}
		}
		Logging.info("Done");

	}

	public void simulationUpdate(SimulationUpdateRequest request) throws Exception {
		synchronized (activeUuid) {
			activeUuid = request.sender_uuid;
		}
		room.update_controller(request.sender_uuid, request.controller);
	}

	public boolean handleRequest(Socket clientSocket) throws Exception {
		Request request = Protocol.recv(clientSocket);
		Logging.info(request.toString());

		switch (request.getCode()) {
			case ExitRequest.CODE:
				return true;
			case SimulationUpdateRequest.CODE:
				simulationUpdate((SimulationUpdateRequest) request);
				break;
			default:
				Logging.error("Error: Invalid request code");
				break;
		}

		return false;
	}

	public Room getRoom() {
		return room;
	}

	public String getActiveUuid() {
		synchronized (activeUuid) {
			return activeUuid;
		}
	}
}
