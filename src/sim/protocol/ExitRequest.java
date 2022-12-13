package sim.protocol;

public class ExitRequest extends Request {
	public static final String CODE = "EXIT";

	public String getCode() {
		return CODE;
	}

	public void deserialize(String message) {
	}

	public String serialize() {
		return "";
	}
}
