import java.net.*;

import sim.Controller;
import sim.Protocol;
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
		String message = new String(clientSocket.getInputStream().readAllBytes());
		String command = Protocol.getCommand(message);
		System.out.println("Received command: '" + command + "'");

		switch (command) {
			case Protocol.TOKEN:
				Token token = Protocol.receiveToken(message);
				this.tokenRequest(token);
				break;
			case Protocol.EXIT:
				return true;
			default:
				System.out.println("Error: Invalid command");
				break;
		}

		return false;
	}

	public void tokenRequest(Token token) throws Exception {
		System.out.println("Received token: " + token);

		// Run node controller
		controller.run(token);

		// Call next node
		Thread.sleep(1000);
		Protocol.sendToken(this.nextNodeAddress, token);
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
