class Config {
	// --- Simulation config ---
	public int roomCapacity;
	public int nbDoors;

	public double entryRate;
	public double visitTimeMean;
	public double visitTimeStdDev;

	// --- UI config ---

	static public Config _default() {
		config = new Config();

		config.roomCapacity = 3;
		config.nbDoors = 2;

		config.entryRate = 1;
		config.visitTimeMean = 1;
		config.visitTimeStdDev = 0.5;

		return config;
	}
}
