package sim;

import java.io.*;
import java.net.*;

public class ControllerServer extends ThreadedServer {
	private Controller controller;
	private Socket nextController;
	private Token startingToken;

	public ControllerServer(Controller controller) throws IOException {
		super();
		this.controller = controller;
	}

	public void setNext(String nextHost, int nextPort) throws IOException {
		System.out.println("Controller connecting to next at " + nextHost + ":" + nextPort);
		nextController = new Socket(nextHost, nextPort);
	}

	public void setStartingToken(Token startingToken) {
		this.startingToken = startingToken;
	}

	public boolean handle(Socket clientSocket) throws Exception {
		if (startingToken != null) {
			// Start the loop by sending the token to the next controller
			startingToken.sendTo(nextController.getOutputStream());
		}

		// Run forever
		while (true) {
			// Parse message
			Token token = Token.receiveFrom(clientSocket.getInputStream());

			System.out.println("Controller received token with " + token.placesLeft + " places left");

			// Call controller
			controller.run(token);

			// Call next controller
			Thread.sleep(1000);
			token.sendTo(nextController.getOutputStream());
		}

		// clientSocket.close();
		// nextController.close();

		// return true;
	}
}
