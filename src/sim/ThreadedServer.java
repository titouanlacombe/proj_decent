package sim;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Thread;

public class ThreadedServer extends ServerSocket {
	private Thread thread;

	public ThreadedServer(int port) throws IOException {
		super(port);
	}

	public abstract void handle(Socket socket) throws Exception;

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

	public void stop() {
		thread.interrupt();
		this.close();
	}

	public String toString() {
		return "ThreadedServer listening at " + this.getHost() + ":" + this.getPort();
	}
}
