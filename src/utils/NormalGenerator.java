package utils;

class NormalGenerator {
	private Random rand;
	private double mean;
	private double stdDev;

	public NormalGenerator(double mean, double stdDev, long seed) {
		this.mean = mean;
		this.stdDev = stdDev;
		rand = new Random(seed);
	}

	public double get() {
		return mean + rand.nextGaussian() * stdDev;
	}
}
