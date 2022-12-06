package sim;

import java.io.*;
import java.net.*;
import java.util.*;

public class RoomServer extends ThreadedServer {
	private Room room;

	public RoomServer(Room room, int port) throws IOException {
		super(port);
		this.room = room;
	}

	public void handle(Socket clientSocket) {
		InputStream in = clientSocket.getInputStream();

		// Run forever
		while (true) {
			// TODO Parse command
			
			// Call room
			room.run();
		}
	}
}
