import Config;
import sim.Room;

class Main {
	public static void main(String[] args) {
		Config config = Config._default();

		IncrementingPortAllocator portAllocator = new PortAllocator();

		NormalGenerator generator = new NormalGenerator(config.visitTimeMean, config.visitTimeStdDev, 0);
		Room room = new Room(config.entryRate, generator);
		RoomServer roomServer = new RoomServer(room, portAllocator.get());

		ArrayList<DoorServer> doorServers = new ArrayList<DoorServer>();
		for (int i = 0; i < config.nbDoors; i++) {
			DoorServer doorServer = new DoorServer(portAllocator.get());
			doorServer.start();
			doorServers.add(doorServer);
		}

		// TODO call first server with token from default config
		Token token = new Token();
		doorServers.get(0).call(token);

		System.out.println("Done !");
	}
}
