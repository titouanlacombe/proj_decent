package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Logger {
	public enum Level {
		DEBUG,
		INFO,
		WARNING,
		ERROR
	}

	static String default_format = "[{Y}/{M}/{D} {h}:{m}:{s}.{millis}] [{name}-{level}]: {message}\n";
	String format;
	String name;
	OutputStream stream;

	public Logger(String format, String name, OutputStream stream) {
		this.format = format;
		this.name = name;
		this.stream = stream;
	}

	public static Logger fileLogger(String format, String name, String path) throws FileNotFoundException {
		return new Logger(format, name, new FileOutputStream(new File(path)));
	}

	public static Logger fileLogger(String name, String path) throws FileNotFoundException {
		return fileLogger(default_format, name, path);
	}

	public static String getLevelName(Level level) {
		switch (level) {
			case DEBUG:
				return "DEBUG";
			case INFO:
				return "INFO";
			case WARNING:
				return "WARNING";
			case ERROR:
				return "ERROR";
			default:
				return "UNKNOWN";
		}
	}

	public static String format(String message, Map<String, String> metas) {
		String formatted = message;
		for (Map.Entry<String, String> meta : metas.entrySet()) {
			formatted = formatted.replace("{" + meta.getKey() + "}", meta.getValue());
		}
		return formatted;
	}

	public static String formatInt(int i, int length) {
		String s = String.valueOf(i);
		if (length < 0) {
			return s;
		}
		while (s.length() < length) {
			s = "0" + s;
		}
		return s;
	}

	public static String formatInt(int i) {
		return formatInt(i, -1);
	}

	public void log(String message, Level level) {
		// Build log metas
		Map<String, String> metas = new HashMap<String, String>();
		metas.put("level", getLevelName(level));
		metas.put("message", message);
		metas.put("name", this.name);
		LocalDateTime date = LocalDateTime.now();
		metas.put("Y", formatInt(date.getYear()));
		metas.put("M", formatInt(date.getMonthValue(), 2));
		metas.put("D", formatInt(date.getDayOfMonth(), 2));
		metas.put("h", formatInt(date.getHour(), 2));
		metas.put("m", formatInt(date.getMinute(), 2));
		metas.put("s", formatInt(date.getSecond(), 2));
		int millis = (int) Math.round(date.getNano() / 10E6);
		metas.put("millis", formatInt(millis, 3));

		// Format & log
		String formatted = format(this.format, metas);
		try {
			this.stream.write(formatted.getBytes());
		} catch (Exception e) {
			System.err.println("FATAL LOGGING ERROR:");
			e.printStackTrace();
		}
	}

	public void debug(String message) {
		log(message, Level.DEBUG);
	}

	public void info(String message) {
		log(message, Level.INFO);
	}

	public void warning(String message) {
		log(message, Level.WARNING);
	}

	public void error(String message) {
		log(message, Level.ERROR);
	}

	public void exception(Exception e) {
		String stackTrace = "";
		for (StackTraceElement element : e.getStackTrace()) {
			stackTrace += "\t" + element.toString() + "\n";
		}
		error(e.getClass().getSimpleName() + ": " + e.toString() + "\n" + stackTrace);
	}
}
