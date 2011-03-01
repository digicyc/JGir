package org.antitech.jgir.plugins.events;

import org.antitech.jgir.pluginmanager.PluginEvent;
import org.jibble.pircbot.DccChat;

public class DccRequestEvent extends PluginEvent {
    private DccChat chat = null;

    public DccRequestEvent(Object source) {
        super(source);
    }

    public void setChat(DccChat chat) {
        this.chat = chat;
    }

    public DccChat getChat() {
        return this.chat;
    }
}
