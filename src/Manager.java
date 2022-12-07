import java.util.*;
import java.io.*;
import java.net.*;

import utils.*;

public class Manager {
	// Args: num_nodes => write to stdout the ip:port of manager
	public static void _main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Error: Invalid number of arguments");
			System.out.println("Usage: java Manager num_nodes");
			return;
		}

		// Create server
		ServerSocket serverSocket = new ServerSocket(0);
		FullAddress managerAddress = FullAddress.fromSocket(serverSocket);
		System.out.println("Manager started at " + managerAddress);

		// Write address to "server.txt" file for automation
		File server_file = new File("./data/manager_address.txt");
		server_file.getParentFile().mkdirs();
		server_file.createNewFile();
		FileWriter writer = new FileWriter(server_file);
		writer.write(managerAddress.toString() + "\n");
		writer.close();

		// Wait for nodes
		System.out.println("Waiting for " + args[0] + " nodes to connect");
		ArrayList<FullAddress> nodes = new ArrayList<>();
		int numNodes = Integer.parseInt(args[0]);
		for (int i = 0; i < numNodes; i++) {
			Socket socket = serverSocket.accept();
			FullAddress node = FullAddress.fromSocket(socket);

			System.out.println("Node " + i + " connected: " + node);

			nodes.add(node);
			socket.close();
		}

		// Callback nodes with their next node
		for (int i = 0; i < numNodes; i++) {
			FullAddress current = nodes.get(i);
			FullAddress next = nodes.get((i + 1) % numNodes);

			System.out.println("Calling back " + current + " with " + next);

			Socket socket = new Socket(current.ip, current.port);
			socket.getOutputStream().write(next.toString().getBytes());
			socket.close();
		}

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
