import java.util.*;

import sim.*;
import server.IncrementingPortAllocator;
import utils.NormalGenerator;

public class Main {
	public static void main(String[] args) {
		Config config = Config._default();

		IncrementingPortAllocator portAllocator = new IncrementingPortAllocator(10000);

		NormalGenerator generator = new NormalGenerator(config.visitTimeMean, config.visitTimeStdDev, 0);
		sim.Room room = new sim.Room(config.entryRate, generator);
		RoomServer roomServer = new RoomServer(room, portAllocator.get());

		ArrayList<ControllerServer> doorServers = new ArrayList<ControllerServer>();
		for (int i = 0; i < config.nbDoors; i++) {
			ControllerServer controllerServer = new ControllerServer(portAllocator.get());
			controllerServer.start();
			doorServers.add(controllerServer);
		}

		// TODO call first server with token from default config
		Token token = new Token();
		doorServers.get(0).call(token);

		System.out.println("Done !");
	}
}
