package org.antitech.jgir.plugins;
/**
 * Storage.java
 *
 * Will store information for programming/security
 * as well as tutorial links.
 *
 * @author Aaron Allred
 */
import java.util.Properties;
import java.io.*;


public class Storage {

	private Properties props = new Properties();
	private static String storeFile = "properties/store.properties";

	public Storage() {
		try {
			FileInputStream in = new FileInputStream(storeFile);
			props.load(in);
			in.close();
		} catch(IOException ioe) {
			System.out.println("Creating file.");
			this.updateConfig();
		}
	}

	/** Read in a key/value from the config file. */
	public String readConfig(String key) {
		String result;
		result = props.getProperty(key);
		return result;
	}

	/** Insert key/value into config File. */
	public void insertProperty(String key, String value) {
		props.setProperty(key, value);
	}

	public void updateConfig() {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(storeFile);
			props.store(out, " --Class/Tut reference-- ");
		} catch(IOException ioe) {
			System.err.println("Unable to open file for writing.");
		}
		finally {
			try {
				out.close();
			} catch(IOException e) { }
		}
	}
}
