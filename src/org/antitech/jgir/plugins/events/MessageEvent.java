package org.antitech.jgir.plugins.events;

import org.antitech.jgir.pluginmanager.PluginEvent;


public class MessageEvent extends PluginEvent {
    private String channel;
    private String sender;
    private String login;
    private String hostname;
    private String message;

    public MessageEvent(Object source) {
        super(source);
    }

    public void sendInfo(String channel, String sender,
                    String login, String hostname,
                    String message) {
        this.channel = channel;
        this.sender = sender;
        this.login = login;
        this.hostname = hostname;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public String getSender() {
        return sender;
    }

    public String getLogin() {
        return login;
    }

    public String getHostname() {
        return hostname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
