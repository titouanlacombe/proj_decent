import java.net.*;
import java.util.UUID;

import sim.Controller;
import sim.Token;
import sim.protocol.*;
import utils.*;

public class Node {
	private Logger logger;
	private String uuid;
	private Controller controller;
	private FullAddress nextNodeAddress;
	private FullAddress simulatorAddress;
	private Long sleepTime;

	public Node() {
		this.uuid = UUID.randomUUID().toString();
		this.logger = new Logger("Node " + this.uuid);
	}

	// Args: manager_ip:manager_port
	public void _main(String[] args) throws Exception {
		if (args.length < 1) {
			logger.error(
					"Error: Invalid number of arguments\nUsage: java Node manager_ip:manager_port [simulator_ip:simulator_port sleep_time]");
			return;
		}

		// Get simulator address and sleep time
		if (args.length > 1) {
			simulatorAddress = FullAddress.fromString(args[1]);
			logger.debug("Got simulator address: " + simulatorAddress);

			sleepTime = Long.parseLong(args[2]);
			logger.debug("Got sleep time: " + sleepTime);
		}

		// Creating server
		ServerSocket serverSocket = new ServerSocket(0);
		FullAddress myAddress = FullAddress.fromSocket(serverSocket);
		logger.debug("Node started at " + myAddress);

		// Send manager my address
		FullAddress managerAddress = FullAddress.fromString(args[0]);
		Socket socket = new Socket(managerAddress.ip, managerAddress.port);
		logger.debug("Sending my uuid/address to " + managerAddress);
		String message = uuid + " " + myAddress;
		socket.getOutputStream().write(message.getBytes());
		socket.close();

		// Wait for manager to send next node
		logger.debug("Waiting for manager to send next node");
		Socket rep_socket = serverSocket.accept();
		String resp = new String(rep_socket.getInputStream().readAllBytes());
		rep_socket.close();
		this.nextNodeAddress = FullAddress.fromString(resp);
		logger.debug("Received next: " + this.nextNodeAddress + " from manager");

		// Start controller
		logger.info("Setup complete, starting node");
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
		Request request = Protocol.recv(clientSocket);
		logger.info(request.toString());

		switch (request.getCode()) {
			case ExitRequest.CODE:
				return true;
			case ArrivalRequest.CODE:
				controller.arrival();
				break;
			case DepartureRequest.CODE:
				controller.departure();
				break;
			case TokenRequest.CODE:
				tokenRequest((TokenRequest) request);
				break;
			default:
				logger.error("Error: Invalid request code");
				break;
		}

		return false;
	}

	public void tokenRequest(TokenRequest request) throws Exception {
		Token token = request.token;

		logger.debug("Node got token: " + token);
		logger.debug("Node controller state: " + controller);

		// Run node controller
		controller.run(token);

		// Send new controller state to simulation server
		if (simulatorAddress != null) {
			Protocol.send(simulatorAddress, new SimulationUpdateRequest(uuid, controller));
		}

		// Sleep if needed
		if (sleepTime != null) {
			Thread.sleep(sleepTime);
		}

		// Call next node
		Protocol.send(nextNodeAddress, new TokenRequest(token));
	}

	public static void main(String[] args) {
		Node node = new Node();

		try {
			node._main(args);
		} catch (Exception e) {
			node.logger.exception(e);
		}
	}
}
