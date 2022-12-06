package sim;

import java.io.*;
import java.net.*;

public class ControllerServer extends ThreadedServer {
	private Controller controller;
	private Socket nextController;

	public ControllerServer(Controller controller) throws IOException {
		super();
		this.controller = controller;
	}

	public void setNext(String nextHost, int nextPort) throws IOException {
		System.out.println("Controller connecting to next at " + nextHost + ":" + nextPort);
		nextController = new Socket(nextHost, nextPort);
	}

	public boolean handle(Socket clientSocket) throws Exception {
		// Run forever
		while (true) {
			// Parse message
			Token token = Token.receiveFrom(clientSocket.getInputStream());

			System.out.println("Controller received token with " + token.placesLeft + " places left");

			// Call controller
			controller.run(token);

			// Call next controller
			token.sendTo(nextController.getOutputStream());
		}

		// clientSocket.close();
		// nextController.close();

		// return true;
	}
}
