package sim;

import java.io.*;
import java.net.*;
import java.util.*;

public class RoomServer {
	private Room room;

	public RoomServer(Room room, int port) throws IOException {
		super(port);
		this.room = room;
	}

	public void run() {
		Socket clientSocket = this.accept();
		InputStream in = clientSocket.getInputStream();

		// Run forever
		while (true) {
			// TODO Parse command
			
			// Call room
			room.run();
		}
	}

	public String toString() {
		return "RoomServer(" + this.getLocalPort() + ")";
	}
}
