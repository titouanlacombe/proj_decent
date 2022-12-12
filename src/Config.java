public class Config {
	// --- Simulation config ---
	public double entryRate;
	public double visitTimeMean;
	public double visitTimeStdDev;
	public int roomCapacity;
	public int numNodes;
	public double timeScale;
	public long randSeed;

	// --- UI config ---

	static public Config _default() {
		Config config = new Config();

		config.entryRate = 1;
		config.visitTimeMean = 1;
		config.visitTimeStdDev = 0.5;
		config.roomCapacity = 10;
		config.numNodes = 3;
		config.timeScale = 1000;
		config.randSeed = 0;

		return config;
	}
}
