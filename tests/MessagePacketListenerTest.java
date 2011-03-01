//package test;

import org.junit.Test;
import org.junit.

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.antitech.jgir.packet.MessagePacketListener;

public class MessagePacketListenerTest implements Test {

    private MessagePacketListener msgPacket = null;
    private Message packet = null;

    @Before public void setUp() {
        packet.setBody("Command");
        packet.setFrom("test@antitech.org/res");
        packet.setType(Message.Type.CHAT);
        msgPacket = new MessagePacketListener();
        msgPacket.processPacket(packet);

    }

    @Test public void testParsedJID() {
        
    }

    @Test public void testAdminJID() {

    }

    @Test public 

}
