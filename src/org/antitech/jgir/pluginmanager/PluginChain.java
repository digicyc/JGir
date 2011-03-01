package org.antitech.jgir.pluginmanager;

import java.util.Iterator;
import java.util.Vector;

public class PluginChain {
    private static Vector pluginListeners = new Vector();

    public static void addListener(PluginEventListener listener) {
        pluginListeners.add(listener);
    }

    public static void removeListener(PluginEventListener listener) {
        pluginListeners.remove(listener);
    }

    public static void fireEvent(PluginEvent e) {
        Iterator i = pluginListeners.iterator();

        while (i.hasNext()) {
            PluginEventListener listener = (PluginEventListener)i.next();
            listener.handleEvent(e);
        }
    }
}
