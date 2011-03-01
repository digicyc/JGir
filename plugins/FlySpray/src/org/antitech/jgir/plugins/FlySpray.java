package org.antitech.jgir.plugins;
/**
 * FlySpray.java
 *
 * Will parse a FileSpray URL and return information requested.
 *
 * @author Aaron Allred
 */
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.io.*;

public class FlySpray {
	public URL fly = null;
	public URLConnection flyConnection = null;

	public FlySpray() {
		BufferedReader in = null;

		try {
			fly = new 
				URL("http", "sendoutcards.com", "/flyspray/index.php?tasks=all&project=1");
			flyConnection = fly.openConnection();
		} catch(MalformedURLException urle) {
		} catch(IOException ioe) {
			System.err.println("Connection failed.");
		}
		
		this.outputUrl();
	}

	public void outputUrl() {
		BufferedReader in = null;

		        try {
            in = new BufferedReader(new InputStreamReader(
                                        flyConnection.getInputStream()));
        } catch(IOException ioe) {
            System.err.println("Unable to open filestream.");
        }
        String inputLine;

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
	public static void main(String[] args) {
		FlySpray spray = new FlySpray();
	}
}


