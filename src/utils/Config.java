package utils;

public class Config {
    // --- Simulation config ---
    public double entryRate;
    public double visitTimeMean;
    public double visitTimeStdDev;
    public int roomCapacity;
    public int numNodes;

    public Config(int numNodes, int numPersons) {
        this.numNodes = numNodes;
        this.roomCapacity = numPersons;
    }

    // --- UI config ---

    static public Config _default() {
        Config config = new Config(3, 10);

        config.entryRate = 1;
        config.visitTimeMean = 1;
        config.visitTimeStdDev = 0.5;

        return config;
    }

    public String toString() {
        return "Config(" +
                "entryRate=" + entryRate +
                ", visitTimeMean=" + visitTimeMean +
                ", visitTimeStdDev=" + visitTimeStdDev +
                ", roomCapacity=" + roomCapacity +
                ", numNodes=" + numNodes +
                ')';
    }
}
