package sim.protocol;

public class ArrivalRequest extends Request {
	public static final String CODE = "ARRIVAL";

	public String getCode() {
		return CODE;
	}

	public void deserialize(String message) {
	}

	public String serialize() {
		return "";
	}
}
