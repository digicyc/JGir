package org.antitech.jgir.packet;

import org.antitech.jgir.Config;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message;

/**
 * We plan to get a bit more tricky with this filter.
 * Reason why it is in its own class.
 */
public class ControlPacketFilter implements PacketFilter {

    private Config conf = Config.getInstance();

    public boolean accept(Packet packet) {

        if (packet == null)
            return false;
        String from = packet.getFrom();
        if (from == null)
            return false;
        if (from.equals(conf.readConfig("adminJID"))
            || from.equals(conf.readConfig("adminJID2"))) {
            return true;
        }
        return false;
    }
}
