package sim;

import java.io.*;
import java.net.*;
import java.util.Base64;

import utils.FullAddress;

public class Protocol {
	public static final String TOKEN = "TOKEN";
	public static final String EXIT = "EXIT";
	public static final String NODE_UPDATE = "NODE_UPDATE";

	public static String serialize(Object o) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(baos.toByteArray());
	}

	public static Object deserialize(String serialized) throws Exception {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] bytes = decoder.decode(serialized);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return ois.readObject();
	}

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
		sendRequest(address, TOKEN, new String[] { serialize(token) });
	}

	public static Token receiveToken(String message) throws Exception {
		String[] args = getArgs(message);
		return (Token) deserialize(args[0]);
	}

	public static void sendExit(FullAddress address) throws Exception {
		sendRequest(address, EXIT, new String[] {});
	}

	public static void sendNodeUpdate(FullAddress address, String uuid, int entered, int left) throws Exception {
		sendRequest(address, NODE_UPDATE, new String[] { uuid, String.valueOf(entered), String.valueOf(left) });
	}

	public static String receiveUUID(String message) throws Exception {
		String[] args = getArgs(message);
		return args[0];
	}

	public static int[] receiveEnteredLeft(String message) throws Exception {
		String[] args = getArgs(message);
		int[] enteredLeft = new int[2];
		enteredLeft[0] = Integer.parseInt(args[1]);
		enteredLeft[1] = Integer.parseInt(args[2]);
		return enteredLeft;
	}
}
