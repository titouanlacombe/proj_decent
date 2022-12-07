public class Config {
	// --- Simulation config ---
	public double entryRate;
	public double visitTimeMean;
	public double visitTimeStdDev;

	// --- UI config ---

	static public Config _default() {
		Config config = new Config();

		config.entryRate = 1;
		config.visitTimeMean = 1;
		config.visitTimeStdDev = 0.5;

		return config;
	}
}
