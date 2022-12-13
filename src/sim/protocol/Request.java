package sim.protocol;

import java.io.*;
import java.util.Base64;

public abstract class Request {
	// Can't do abstract static variables, so we have to do this
	public static final String CODE = null;

	public String getCode() throws Exception {
		if (CODE == null) {
			throw new RuntimeException("CODE not implemented for " + getClass().getName());
		}
		return CODE;
	}

	abstract void deserialize(String message) throws Exception;

	abstract String serialize() throws Exception;

	public String toString() {
		try {
			return "[" + getCode() + " request]: " + serialize();
		} catch (Exception e) {
			System.err.println("Error serializing request: " + e.getMessage());
			return "";
		}
	}

	public static String serializeObj(Object o) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(baos.toByteArray());
	}

	public static Object deserializeObj(String serialized) throws Exception {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] bytes = decoder.decode(serialized);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return ois.readObject();
	}
}
