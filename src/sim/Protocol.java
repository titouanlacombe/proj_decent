package sim;

import java.io.*;
import java.net.*;

import utils.FullAddress;

public class Protocol {
	public static final String TOKEN = "TOKEN";
	public static final String EXIT = "EXIT";

	public static void sendRequest(FullAddress address, String command, String[] args) throws Exception {
		Socket socket = new Socket(address.ip, address.port);
		OutputStream out = socket.getOutputStream();

		String message = command + " " + String.join(" ", args);
		out.write(message.getBytes());

		socket.close();
	}

	public static String getCommand(String message) throws Exception {
		return message.split(" ")[0];
	}

	public static String[] getArgs(String message) throws Exception {
		String[] parts = message.split(" ");
		String[] args = new String[parts.length - 1];
		for (int i = 1; i < parts.length; i++) {
			args[i - 1] = parts[i];
		}
		return args;
	}

	public static void sendToken(FullAddress address, Token token) throws Exception {
		sendRequest(address, Protocol.TOKEN, new String[] { token.serialize() });
	}

	public static Token receiveToken(String message) throws Exception {
		String[] args = getArgs(message);
		return Token.deserialize(args[0]);
	}
}
