package org.antitech.jgir.plugins;
/**
 * Command.java
 * Control commands sent to the bot.
 */
import org.antitech.jgir.dbase.DBHandler;
import org.antitech.jgir.MyBot;
import org.antitech.jgir.Config;
import org.antitech.jgir.logger.LogSystem;
import org.antitech.jgir.pluginmanager.*;
import org.antitech.jgir.plugins.events.*;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Date;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Command implements PluginInterface, PluginEventListener {
    private MyBot myBot = null;
    private Config conf = Config.getInstance();
    private LogSystem logger = LogSystem.getInstance();
    // Global stats
    private String channel = null;
    private String sender = null;
    private String login = null;
    private String hostname = null;
    private String message = null;

    CommList comm = null;
    ArrayList comlist = null;
    
    public boolean init() {
        logger.log("*[PLUGIN]: Plugin Was loaded Successful.");
        PluginChain.addListener(this);
        this.myBot = MyBot.getInstance();
        if( this.myBot == null )
            return false;
        return true;
    }

    public void exeCmd(String cmd) {
        if (cmd.startsWith("=say")) {
            if (authHostname()) {
                int pound = cmd.indexOf("#");
                String chan = cmd.substring(pound, cmd.length());
                String msg = cmd.substring(5, pound-1);
                if (!msg.startsWith("=say")) {
                    myBot.sendMessage(chan, msg);
                }
            }
        }
        else if(cmd.equals("=hello")) {
            myBot.sendMessage(channel, sender + ": Good day.. I SAY GOODDAY!");
        }
        else if(cmd.startsWith("=join")) {
            if (authHostname()) {
                int pound = cmd.indexOf("#");
                String chan = cmd.substring(pound, cmd.length());
                myBot.sendJabber(sender, "Joining: "+chan, 1);
                myBot.joinChannel(chan);
            }
        }
        else if (cmd.toLowerCase().startsWith("!op")) {
            if (authHostname()) {
                String userOp = cmd.substring(4, cmd.length());
                String[] users = userOp.split(" ");
                for(String user : users) {
                    myBot.op(channel, user.trim());
                }
            }
        }
        else if (cmd.startsWith("digicyc")) {
            myBot.sendJabber(sender, cmd, 1);
        }
        // TODO: throttle the pages.
        else if (cmd.toLowerCase().startsWith("!page") ||
                cmd.toLowerCase().startsWith("!ping")) {
            if (channel.startsWith(conf.readConfig("commandChannel"))) {
                String pageTime = myBot.getTime();
                String pageMessage = cmd.substring(6, cmd.length());
                String pageSender = sender;

                // Create the DB Connection
                DBHandler dbhandler = new DBHandler();
                Connection dbcon = dbhandler.getdbConnection();
                Statement stmt = dbhandler.getStatement();
            
                myBot.sendJabber(sender, pageMessage, 2);

                try {
                    String updateStmnt =
                        "INSERT INTO page (sender, msg, page_time)" +
                        " VALUES (?, ?, ?)";
                    PreparedStatement updatePage =
                        dbcon.prepareStatement(updateStmnt);
                    updatePage.setString(1, sender);
                    updatePage.setString(2, pageMessage);
                    updatePage.setString(3, pageTime);
                    int n = updatePage.executeUpdate();
                    if (n > 0 ) 
                        myBot.sendMessage(channel, sender+": Your Page was Sent!");
                } catch (SQLException sqlE) {
                    myBot.sendMessage(channel, sender+": Page sent, but couldn't log it.");
                    myBot.sendJabber(" ", "[ERROR]:Database Connection is broke.", 2);
                    logger.debug("[Error]:Can't Update Table.", sqlE);
                }
            }
        }
        else if(cmd.startsWith("=page info")) {
            java.util.Date theDate = null;
            String theSender = null;
            String theMsg = null;
            String theTime = null;
            // Create db connection
            DBHandler dbhandler = new DBHandler();
            Connection dbcon = dbhandler.getdbConnection();
            Statement stmt = dbhandler.getStatement();
            String query = "SELECT *" +
                " FROM page ORDER BY page_id DESC LIMIT 1";

            try {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    theSender = rs.getString("sender");
                    theMsg = rs.getString("msg");
                    theTime = rs.getString("page_time");
                    theDate = rs.getDate("page_date");
                }
            } catch (SQLException sqlE) { }

            try {
                if (sender != null &&
                        channel.startsWith(conf.readConfig("commandChannel"))) {
                    myBot.sendMessage(channel, "Last Page info: ");
                    myBot.sendMessage(channel, "From: ["+theSender+"] "+
                            "msg: ["+theMsg+"]");
                    myBot.sendMessage(channel, "Page Time: ["+theTime+"]");
                }
                else {
                    myBot.sendMessage(channel, "No Recent Pages.");
                }
            } catch (NullPointerException ne) {
                myBot.sendMessage(conf.readConfig("commandChannel"), "No Info");
            }
        }
        // Re/Connect to Jabber.
        else if(cmd.startsWith("=jabber connect")) {
            if (authHostname()) {
                myBot.jabberReconnect();
            }
        }
        else if(cmd.startsWith("=add class")) {
            Storage stor = new Storage();
            int http = cmd.indexOf("http");
            String addClass = cmd.substring(11, http-1);
            String finalClass = addClass.toLowerCase();
            String classUrl = cmd.substring(http, cmd.length());

            stor.insertProperty(finalClass, classUrl);
            stor.updateConfig();
        }
        else if(cmd.startsWith("=class ")) {
            Storage stor = new Storage();
            String readClass = cmd.substring(7, cmd.length());
            String finalClass = readClass.toLowerCase();
            String viewClass = stor.readConfig(finalClass);

            if (!viewClass.equals("null")) {
                myBot.sendMessage(channel, "Class: ["+readClass+"] "+
                    viewClass);
            }
        }
        else if(cmd.startsWith("=command")) {
            if (comm == null) {
                comm = new CommList();
                comlist = comm.getCommands();
            }
            Iterator iterator = comlist.iterator();

            myBot.sendMessage(sender, "Sending list of commands");

            while (iterator.hasNext()) {
                String str = (String)iterator.next();
                myBot.sendMessage(sender, str);
            }
        }
        else if(cmd.startsWith("=jabber connect") &&
            sender.equals(conf.readConfig("adminName"))) {
            myBot.jabberConnect();
        }
    }

    public void handleEvent( PluginEvent eve ) {
        if(eve instanceof MessageEvent) {
            MessageEvent event = (MessageEvent)eve;
            this.channel = event.getChannel();
            this.sender = event.getSender();
            this.login = event.getLogin();
            this.hostname = event.getHostname();
            this.message = event.getMessage();

            this.exeCmd(message);
        }
    }

    private boolean authHostname() {
        if (hostname.equals(conf.readConfig("adminHost"))) {
            return true;
        }
        return false;
    }

    public boolean unload() {
        PluginChain.removeListener(this);
        logger.log("[PLUGIN]: Plugin unloaded");
        return true;
    }
}

