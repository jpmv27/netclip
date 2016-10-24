package net.dumbinter.netclip;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	static Properties prop = new Properties();
	static boolean initialized = false;

	public static void read() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream("config.properties");
		prop.load(stream);
	}

	public static Properties get() throws IOException {
		if (!initialized) {
			read();
		}
		return prop;
	}
}
