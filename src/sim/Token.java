package sim;

public class Token {
	public int placesLeft;

	public Token(int placesLeft) {
		this.placesLeft = placesLeft;
	}

	public Token() {
	}

	public String toString() {
		return "Token {\n" +
				"\tplacesLeft: " + placesLeft + "\n" +
				"}";
	}
}
