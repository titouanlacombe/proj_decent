import java.util.*;
import java.net.*;

import sim.*;
import utils.NormalGenerator;

public class Main {
	public static void _main(String[] args) throws Exception {
		Config config = Config._default();

		// --- Create controller servers ---
		System.out.println("Creating " + config.nbControllers + " controller servers");
		ArrayList<ControllerServer> controllerServers = new ArrayList<ControllerServer>();
		ControllerServer first = new ControllerServer(new Controller());

		ControllerServer previous = first;
		int i = 1;
		while (i < config.nbControllers) {
			ControllerServer controllerServer = new ControllerServer(new Controller());
			controllerServers.add(controllerServer);

			previous.setNext("0.0.0.0", controllerServer.getLocalPort());
			previous = controllerServer;
			i++;
		}
		previous.setNext(first.getHost(), first.getLocalPort());

		// --- Create room server ---
		System.out.println("Creating room server");
		NormalGenerator generator = new NormalGenerator(config.visitTimeMean, config.visitTimeStdDev, 0);
		sim.Room room = new sim.Room(config.entryRate, generator);
		RoomServer roomServer = new RoomServer(room);

		// --- Start servers threads ---
		System.out.println("Starting servers");
		for (ControllerServer controllerServer : controllerServers) {
			controllerServer.serve();
		}
		roomServer.serve();

		// Call first server with token from default config
		System.out.println("Calling first server at " + first.getHost() + ":" + first.getLocalPort());
		Token token = new Token(config.roomCapacity);
		Socket clientSocket = new Socket(first.getHost(), first.getLocalPort());
		token.sendTo(clientSocket.getOutputStream());

		// --- Wait for room server to finish ---
		System.out.println("Waiting for room server to finish");
		roomServer.join();

		// --- Stop servers ---
		System.out.println("Stopping servers");
		for (ControllerServer controllerServer : controllerServers) {
			controllerServer.close();
		}
		roomServer.close();

		System.out.println("Main thread finished");
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
