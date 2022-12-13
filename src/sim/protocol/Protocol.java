package sim.protocol;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import utils.FullAddress;

public class Protocol {
	private static ArrayList<Class<? extends Request>> requestClasses = new ArrayList<Class<? extends Request>>();
	private static HashMap<String, Class<? extends Request>> requestMap = null;

	private static void cacheRequestClasses() throws Exception {
		if (requestClasses.isEmpty()) {
			requestClasses.add(ArrivalRequest.class);
			requestClasses.add(DepartureRequest.class);
			requestClasses.add(ExitRequest.class);
			requestClasses.add(SimulationUpdateRequest.class);
			requestClasses.add(TokenRequest.class);
		}

		if (requestMap == null) {
			requestMap = new HashMap<>();
			for (Class<? extends Request> requestClass : requestClasses) {
				Request request = requestClass.getDeclaredConstructor().newInstance();
				requestMap.put(request.getCode(), requestClass);
			}
		}
	}

	public static void send(Socket socket, Request request) throws Exception {
		OutputStream out = socket.getOutputStream();

		// Encode & serialize message
		String message = Base64.getEncoder().encodeToString(request.serialize().getBytes());
		String packet = request.getCode() + " " + message;
		out.write(packet.getBytes());
	}

	public static void send(FullAddress address, Request request) throws Exception {
		Socket socket = new Socket(address.ip, address.port);
		send(socket, request);
		socket.close();
	}

	public static Request recv(String packet) throws Exception {
		cacheRequestClasses();

		String[] parts = packet.split(" ");
		String code = parts[0];

		if (!requestMap.containsKey(code)) {
			throw new Exception("Invalid request code: " + code);
		}

		Request request = requestMap.get(code).getDeclaredConstructor().newInstance();

		// Decode & deserialize message
		request.deserialize(new String(Base64.getDecoder().decode(parts[1])));
		return request;
	}

	public static Request recv(Socket socket) throws Exception {
		return recv(new String(socket.getInputStream().readAllBytes()));
	}
}
