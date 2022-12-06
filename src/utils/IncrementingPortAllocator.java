package utils;

public class IncrementingPortAllocator {
	private int port;

	public IncrementingPortAllocator(int startPort) {
		port = startPort;
	}

	public int get() {
		return port++;
	}
}
