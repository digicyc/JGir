package org.antitech.jgir;

import org.jibble.pircbot.IrcException;

import java.io.IOException;

public class JGirMain {
	public static void main(String[] args) {
		
		//Start up the bot.
		MyBot jgir = MyBot.getInstance();
		Config conf = Config.getInstance();

		// Enable debugging output.
		jgir.setVerbose(true);

		try {
			// Connect to the IRC server.
			jgir.connect(conf.readConfig("serverName"));
		} catch(IrcException ircE) {
			System.err.println("Name already in Use.");
		} catch(IOException ioe) {

		}

		// Join the default channels
		// listed in jgir.properties
		String list = conf.readConfig("channels");
		String[] channels = list.split(",");
	
		for(int i = 0; i<channels.length; i++) {
			jgir.joinChannel(channels[i]);
		} 

		// Send registration information.
		//jgir.sendRegistraton();
        if (conf.readConfig("serverName").equals("irc.freenode.net")) {
		    jgir.sendIdent();
        }
        jgir.loadPlugList();
	}
}
