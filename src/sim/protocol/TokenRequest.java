package sim.protocol;

import sim.Token;

public class TokenRequest extends Request {
	public static final String CODE = "TOKEN";
	public Token token;

	public String getCode() {
		return CODE;
	}

	public TokenRequest(Token token) {
		this.token = token;
	}

	public TokenRequest() {
	}

	public void deserialize(String message) throws Exception {
		token = (Token) deserializeObj(message);
	}

	public String serialize() throws Exception {
		return serializeObj(token);
	}
}