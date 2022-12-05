class Config {
	// Simulation config
	public int roomCapacity;
	public int nbDoors;

	static public Config _default() {
		conf = new Config();
		conf.roomCapacity = 3;
		conf.nbDoors = 2;
		return conf;
	}
}
