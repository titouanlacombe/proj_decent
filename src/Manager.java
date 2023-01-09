import java.util.*;
import java.io.*;
import java.net.*;

import sim.Token;
import sim.protocol.Protocol;
import sim.protocol.TokenRequest;
import utils.*;

public class Manager {
	private Logger logger;

	// Args: num_nodes => write to stdout the ip:port of manager
	public void _main(String[] args) throws Exception {
		this.logger = Logger.fileLogger("Manager", "./data/manager.log");

		if (args.length < 2) {
			logger.error("Error: Invalid number of arguments\nUsage: java Manager num_nodes room_capacity");
			return;
		}

		// Create server
		ServerSocket serverSocket = new ServerSocket(0);
		FullAddress managerAddress = FullAddress.fromSocket(serverSocket);
		logger.debug("Manager started at " + managerAddress);

		// Write address to file file for automation
		File server_file = new File("./data/manager_address.txt");
		server_file.getParentFile().mkdirs();
		server_file.createNewFile();
		FileWriter writer = new FileWriter(server_file);
		writer.write(managerAddress + "\n");
		writer.close();

		// Wait for nodes
		int numNodes = Integer.parseInt(args[0]);
		logger.debug("Waiting for " + numNodes + " nodes to connect");
		HashMap<String, FullAddress> nodes = new HashMap<String, FullAddress>();
		for (int i = 0; i < numNodes; i++) {
			// Get message
			Socket socket = serverSocket.accept();
			String node_message = new String(socket.getInputStream().readAllBytes());
			socket.close();

			// Parse message
			String[] parts = node_message.split(" ");
			String uuid = parts[0];
			FullAddress node_address = FullAddress.fromString(parts[1]);
			logger.debug("Node " + uuid + " registered with address: " + node_address);

			nodes.put(uuid, node_address);
		}

		// Write node addresses to file for automation
		File nodes_file = new File("./data/nodes_addresses.txt");
		nodes_file.getParentFile().mkdirs();
		nodes_file.createNewFile();
		writer = new FileWriter(nodes_file);
		for (String uuid : nodes.keySet()) {
			writer.write(uuid + " " + nodes.get(uuid) + "\n");
		}
		writer.close();

		// Callback nodes with their next node
		ArrayList<String> uuids = new ArrayList<String>(nodes.keySet());
		for (int i = 0; i < numNodes; i++) {
			FullAddress current = nodes.get(uuids.get(i));
			FullAddress next = nodes.get(uuids.get((i + 1) % numNodes));

			logger.debug("Calling back " + current + " with " + next);

			Socket socket = new Socket(current.ip, current.port);
			socket.getOutputStream().write(next.toString().getBytes());
			socket.close();
		}

		// Create initial token
		int roomCapacity = Integer.parseInt(args[1]);
		Token initialToken = new Token(roomCapacity);

		// Sending start message to node 0
		logger.debug("Sending start message to node 0");
		FullAddress node0 = nodes.get(uuids.get(0));
		Protocol.send(node0, new TokenRequest(initialToken));

		logger.info("Nodes setup complete, exiting");
		serverSocket.close();
	}

	public static void main(String[] args) {
		Manager manager = new Manager();
		try {
			manager._main(args);
		} catch (Exception e) {
			manager.logger.exception(e);
		}
	}
}
