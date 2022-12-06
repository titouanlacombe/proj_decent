package sim;

import java.io.*;
import java.net.*;

public class RoomServer extends ThreadedServer {
	private Room room;

	public RoomServer(Room room) throws IOException {
		super();
		this.room = room;
	}

	public boolean handle(Socket clientSocket) throws Exception {
		// InputStream in = clientSocket.getInputStream();

		// Run forever
		while (true) {
			// TODO Parse command

			// Call room
			room.run();
		}

		// return true;
	}
}
