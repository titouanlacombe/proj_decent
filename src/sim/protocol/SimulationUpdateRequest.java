package sim.protocol;

import sim.Controller;

public class SimulationUpdateRequest extends Request {
	public static final String CODE = "SIMULATION_UPDATE";
	public String sender_uuid;
	public Controller controller;

	public SimulationUpdateRequest(String sender_uuid, Controller controller) {
		this.sender_uuid = sender_uuid;
		this.controller = controller;
	}

	public SimulationUpdateRequest() {
	}

	public String getCode() {
		return CODE;
	}

	public void deserialize(String message) throws Exception {
		String[] parts = message.split(" ");
		sender_uuid = parts[0];
		controller = (Controller) deserializeObj(parts[1]);
	}

	public String serialize() throws Exception {
		return sender_uuid + " " + serializeObj(controller);
	}

	public String toString() {
		return "SimulationUpdate request from " + sender_uuid + ": " + controller;
	}
}
