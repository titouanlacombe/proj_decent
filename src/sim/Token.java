package sim;

import java.io.*;
import java.util.Base64;

public class Token implements Serializable {
	public int placesLeft;

	public Token(int placesLeft) {
		this.placesLeft = placesLeft;
	}

	public Token() {
	}

	public String serialize() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	public static Token deserialize(String serialized) throws IOException, ClassNotFoundException {
		byte[] bytes = Base64.getDecoder().decode(serialized);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return (Token) ois.readObject();
	}

	public String toString() {
		return "Token {\n" +
				"\tplacesLeft: " + placesLeft + "\n" +
				"}";
	}
}
