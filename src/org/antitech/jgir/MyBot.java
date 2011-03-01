package org.antitech.jgir;
/**
 * This is the actual Engine of the bot.
 *
 * @author cyclone
 */
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.DccFileTransfer;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import org.antitech.jgir.logger.LogSystem;
import org.antitech.jgir.pluginmanager.PluginLoader;
import org.antitech.jgir.pluginmanager.PluginChain;
import org.antitech.jgir.plugins.events.MessageEvent;
import org.antitech.jgir.plugins.events.JabberMessageEvent;
import org.antitech.jgir.packet.MessagePacketListener;
import org.antitech.jgir.packet.ControlPacketFilter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MyBot extends PircBot {

    // constants for sending jabber msgs.
    public static final int JABBER_ADMIN = 1;
    public static final int JABBER_BROADCAST = 2;

    private static MyBot instance = null;
	private Config conf = Config.getInstance();
    private PluginLoader jar = null;
	// Global logger.
	private LogSystem logger = LogSystem.getInstance();
	private XMPPConnection con;
    private Timer timer;
    private ArrayList<String> loadedPlugs = new ArrayList<String>();
   

    /**  
     * Use singleton for creating instance.
     */
    private MyBot() {	
		this.setName(conf.readConfig("botName"));
		this.setFinger("Stop giving me the finger");
		this.setVersion(conf.readConfig("version"));
		this.setLogin(conf.readConfig("botName"));

        // Connect to the Jabber Server.
		jabberConnect();
        // Set connection check Timer. 
        // Keeps us asleep at night.
        timer = new Timer();
        timer.schedule(new ConnectTimer(), 500000);
	}

    public static MyBot getInstance() {
        if( instance == null )
            instance = new MyBot();
        return instance;
    }

	/**
	 * Connect to our Jabber Server.
 	 */
	public void jabberConnect() {
        try {
            PacketFilter packetFilter = new PacketTypeFilter(Message.class);

            con = new XMPPConnection(conf.readConfig("jabberServ"));
            con.login(conf.readConfig("jabberId"), 
                        conf.readConfig("jabberPass"));

            PacketCollector myCollector = 
                    con.createPacketCollector(packetFilter);
			con.addPacketListener(new MessagePacketListener(), packetFilter);
        } catch (XMPPException xmpe) {
            logger.debug("[ERROR]: Unable to connect to JID serv.", xmpe);
        }
	}

    public void jabberReconnect() {
        if(con == null || con.isConnected()) {
            con.close();
            con = null;
        }
        jabberConnect();
    }

    /**
     * Act on certain commands from messages
     * then send an Event down the chain.
     *
     * @param channel
     *          channel message was recvd from.
     * @param sender
     *          User who sent the message.
     * @param login
     *          not used.
     * @param hostname
     *          The users hostname
     * @param message
     *          The message that was sent.
     */
	public void onMessage(String channel, String sender,
					String login, String hostname, String message) {

        // If the message was to load/unload a plugin by the boss then do so.
        if(message.startsWith("=load")
                && hostname.equals(conf.readConfig("adminHost"))) {
            String pluginToload = message.substring(6, message.length());
            loadPlug(conf.readConfig("pluginDir")+pluginToload+".jar", pluginToload.trim());
            sendMessage(channel, "+[Plugin was loaded!]");
        }
        else if(message.startsWith("=unload") 
                && hostname.equals(conf.readConfig("adminHost"))) {
            final String plugToUnload = message.substring(8, message.length()).trim();
            if (unloadPlug(plugToUnload)) {
                sendMessage(channel, "-[Plugin was Unloaded]");
            }
            else {
                sendMessage(channel, "[X]Can't find Plugin: "+plugToUnload);
            }
        }
        else if(message.startsWith("=reload config") 
                && hostname.equals(conf.readConfig("adminHost"))) {
            conf = Config.reloadConfig();
            sendMessage(channel, sender+": [Config Reloaded]");
        }
        else if(message.startsWith("=plugs")) {
            Iterator iterator = loadedPlugs.iterator();

            while (iterator.hasNext()) {
                sendMessage(channel, "[Plugin]: "+iterator.next());
            }
        }

        // Send our event down the chain.
        MessageEvent event = new MessageEvent(this);
        event.sendInfo(channel, sender, login, hostname, message);
        PluginChain.fireEvent(event);
	}

	/** Receives a File. */
	public void onIncomingFileTransfer(DccFileTransfer transfer) {
		//File file = transfer.getFile();
		//transfer.receive(file, true);
	}

	/** Sends Registration info. Only once. */
	public void sendRegistraton() {
		sendMessage("nickserv", "register " + conf.readConfig("botPass"));
	}

	/** Sends Identification to server during connect. */
	public void sendIdent() {
		sendMessage("nickserv", "ident " + conf.readConfig("botPass"));
        sendMessage("chanserv", "op " + conf.readConfig("commandChannel"));
	}

	/** 
     * Auto rejoin channel if kicked or kick kicker if admin is kicked. 
     * Kick the kicking kicker!
     */
	protected void onKick(String channel, String kickerNick,
						  String kickerLogin, String kickerHostname, 
					  	  String recipientNick, String reason) {
		if (recipientNick.startsWith(getNick())) {
			joinChannel(channel);
		}
        else if(recipientNick.equals(conf.readConfig("adminName"))) {
            kick(channel, kickerNick, "Don't Kick the MAN!");
        }
	}

	/** 
     * Send a message via Jabber to a list of users in config.
     *
     * @param alertSender 
     *              Person that raised the alert.
     * @param alertMsg
     *              The Msg to report.
	 * @param msgType  
     *              2: for broadCast or 1: for Admin msg. 
	 */
	public void sendJabber(String alertSender, String alertMsg, int msgType) {
		if (msgType == 2) {
			String[] jidList = conf.readConfig("jabberList").split(",");

			for (int i = 0; i < jidList.length; i++) {
				try {
					con.createChat(jidList[i]).sendMessage(alertSender + 
						": " + alertMsg);
				} catch (XMPPException xmpe) {
					sendMessage(conf.readConfig("adminName"), 
                        "Unable to send msg to jid id: "+jidList[i]);
				} catch (IllegalStateException ie) {
					try {
						con = new XMPPConnection(conf.readConfig("jabberServ"));
						con.login(conf.readConfig("jabberId"), 
                            conf.readConfig("jabberPass"));
					} catch (XMPPException xmpe) {
						sendMessage(conf.readConfig("adminName"), 
                            "Unable to Reconnect.");
					}
				}			
			}
		}
        /* or send it to just the bot admin */
		else if (msgType == 1) {
			try {
				con.createChat(conf.readConfig("adminJID"))
                    .sendMessage(alertSender +
					": " + alertMsg);
                con.createChat(conf.readConfig("adminJID2"))
                    .sendMessage(alertSender +
                    ": " + alertMsg);
			} catch (XMPPException xmpe) {
				sendMessage(conf.readConfig("adminName"), 
                    "Unable to send msg to jid serv.");
			}
		}
	}

	/**
  	 * Get Current time.
 	 * @return Returns current time.
	 */
	public String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E h:mm a");
        return dateFormat.format(new Date());
	}

    /**
     * Get the time based off a Date Object.
     * @param date
     *      java.util.Date Object.
     */
	public String getTime(java.util.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E h:mm a");
        return dateFormat.format(date);
	}

    /**
     * Initial setup and connect.
     *
     * @return boolean
     *          True if connection was successful else false.
     */
    private boolean connect() {
        try {
            super.connect(conf.readConfig("serverName"));
        } catch (Exception e) {
            logger.debug("Unable to reconnect to IRC server.", e);
            return false;
        }
        String[] channels = conf.readConfig("channels").split(",");

        for (int i=0; i<channels.length; i++) {
            joinChannel(channels[i]);
        }
        if (conf.readConfig("serverName").equals("irc.freenode.net")) {
            sendIdent();
        }
        return true;
    }

    /**
     * When our bot gets disconnected, rejoin server and channel.
     */
	public void onDisconnect() {
		if(!connect()) {
            sendJabber("[IRC]: ", "Disconnected from IRC!", JABBER_ADMIN);
            timer = new Timer();
            timer.schedule(new ConnectTimer(), 100000);
        }
	}

	/**
	 * OP certain people that join.
	 */
    protected void onJoin(String channel, String sender,
                          String login, String hostname) {
        opList(channel, sender, login, hostname);
    }
    
    /** 
     * Op our list of specific users.
     */
	private void opList(String channel, String sender,
            String login, String hostname) {
		if (channel.equals(conf.readConfig("commandChannel"))) {
			String ops = conf.readConfig("opsList");
            String hosts = conf.readConfig("hostnames");
			String opList[] = ops.split(",");
            String hostList[] = hosts.split(",");

			for (int i = 0; i < opList.length; i++) {
				if (sender.equals(opList[i])) {
                    for (int j = 0; j < hostList.length; j++) {
                        if (hostname.equals(hostList[j])) {
    					    op(conf.readConfig("commandChannel"), opList[i]);
                        }
                    }
				}
			}
		}
	}

    /**
     * Authenticate/Register Users.
     * TODO: Send Event and have plugin take control.
     */
    protected void onPrivateMessage(String sender, String login, 
                                    String hostname, String message) {
        // Send an Event down the Chain.
        MessageEvent event = new MessageEvent(this);
        event.sendInfo(null, sender, login, hostname, message);
        PluginChain.fireEvent(event);
    }

    /**
     * Timer class to check connection still exists.
     */
    class ConnectTimer extends TimerTask {
        public void run() {
            logger.log("[TIMER UP]Check Reconnect!");
            if (!isConnected()) {
                connect();
                logger.log("[TIMER] Was disconnected. Reconnecting");
            }
            else {
                logger.log("[TIMER] Connection Still EXISTS!");
            }
        }
    }
   
    /** 
     * Load the Plug requested in a seperate thread. 
     *
     * @param plug
     *          Path to the plugin.
     * @param plugname
     *          Name given to plugin.
     */ 
    public void loadPlug(final String plug, final String plugname) {
        Thread pluginThread = new Thread(new Runnable() {
            public void run() {
                try {
                    jar = PluginLoader.getNewInstance(plug);
                    jar.loadPlugin();
                } catch (NullPointerException ne) {
                    logger.log("[PLUGIN]: NULL EXCEPTION");

                } catch (Exception ioe) {
                    logger.log("[PLUGIN]: Plugin Problems during loading.");
                }
            }
        });
        pluginThread.start();
        loadedPlugs.add(plugname);
    }

    /** 
     * Unload a plugin in a seperate thread. 
     *
     * @param plug
     *      Plugin to unload by its name.
     *
     * @return true if unloaded successfully. 
     */
    public boolean unloadPlug(String plug) {
        Thread pluginThread = new Thread(new Runnable() {
            public void run() {
                jar.unloadPlugin();
                jar = null;
                System.gc();
            }
        });
        pluginThread.start();
        int i = loadedPlugs.indexOf(plug);
        if (i != -1) {
            loadedPlugs.remove(i);
            return true;
        }
        return false;
    } 

    /**
     * Load requested list of plugins.
     * Just a default list of plugins that should
     * always be ran during bootup.
     */
    public void loadPlugList() {
        String[] plugs = conf.readConfig("plugs").split(",");
        for(int i = 0; i<=plugs.length; i++) {
            loadPlug(conf.readConfig("pluginDir")+plugs[i]+".jar", plugs[i]);
        }
    }

    /**
     * Get list of loaded plugins.
     *
     * @return a String collection of plug names.
     */
    public ArrayList getLoadedPlugs() {
        return loadedPlugs;
    }

    public XMPPConnection getConnection() {
        return con;
    }
}
