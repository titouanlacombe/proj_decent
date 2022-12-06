package sim;

import java.io.*;
import java.net.*;
import java.util.*;

public class ControllerServer extends ThreadedServer {
	private Controller controller;
	private Socket nextController;

	public ControllerServer(Controller controller, int port) throws IOException {
		super(port);
		this.controller = controller;
	}

	public void setNext(String nextHost, int nextPort) {
		nextController = new Socket(nextHost, nextPort);
	}

	public void handle(Socket clientSocket) {
		// Run forever
		while (true) {
			// Parse message
			Token token = Token.receiveFrom(clientSocket.getInputStream());

			// Call controller
			controller.run(token);

			// Call next controller
			token.sendTo(nextController.getOutputStream());
		}

		clientSocket.close();
		nextController.close();
	}
}
