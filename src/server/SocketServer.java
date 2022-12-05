package server;

// Implement simple RMI server with sockets
// Packet: [length][method][args]
public class SocketServer {
	private Object object; // Object to call methods on

	public SocketServer(Object object) {
		this.object = object;
	}
}
