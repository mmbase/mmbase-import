package org.mmbase.util.logging;

public class TestConfig {
	public static void main(String[] args) {		
		String configuration = args[0];
		String category      = args[1];
		Logging.configure(configuration);
		Logger log = Logging.getLoggerInstance(category);
		
		log.trace("a trace message");
		log.debug("a debug message");
		log.info("an info message");
		log.service("a service message");
		log.warn("a warn message");
		log.error("an error message");

		Logging.shutdown();

	}
}
