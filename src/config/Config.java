package config;

import java.lang.reflect.Field;
import java.util.ArrayList;

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
    public long nodeSleepTime;

    // --- UI config ---
    public String windowTitle;
    public int windowWidth;
    public int windowHeight;
    public long uiRefreshInterval;

    static public Config _default() {
        Config config = new Config();

        // In milliseconds
        config.entryRate = 10 / 2000.0; // 1 entry every 2 seconds
        config.visitTimeMean = 10000; // 1 second
        config.visitTimeStdDev = 1000; // 1 second

        config.roomCapacity = 10;
        config.numNodes = 3;
        config.timeScale = 1;
        config.randSeed = 0;
        config.simulationTimeFactor = 1;
        config.simulationUpdateInterval = 20;
        config.nodeSleepTime = 100;

        config.windowTitle = "Simulation";
        config.windowWidth = 800;
        config.windowHeight = 600;
        config.uiRefreshInterval = 20;

        return config;
    }

    public ArrayList<String> getKeys() {

        ArrayList<String> keys = new ArrayList<>();
        Field[] fields = getFields();
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].getName().equals("windowTitle") && !fields[i].getName().equals("windowWidth")
                    && !fields[i].getName().equals("windowHeight"))
                keys.add(fields[i].getName());
        }
        return keys;
    }

    public Field getField(String key) throws NoSuchFieldException {
        return this.getClass().getDeclaredField(key);
    }

    public Field[] getFields() {
        return this.getClass().getDeclaredFields();
    }

    public Object get(String key) throws NoSuchFieldException, IllegalAccessException {
        return this.getClass().getDeclaredField(key).get(this);
    }

    public void set(String key, Object value) throws NoSuchFieldException, IllegalAccessException {
        this.getClass().getDeclaredField(key).set(this, value);
    }

    public String toString() {
        // Using reflexion
        String str = "Config:\n";
        for (String key : this.getKeys()) {
            try {
                str += "\t" + key + ": " + this.get(key) + "\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }
}
