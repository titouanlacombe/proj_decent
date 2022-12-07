package utils;

import java.net.*;

public class FullAddress {
	public String ip;
	public int port;

	public FullAddress(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public String toString() {
		return ip + ":" + port;
	}

	public static FullAddress fromString(String str) {
		String[] parts = str.split(":");
		return new FullAddress(parts[0], Integer.parseInt(parts[1]));
	}

	public static FullAddress fromSocket(Socket socket) {
		return new FullAddress(socket.getInetAddress().getHostAddress(), socket.getLocalPort());
	}

	public static FullAddress fromSocket(ServerSocket socket) {
		return new FullAddress(socket.getInetAddress().getHostAddress(), socket.getLocalPort());
	}
}
