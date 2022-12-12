package utils;

public class Timer {
	private double scale;

	public Timer(double scale) {
		this.scale = scale;
	}

	public double now() {
		return System.currentTimeMillis() * scale;
	}
}
