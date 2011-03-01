package org.antitech.jgir.packet;

import org.antitech.jgir.MyBot;
import org.antitech.jgir.Config;
import org.antitech.jgir.pluginmanager.PluginChain;
import org.antitech.jgir.plugins.events.JabberMessageEvent;
import org.antitech.jgir.logger.LogSystem;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.XMPPConnection;


public class MessagePacketListener implements PacketListener {

    private Message message = null;
    private String senderJID = null;


    public void processPacket(Packet packet) {
        // Make sure this is the write type of packet
        if (packet instanceof Message)
            message = (Message) packet;
        else
            return;

        MyBot myBot = MyBot.getInstance();
        Config conf = Config.getInstance();

        if (message.getBody() == null) {
            return;
        }
        
        // Parse off the resource.
        String from = message.getFrom();
        if (from != null)
            senderJID = from.substring(0, from.indexOf("/"));

        // echo commands
        XMPPConnection connection = myBot.getConnection();
        Message newMsg = new Message(from);
        newMsg.setBody(message.getBody());
        newMsg.setType(Message.Type.CHAT);
        connection.sendPacket(newMsg);

        if (senderJID.equals(conf.readConfig("adminJID"))
         || senderJID.equals(conf.readConfig("adminJID2"))) {
            // Send an Event down the chain.
            JabberMessageEvent event = new JabberMessageEvent(this);
            event.setMessage(message.getBody());
            event.setPacket(packet);
            PluginChain.fireEvent(event);
        } 
    }

    public String getSenderJID() {
        return senderJID;
    }
}

