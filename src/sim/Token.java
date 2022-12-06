package sim;

import org.json.JSONObject;
import java.io.*;

public class Token {
	public int placesLeft;

	public Token(int placesLeft) {
		this.placesLeft = placesLeft;
	}

	public Token() {
	}

	public String toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("placesLeft", placesLeft);
		return obj.toString();
	}

	public static Token fromJSON(String json) {
		JSONObject obj = new JSONObject(json);
		Token token = new Token();
		token.placesLeft = obj.getInt("placesLeft");
		return token;
	}

	public void sendTo(OutputStream out) throws IOException {
		String json = toJSON();
		int length = json.length();
		out.write(length >> 24);
		out.write(length >> 16);
		out.write(length >> 8);
		out.write(length);
		out.write(json.getBytes());
	}

	public static Token receiveFrom(InputStream in) throws IOException {
		// Get new message length
		byte[] len = new byte[4];
		in.read(len, 0, 4);
		int length = len[0] << 24 | len[1] << 16 | len[2] << 8 | len[3];

		// TODO exit if length == 0 ?

		// Get message (JSON)
		String json = "";
		for (int i = 0; i < length; i++) {
			json += (char) in.read();
		}

		// Parse message
		return fromJSON(json);
	}
}
