package org.antitech.jgir.pluginmanager;

/**
 * All Plugins have to implement this interface.
 *
 */


public interface PluginInterface {

    /** 
     * Acts as a main sorta. 
     * @return returns true if SuccessFul.
     *
     */
    public boolean init();

    /** 
     * Up to the plugin writer
     * to unload their changes.
     * Good idea to keep track of old 
     * references to put it the way it was.
     */
    public boolean unload();
}
