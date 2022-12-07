package sim;

import java.io.*;

public class Token implements Serializable {
	public int placesLeft;

	public Token(int placesLeft) {
		this.placesLeft = placesLeft;
	}

	public Token() {
	}

	public void sendTo(OutputStream out) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(this);
		oos.flush();
	}

	public static Token receiveFrom(InputStream in) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(in);
		return (Token) ois.readObject();
	}

	public String toString() {
		return "Token {\n" +
				"\tplacesLeft: " + placesLeft + "\n" +
				"}";
	}
}
