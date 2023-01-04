package config;

import java.lang.reflect.Field;

public class Config {
	// --- Simulation config ---
	public double entryRate;
	public double visitTimeMean;
	public double visitTimeStdDev;
	public int roomCapacity;
	public int numNodes;
	public double timeScale;
	public long randSeed;
	public double simulationTimeFactor;
	public long simulationUpdateInterval;

	// --- UI config ---
	public String windowTitle;
	public int windowWidth;
	public int windowHeight;
	public long uiRefreshInterval;

	static public Config _default() {
		Config config = new Config();

		// In milliseconds
		config.entryRate = 4 / 2000.0; // 1 entry every 2 seconds
		config.visitTimeMean = 2000; // 1 second
		config.visitTimeStdDev = 1000; // 1 second

		config.roomCapacity = 10;
		config.numNodes = 3;
		config.timeScale = 1;
		config.randSeed = 0;
		config.simulationTimeFactor = 1;
		config.simulationUpdateInterval = 100;

		config.windowTitle = "Simulation";
		config.windowWidth = 800;
		config.windowHeight = 600;
		config.uiRefreshInterval = 50;

		return config;
	}

	public String toString() {
		// Using reflexion
		Field[] fields = this.getClass().getDeclaredFields();
		String str = "Config:\n";
		for (Field field : fields) {
			try {
				str += "\t" + field.getName() + ": " + field.get(this) + "\n";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}
}
