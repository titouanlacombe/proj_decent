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
				Socket socket = this.accept();
				System.out.println(this + ": Connection from " + socket.getInetAddress().getHostAddress() + ":"
						+ socket.getPort());
				this.handle(socket);
			} catch (Exception e) {
				System.out.println(this + ": Error:");
				e.printStackTrace();
			}
			System.out.println(this + ": Finished");
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
