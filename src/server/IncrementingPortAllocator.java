package server;

class IncrementingPortAllocator {
	private int port;

	public PortAllocator(int startPort) {
		port = startPort;
	}

	public int get() {
		return port++;
	}
}
