import java.net.*;

import sim.Controller;
import utils.*;

public class Node {
	// Args: manager_ip:manager_port
	public static void _main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Error: Invalid number of arguments");
			System.out.println("Usage: java Node manager_ip:manager_port");
			return;
		}

		// Creating server
		ServerSocket serverSocket = new ServerSocket(0);
		FullAddress myAddress = FullAddress.fromSocket(serverSocket);
		System.out.println("Server started at " + myAddress);

		// Send manager my address
		FullAddress managerAddress = FullAddress.fromString(args[1]);
		Socket socket = new Socket(managerAddress.ip, managerAddress.port);
		System.out.println("Sending my address to " + managerAddress);
		socket.getOutputStream().write(myAddress.toString().getBytes());
		socket.close();

		// Wait for manager to send next node
		Socket rep_socket = serverSocket.accept();
		String resp = new String(rep_socket.getInputStream().readAllBytes());
		FullAddress nextAddress = FullAddress.fromString(resp);
		System.out.println("Received " + nextAddress + " from manager");
		rep_socket.close();

		// Start controller
		System.out.println("Setup complete, starting controller");
		Controller controller = new Controller();
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
