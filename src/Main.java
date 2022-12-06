import java.util.*;
import java.lang.Thread;

import sim.*;
import java.io.*;
import utils.IncrementingPortAllocator;
import utils.NormalGenerator;

public class Main {
	public static void main(String[] args) {
		Config config = Config._default();

		String host = "localhost";
		IncrementingPortAllocator portAllocator = new IncrementingPortAllocator(10000);

		// --- Create controller servers ---
		ArrayList<ControllerServer> controllerServers = new ArrayList<ControllerServer>();
		ControllerServer first = new ControllerServer(new Controller(), portAllocator.get());
		
		ControllerServer previous = first;
		int i = 1;
		while (i < config.nbControllers) {
			ControllerServer controllerServer = new ControllerServer(new Controller(), portAllocator.get());
			
			controllerServers.add(controllerServer);
			previous.setNext(previous.getHost(), previous.getPort());
			previous = controllerServer;
			i++;
		}
		previous.setNext(first.getHost(), first.getPort());

		// --- Create room server ---
		NormalGenerator generator = new NormalGenerator(config.visitTimeMean, config.visitTimeStdDev, 0);
		sim.Room room = new sim.Room(config.entryRate, generator);
		RoomServer roomServer = new RoomServer(room, portAllocator.get());

		// --- Start servers threads ---
		for (ControllerServer controllerServer : controllerServers) {
			controllerServer.serve();
		}
		roomServer.serve();

		// Call first server with token from default config
		Token token = new Token(config.roomCapacity);
		Socket clientSocket = new Socket(first.getHost(), first.getPort());
		token.sendTo(clientSocket.getOutputStream());

		System.out.println("Main thread finished");
	}
}
