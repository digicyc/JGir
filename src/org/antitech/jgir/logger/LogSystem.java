package org.antitech.jgir.logger;
/**
 * Our own Global Logging system.
 *
 * @author cyclone
 */
import java.io.*;
import java.util.logging.*;

public class LogSystem {

    private static Logger logger = Logger.getLogger("org.antitech.jgir");
	private static FileHandler fh = null;
	private static LogSystem instance = null;

	/**
	 * Use a singleton to make it global to the system.
	 */
	private LogSystem() {
		try {
			fh = new FileHandler("logs/jgir_log.txt");
		} catch (IOException e) { }

		logger.addHandler(fh);
		logger.setLevel(Level.ALL);
		logger.info("#####- Begin Logging -#####");
	}

	/**
	 * We want this global for the whole system.
	 *
	 * @return
	 *		Instance of LogSystem.
	 */
	public static LogSystem getInstance() {
		if (instance == null)
			instance = new LogSystem();
		return instance;
	}

	/**
	 * Print a debug statement with a stack dump.
	 *
	 * @param str
	 *		An error Message.
	 * @param ex
	 *		The exception which will print a stack dump.
	 */
	public void debug(String str, Exception ex) {
		logger.log(Level.WARNING, "\n-=[ Log Warning ]=-\n"+str, ex);
	}

	/**
	 * Simply log a message.
	 *
	 * @param str
	 *		The Message to log.
	 */
	public void log(String str) {
		logger.log(Level.INFO, "[INFO]: " + str);
	}
	
}
