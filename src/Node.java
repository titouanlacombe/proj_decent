import java.net.*;

import sim.Controller;
import sim.Token;
import utils.*;

public class Node {
	Controller controller;
	FullAddress nextNodeAddress;

	// Args: manager_ip:manager_port
	public void _main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Error: Invalid number of arguments");
			System.out.println("Usage: java Node manager_ip:manager_port");
			return;
		}

		// Creating server
		ServerSocket serverSocket = new ServerSocket(0);
		FullAddress myAddress = FullAddress.fromSocket(serverSocket);
		System.out.println("Node started at " + myAddress);

		// Send manager my address
		FullAddress managerAddress = FullAddress.fromString(args[0]);
		Socket socket = new Socket(managerAddress.ip, managerAddress.port);
		System.out.println("Sending my address to " + managerAddress);
		socket.getOutputStream().write(myAddress.toString().getBytes());
		socket.close();

		// Wait for manager to send next node
		System.out.println("Waiting for manager to send next node");
		Socket rep_socket = serverSocket.accept();
		String resp = new String(rep_socket.getInputStream().readAllBytes());
		this.nextNodeAddress = FullAddress.fromString(resp);
		System.out.println("Received " + this.nextNodeAddress + " from manager");
		rep_socket.close();

		// Start controller
		System.out.println("Setup complete, starting node");
		this.controller = new Controller();

		// Start server
		while (true) {
			Socket clientSocket = serverSocket.accept();
			boolean exit = this.handleRequest(clientSocket);
			clientSocket.close();

			if (exit) {
				break;
			}
		}
	}

	public boolean handleRequest(Socket clientSocket) throws Exception {
		String request = new String(clientSocket.getInputStream().readAllBytes());
		System.out.println("Received request: " + request);

		String[] args = request.split(" ");
		String command = args[0];

		switch (command) {
			case "exit":
				System.out.println("Exiting");
				return true;
			case "token":
				this.tokenRequest(args);
				return false;
			default:
				System.out.println("Error: Invalid command: '" + command + "'");
				return false;
		}
	}

	public void tokenRequest(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Error: Invalid number of arguments");
			System.out.println("Usage: token <token>");
			return;
		}

		Token token = Token.deserialize(args[1]);
		System.out.println("Received token: " + token);

		// Run node controller
		controller.run(token);

		// Call next node
		Thread.sleep(1000);
		Socket nextNode = new Socket(this.nextNodeAddress.ip, this.nextNodeAddress.port);
		nextNode.getOutputStream().write(("token " + token.serialize()).getBytes());
		nextNode.close();
	}

	public static void main(String[] args) {
		Node node = new Node();

		try {
			node._main(args);
		} catch (Exception e) {
			System.out.println("Error in main thread");
			e.printStackTrace();
		}
	}
}
