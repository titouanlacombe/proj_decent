package sim;

public class Clock {
	public Double timerStart;
	public double factor;

	public Clock(double factor) {
		this.factor = factor;
	}

	public double now() {
		return System.currentTimeMillis() * factor;
	}

	public void startTimer() {
		timerStart = now();
	}

	public double elapsed() throws Exception {
		if (timerStart == null) {
			throw new Exception("Timer not started");
		}
		return now() - timerStart;
	}
}
