package sim;

public class Controller {
	private int entering; // Waiting to enter
	private int leaving; // Waiting to leave

	public Controller() {
		this.entering = 0;
		this.leaving = 0;
	}

	private void make_enter(Token token, int n) {
		token.placesLeft -= n;
		entering -= n;
	}

	private void make_leave(Token token, int n) {
		token.placesLeft += n;
		leaving -= n;
	}

	// [Entering strategy 1] Make maximum persons enter the room
	private void es1(Token token) {
		int n = Math.min(token.placesLeft, entering);
		make_enter(token, n);
	}

	// [Leaving strategy 1] Make maximum persons leave the room
	private void ls1(Token token) {
		make_leave(token, leaving);
	}

	// [Entering strategy 2] Make one person enter the room
	private void es2(Token token) {
		make_enter(token, 1);
	}

	// [Leaving strategy 2] Make one person leave the room
	private void ls2(Token token) {
		make_leave(token, 1);
	}

	public void run(Token token) {
		es1(token);
		ls1(token);
	}

	public void add_entering() {
		entering++;
	}

	public void add_leaving() {
		leaving++;
	}
}
