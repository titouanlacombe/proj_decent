import java.io.*;
import java.net.*;

import sim.Protocol;
import utils.FullAddress;

public class Simulator {
	// Args: num_nodes => write to stdout the ip:port of manager
	public void _main(String[] args) throws Exception {
		Config config = Config._default();

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
		String message = new String(clientSocket.getInputStream().readAllBytes());
		String command = Protocol.getCommand(message);
		System.out.println("Received command: '" + command + "'");

		switch (command) {
			case Protocol.NODE_UPDATE:
				String uuid = Protocol.receiveUUID(message);
				int[] enteredLeft = Protocol.receiveEnteredLeft(message);
				this.nodeUpdate(uuid, enteredLeft[0], enteredLeft[1]);
				break;
			case Protocol.EXIT:
				return true;
			default:
				System.out.println("Error: Invalid command");
				break;
		}

		return false;
	}

	public void nodeUpdate(String sender_uuid, int entered, int left) {
		System.out.println("Node update from " + sender_uuid + ": " + entered + " entered, " + left + " left");
	}

	public static void main(String[] args) {
		Simulator simulator = new Simulator();
		try {
			simulator._main(args);
		} catch (Exception e) {
			System.out.println("Error in main thread");
			e.printStackTrace();
		}
	}
}
