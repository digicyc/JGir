//package org.antitech.jgir.plugins;
/**
 * Google.java
 *
 * Will parse a FileSpray URL and return information requested.
 *
 * @author Aaron Allred
 */
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.io.*;

public class Google {
	private URL fly = null;
	private URLConnection flyConnection = null;
    private String key = null;


	public Google(String key) {
        this.key = key;
	    BufferedReader in = null;

        // Replace spaces with +'s
        key = key.replace(" ", "+");
        System.out.println("FINAL: " +key);

		try {
			fly = new 
				URL("http", "www.google.com", "/search?hl=en&q="+key+"&btnI=I%27m+Feeling+Lucky");
			flyConnection = fly.openConnection();
		} catch(MalformedURLException urle) {
            System.err.println("Bad URL!");
		} catch(IOException ioe) {
			System.err.println("Connection failed.");
		}
	}

	public void outputUrl() {
		BufferedReader in = null;

		try {
            in = new BufferedReader(new InputStreamReader(flyConnection.getInputStream()));
        } catch(IOException ioe) {
            System.err.println("Unable to open filestream.");
            ioe.printStackTrace();
        }
        String inputLine = null;

        try {
            while((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch(IOException ioe) { }

        try {
            in.close();
        } catch(IOException ioe) { }
	}


/** This is just for testing, REMOVE THIS OTHERWISE.*/
/**	public static void main(String[] args) {
		Google google = new Google(args[0]);
        google.outputUrl();
	}
**/
}


