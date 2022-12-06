package sim;

import java.net.*;
import java.io.*;
import java.lang.Thread;

abstract public class ThreadedServer extends ServerSocket {
	private Thread thread;

	public ThreadedServer() throws IOException {
		super(0);
	}

	abstract public void handle(Socket socket) throws Exception;

	public String getHost() {
		return this.getInetAddress().getHostAddress();
	}

	public void serve() {
		thread = new Thread(() -> {
			try {
				this.handle(this.accept());
			} catch (Exception e) {
				System.out.println("Error in " + this);
				e.printStackTrace();
			}
			System.out.println(this + " finished");
		});
		thread.start();
	}

	public void join() throws InterruptedException {
		thread.join();
	}

	public void close() throws IOException {
		thread.interrupt();
		this.close();
	}

	public String toString() {
		return "ThreadedServer listening at " + this.getHost() + ":" + this.getLocalPort();
	}
}
