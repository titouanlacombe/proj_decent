class Controller {
	private int waiting;
	private int leaving;

	public Controller() {
		this.waiting = 0;
		this.leaving = 0;
	}

	public void waiting() {
		waiting++;
	}

	public void leaving() {
		leaving++;
	}

	// [Entering strategy 1] Make maximum persons enter the room
	public void es1(Token token) {
		token.placesLeft -= waiting;
		waiting = 0;

		if (token.placesLeft < 0) {
			waiting = -token.placesLeft;
			token.placesLeft = 0;
		}
	}

	// [Leaving strategy 1] Make maximum persons leave the room
	public void ls1(Token token) {
		token.placesLeft += leaving;
		leaving = 0;
	}

	// [Entering strategy 2] Make one person enter the room
	public void es2(Token token) {
		if (waiting > 0) {
			token.placesLeft--;
			waiting--;
		}
	}

	// [Leaving strategy 2] Make one person leave the room
	public void ls2(Token token) {
		if (leaving > 0) {
			token.placesLeft++;
			leaving--;
		}
	}

	public void run(Token token) {
		es1(token);
		ls1(token);
	}
}
