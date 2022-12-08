package sim;

import java.io.*;

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
		return baos.toString();
	}

	public static Token deserialize(String serialized) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(serialized.getBytes());
		ObjectInputStream ois = new ObjectInputStream(bais);
		return (Token) ois.readObject();
	}

	public String toString() {
		return "Token {\n" +
				"\tplacesLeft: " + placesLeft + "\n" +
				"}";
	}
}
