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

	public void run(int port) throws IOException {
		Socket clientSocket = this.accept();
		InputStreamReader in = new InputStreamReader(clientSocket.getInputStream());

		// Run forever
		while (true) {
			// Get new message length
			byte[] len = new byte[4];
			in.read(len, 0, 4);
			int length = len[0] << 24 | len[1] << 16 | len[2] << 8 | len[3];

			// TODO exit if length == 0 ?

			// Get message (JSON)
			String json = "";
			for (int i = 0; i < length; i++) {
				json += (char) in.read();
			}

			// Parse message
			Token token = Token.fromJson(json);

			// Call room
			room.call(token);
		}
	}

	public String toString() {
		return "RoomServer(" + this.getLocalPort() + ")";
	}
}
