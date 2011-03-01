package org.antitech.jgir;
/**
 * Config.java
 *
 * This will control the Properties of Gir.
 * 
 * @author Aaron Allred
 */

import org.antitech.jgir.logger.LogSystem;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Config {

	private Properties props;
	private static String configFile = "properties/jgir.properties";
    private static Config conf = null;
    private LogSystem logger = LogSystem.getInstance();


	private Config() { 
		props = new Properties();
		FileInputStream in = null;
		FileOutputStream out = null;
		
		try {
			in = new FileInputStream(configFile);
			props.load(in);
			in.close();

		} catch(IOException ioe) {
			logger.debug("Unable to open file:"+configFile, ioe);

		}
	}

    /** 
     * Return the instance of the config.
     *
     * @return Config instance
     */
    public static Config getInstance() {
        if (conf == null)
            conf = new Config();
        return conf;
    }

    /** 
     * Reload the config and return new instance.
     *
     * @return Config instance
     */
    public static Config reloadConfig() {
        conf = null;
        System.gc();
        conf = new Config();
        return conf;
    }

    /**
     * Read config file for key value.
     *
     * @param String key
     *
     * @return String value
     */
	public String readConfig(String key) {
		return (props.getProperty(key));
	}
}
