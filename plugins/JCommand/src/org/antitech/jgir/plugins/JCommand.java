package org.antitech.jgir.plugins;
/**
 * JCommand.java
 * Control commands sent to the bot via Jabber.
 */
import org.antitech.jgir.MyBot;
import org.antitech.jgir.Config;
import org.antitech.jgir.pluginmanager.*;
import org.antitech.jgir.plugins.events.*;

import org.jivesoftware.smack.packet.Packet;


public class JCommand implements PluginInterface, PluginEventListener {
    private MyBot myBot = null;
    private Config conf = Config.getInstance();
    private String sender = null;
    private String message = null;
    private Packet packet = null;
    // Comand handler
    private String command = null;
    private String arg     = null;
    
    public boolean init() {
        PluginChain.addListener(this);
        this.myBot = MyBot.getInstance();
        if( this.myBot == null )
            return false;
        return true;
    }

    private void exeCmd(String cmd) {
        if (cmd.startsWith("=say")) {
            if (authJID()) {
                int pound = cmd.indexOf("#");
                String chan = cmd.substring(pound, cmd.length());
                String msg = cmd.substring(5, pound-1);
                if (!msg.startsWith("=say")) {
                    myBot.sendMessage(chan, msg);
                }
            }
        }
        else if(cmd.startsWith("=join")) {
            if (authJID()) {
                int pound = cmd.indexOf("#");
                String chan = cmd.substring(pound, cmd.length());
                myBot.sendJabber(sender, "Joining: "+chan, 1);
                myBot.joinChannel(chan);
            }
        }
        else if (cmd.toLowerCase().startsWith("!op")) {
            if (authJID()) {
                String userOp = cmd.substring(4, cmd.length());
                String[] users = userOp.split(",");
                for(String user : users) {
                    myBot.op("#swetesoc", user.trim());
                }
            }
        }
    }

    public void handleEvent( PluginEvent eve ) {
        if(eve instanceof JabberMessageEvent) {
           JabberMessageEvent event = (JabberMessageEvent)eve;
            this.message = event.getMessage();
            packet       = event.getPacket();
            this.sender  = packet.getFrom();

            exeCmd(message);
        }
    }

    private boolean authJID() {
        if (sender.equals(conf.readConfig("adminJID"))) {
            return true;
        }
        return false;
    }

    public boolean unload() {
        PluginChain.removeListener(this);
        return true;
    }
}

