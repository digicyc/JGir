package org.antitech.jgir.plugins;
/**
 * Commands.java
 *
 * This class will parse commands and execute them properly
 * based off a list.
 *
 * @author cyclone
 */
import java.io.*;
import java.util.*;

import org.antitech.jgir.Config;


public class CommList {

	private Config conf;
	private ArrayList<String> alist = new ArrayList<String>();

	public CommList() {
		conf = Config.getInstance();
        // Load the file up
		loadFile();
	}

	/** Loads the command reference file. */
	private void loadFile() {
		BufferedReader in = null;
		String str;

		try {
			in = new BufferedReader(new FileReader(conf.readConfig("commandsList")));
			while((str = in.readLine()) != null) {
				alist.add(str);
			}

			in.close();
		} catch(IOException ioe) {
			System.err.println("Unable to open file for reading.");
			System.exit(1);
		}
	}

	/** Save new commands to the File. */
	private void saveFile() {
		BufferedWriter out = null;
		String str = null;

		try {
			out = new BufferedWriter(new FileWriter(conf.readConfig("commandsList")));
			out.write(str + "\n");
		} catch(IOException ioe) {	}
		
		try {		
			out.close();
		} catch(IOException ie) {
			System.err.println("Unable to close file:");
		}
	}

	/** The method to check the command before writing to the file.*/
/*	public void addCommand(String command) {
	
        // Now save it to file
		saveFile(command);
	}
*/
	/** Hand the commands to the Object calling this method from 
	 *	THIS object.
     *
     * @return arraylist of commands.
     */
	public ArrayList<String> getCommands() {
		return this.alist;
	}
}
