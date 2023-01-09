package utils;

import java.io.FileNotFoundException;

// Facade of logger
public class Logging {
	public static Logger logger;

	public static void init(String name, String path) throws FileNotFoundException {
		logger = Logger.fileLogger(name, path);
	}

	public static void debug(String message) {
		logger.debug(message);
	}

	public static void info(String message) {
		logger.info(message);
	}

	public static void warning(String message) {
		logger.warning(message);
	}

	public static void error(String message) {
		logger.error(message);
	}

	public static void exception(Exception e) {
		logger.exception(e);
	}
}
