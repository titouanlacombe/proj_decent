package server;

class IncrementingPortAllocator {
	private int port = 0;

	public PortAllocator(int startPort) {
		port = startPort;
	}

	public int get() {
		return port++;
	}
}
