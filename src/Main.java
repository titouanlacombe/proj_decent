import java.util.*;

import sim.*;
import utils.NormalGenerator;

public class Main {
	public static void _main(String[] args) throws Exception {
		Config config = Config._default();

		// --- Create controller servers ---
		System.out.println("Creating " + config.nbControllers + " controller servers");
		ArrayList<ControllerServer> controllerServers = new ArrayList<ControllerServer>();
		for (int i = 0; i < config.nbControllers; i++) {
			ControllerServer controllerServer = new ControllerServer(new Controller());
			controllerServers.add(controllerServer);
		}
		// Connect controllers
		for (int i = 0; i < config.nbControllers; i++) {
			ControllerServer current = controllerServers.get(i);
			ControllerServer next = controllerServers.get((i + 1) % config.nbControllers);
			current.setNext(next.getHost(), next.getLocalPort());
		}

		// --- Create room server ---
		System.out.println("Creating room server");
		NormalGenerator generator = new NormalGenerator(config.visitTimeMean, config.visitTimeStdDev, 0);
		sim.Room room = new sim.Room(config.entryRate, generator);
		RoomServer roomServer = new RoomServer(room);

		// Configuring first controller
		System.out.println("Configuring first controller");
		controllerServers.get(0).setStartingToken(new Token(config.roomCapacity));

		// --- Start servers threads ---
		System.out.println("Starting servers");
		for (ControllerServer controllerServer : controllerServers) {
			controllerServer.serve();
		}
		roomServer.serve();

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
