package sim;

import java.net.*;
import java.io.*;
import java.lang.Thread;

abstract public class ThreadedServer extends ServerSocket {
	private Thread thread;

	public ThreadedServer() throws IOException {
		super(0);
	}

	abstract public boolean handle(Socket socket) throws Exception;

	public String getHost() {
		return this.getInetAddress().getHostAddress();
	}

	public void serve() {
		thread = new Thread(() -> {
			while (true) {
				try {
					System.out.println(this + ": Waiting for connection");
					Socket socket = this.accept();
					System.out.println(this + ": Connection from " + socket.getInetAddress().getHostAddress() + ":"
							+ socket.getPort());
					boolean exit = this.handle(socket);
					if (exit) {
						break;
					}
				} catch (Exception e) {
					System.out.println(this + ": Error:");
					e.printStackTrace();
				}
			}
			System.out.println(this + ": Exited");
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
