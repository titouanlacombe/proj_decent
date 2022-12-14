package sim.protocol;

import java.io.*;
import java.util.Base64;

public abstract class Request {
	abstract public String getCode();

	abstract void deserialize(String message) throws Exception;

	abstract String serialize() throws Exception;

	public String toString() {
		return getCode() + " Request";
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
