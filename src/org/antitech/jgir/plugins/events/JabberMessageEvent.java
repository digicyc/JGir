package org.antitech.jgir.plugins.events;

import org.antitech.jgir.pluginmanager.PluginEvent;

import org.jivesoftware.smack.packet.Packet;


public class JabberMessageEvent extends PluginEvent {

    private String message;
    private Packet packet;

    public JabberMessageEvent(Object source) {
        super(source);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
}
