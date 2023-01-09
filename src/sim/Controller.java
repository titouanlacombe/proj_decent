package sim;

import java.io.Serializable;

import utils.Logging;

public class Controller implements Serializable {
	private int entering; // Waiting to enter
	private int leaving; // Waiting to leave

	public Controller() {
		this.entering = 0;
		this.leaving = 0;
	}

	private void make_enter(Token token, int n) {
		Logging.info("Controller: " + n + " entering");
		token.placesLeft -= n;
		entering -= n;
	}

	private void make_leave(Token token, int n) {
		Logging.info("Controller: " + n + " leaving");
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
		if (token.placesLeft > 0)
			make_enter(token, 1);
	}

	// [Leaving strategy 2] Make one person leave the room
	private void ls2(Token token) {
		make_leave(token, 1);
	}

	public void run(Token token) {
		ls1(token);
		es1(token);
	}

	public void arrival() {
		entering++;
	}

	public void departure() {
		leaving++;
	}

	public int get_entering() {
		return entering;
	}

	public int get_leaving() {
		return leaving;
	}

	public String toString() {
		return "Controller (entering: " + entering + ", leaving: " + leaving + ")";
	}
}