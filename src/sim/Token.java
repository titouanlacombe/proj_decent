package sim;

import java.io.Serializable;

public class Token implements Serializable {
	public int placesLeft;

	public Token(int placesLeft) {
		this.placesLeft = placesLeft;
	}

	public Token() {
	}

	public String toString() {
		return "Token (placesLeft: " + placesLeft + ")";
	}
}
