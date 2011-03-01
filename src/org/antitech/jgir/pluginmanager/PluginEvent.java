package org.antitech.jgir.pluginmanager;

public class PluginEvent {
    protected Object source;

    public PluginEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
