import java.util.*;
import java.io.*;
import java.net.*;

import sim.Token;
import sim.protocol.Protocol;
import sim.protocol.TokenRequest;
import utils.*;

public class Manager {
	// Args: num_nodes => write to stdout the ip:port of manager
	public static void _main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Error: Invalid number of arguments");
			System.out.println("Usage: java Manager num_nodes room_capacity");
			return;
		}

		// Create server
		ServerSocket serverSocket = new ServerSocket(0);
		FullAddress managerAddress = FullAddress.fromSocket(serverSocket);
		System.out.println("Manager started at " + managerAddress);

		// Write address to file file for automation
		File server_file = new File("./data/manager_address.txt");
		server_file.getParentFile().mkdirs();
		server_file.createNewFile();
		FileWriter writer = new FileWriter(server_file);
		writer.write(managerAddress + "\n");
		writer.close();

		// Wait for nodes
		int numNodes = Integer.parseInt(args[0]);
		System.out.println("Waiting for " + numNodes + " nodes to connect");
		ArrayList<FullAddress> nodes = new ArrayList<>();
		for (int i = 0; i < numNodes; i++) {
			// Get message
			Socket socket = serverSocket.accept();
			String node_message = new String(socket.getInputStream().readAllBytes());
			socket.close();

			// Parse message
			FullAddress node_address = FullAddress.fromString(node_message);
			System.out.println("Node " + i + " registered with callback address: " + node_address);

			nodes.add(node_address);
		}

		// Write node addresses to file for automation
		File nodes_file = new File("./data/nodes_addresses.txt");
		nodes_file.getParentFile().mkdirs();
		nodes_file.createNewFile();
		writer = new FileWriter(nodes_file);
		for (FullAddress node : nodes) {
			writer.write(node + "\n");
		}
		writer.close();

		// Callback nodes with their next node
		for (int i = 0; i < numNodes; i++) {
			FullAddress current = nodes.get(i);
			FullAddress next = nodes.get((i + 1) % numNodes);

			System.out.println("Calling back " + current + " with " + next);

			Socket socket = new Socket(current.ip, current.port);
			socket.getOutputStream().write(next.toString().getBytes());
			socket.close();
		}

		// Create initial token
		int roomCapacity = Integer.parseInt(args[1]);
		Token initialToken = new Token(roomCapacity);

		// Sending start message to node 0
		System.out.println("Sending start message to node 0");
		FullAddress node0 = nodes.get(0);
		Protocol.send(node0, new TokenRequest(initialToken));

		System.out.println("Nodes setup complete, exiting");
		serverSocket.close();
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
