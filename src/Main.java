import java.util.*;
import java.lang.Thread;

import sim.*;
import java.io.*;
import utils.IncrementingPortAllocator;
import utils.NormalGenerator;

public class Main {
	public static void main(String[] args) {
		Config config = Config._default();

		// --- Create controller servers ---
		ArrayList<ControllerServer> controllerServers = new ArrayList<ControllerServer>();
		for (int i = 0; i < config.nbDoors; i++) {
			Controller controller = new Controller(config.nbPlaces);
			ControllerServer controllerServer = new ControllerServer(controller, portAllocator.get());
			controllerServers.add(controllerServer);
		}

		// --- Create room server ---
		NormalGenerator generator = new NormalGenerator(config.visitTimeMean, config.visitTimeStdDev, 0);
		sim.Room room = new sim.Room(config.entryRate, generator);
		RoomServer roomServer = new RoomServer(room, portAllocator.get());

		// --- Start servers threads ---
		IncrementingPortAllocator portAllocator = new IncrementingPortAllocator(10000);
		for (ControllerServer controllerServer : controllerServers) {
			Thread thread = new Thread(() -> {
				try {
					controllerServer.run();
				} catch (Exception e) {
					System.out.println("Error in " + controllerServer);
					e.printStackTrace();
				}
				System.out.println(controllerServer + " finished");
			});
			thread.start();
		}
		
		Thread thread = new Thread(() -> {
			try {
				roomServer.run();
			} catch (Exception e) {
				System.out.println("Error in " + roomServer);
				e.printStackTrace();
			}
			System.out.println(roomServer + " finished");
		});
		thread.start();

		// Call first server with token from default config
		Token token = new Token(config.roomCapacity);
		ControllerServer first = controllerServers.get(0);
		Socket clientSocket = new Socket(first.getHost(), first.getPort());
		OutputStreamWriter out = new OutputStreamWriter(clientSocket.getOutputStream());
		token.sendTo(out);

		System.out.println("Main thread finished");
	}
}
