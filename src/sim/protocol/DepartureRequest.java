package sim.protocol;

public class DepartureRequest extends Request {
	public static final String CODE = "DEPARTURE";

	public String getCode() {
		return CODE;
	}

	public void deserialize(String message) {
	}

	public String serialize() {
		return "";
	}
}
