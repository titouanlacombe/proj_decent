package sim;

import java.io.*;
import java.net.*;
import java.util.*;

public class ControllerServer extends ServerSocket {
	private Controller controller;
	private String nextHost;
	private int nextPort;

	public ControllerServer(Controller controller, int port, String nextHost, int nextPort) throws IOException {
		super(port);

		this.controller = controller;
		this.nextHost = nextHost;
		this.nextPort = nextPort;
	}

	public void run() {
		Socket clientSocket = this.accept();
		Socket nextSocket = new Socket(nextHost, nextPort);

		// Run forever
		while (true) {
			// Parse message
			Token token = Token.receiveFrom(clientSocket.getInputStream());

			// Call controller
			controller.run(token);

			// Call next controller
			token.sendTo(nextSocket.getOutputStream());
		}

		clientSocket.close();
		nextSocket.close();
		this.close();
	}

	public String toString() {
		return "ControllerServer(" + this.getLocalPort() + ")";
	}
}
