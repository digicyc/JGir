package org.antitech.jgir.pluginmanager;

/**
 * Our Adapter Class to Call the loader.
 * UI will create instance of this class
 * for loading/unloading plugins as well
 * as reciving Collections of plugins loaded.
 *
 * @author cyclone
 */
import java.util.*;
import java.util.jar.*;
import java.io.*;
import java.awt.Image;
import org.antitech.jgir.pluginmanager.PluginInterface;


public class PluginLoader {
    
    private static final boolean DEBUG = true;
    private static PluginLoader instance = null;
    // .properties file.
    private Properties props = new Properties();
    // Plugin loaded?
    private boolean loaded = false;
    // Jar Files contents.
    private ArrayList contents = new ArrayList();
    private ArrayList loadedPlugins = new ArrayList();
    private ArrayList loadedClasses = new ArrayList();
    // Actual path/jarfile.jar
    private String jarFile = null;
    private JarFile jar = null;
    private PluginInterface plugin = null;
    private Class c = null;
    private JarClassLoader jarLoader = null;
    
    
    private PluginLoader(String jarFile) throws Exception {
        this.jarFile = jarFile;
        // Load in the JarFile
        if (DEBUG)
            System.out.println("JARFILE: " + jarFile);
        jarLoader = new JarClassLoader(jarFile);
        loadedPlugins.add(jarFile);
        jar = new JarFile(jarFile);
        
        loadContents();
        
    }
    
    public static PluginLoader getInstance() {
        return instance;
    }
    
    public static PluginLoader getNewInstance(String jar) throws Exception {
        instance = new PluginLoader(jar);
        return instance;
    }
    
    /** load the contents of the JarFile into an ArrayList. */
    private void loadContents() throws IOException {
        contents.removeAll(contents);
        Enumeration e = jar.entries();
        
        while (e.hasMoreElements()) {
            JarEntry entry = (JarEntry) e.nextElement();
            
            /* Looking for pluin.properties. */
            if (entry.getName().equals("plugin.properties")) {
                InputStream stream = jar.getInputStream(entry);
                props.load(stream);
                
                // Get File Size and name of Jar Plugin.
                File file = new File(this.jarFile);
                
                props.setProperty("size", "" + file.length());
                props.setProperty("fileName", file.getName());
                
                stream.close();
            }
            
            System.out.println("Adding: " + entry.getName());
            contents.add(entry.getName());
        }
    }
    
    /**
     * Load the Plugin and call it's init() method.
     *
     * @return
     *          Returns the instance of the newly created plugin.
     */
    public PluginInterface loadPlugin() {
        Object o = null;
        
        try {
            // Need to get the mainClass from properties file.
            c = jarLoader.loadClass(props.getProperty("mainClass"), true);
            
            // Create the new instance.
            o = c.newInstance();
            if (o == null)
                return null;
        } catch (Exception ex) {
            System.out.println("Unable to loadClass.");
            ex.printStackTrace();
        }
        // Keep track of Loaded classes
        // so we can unload em if requested.
        loadedClasses.add(c);
        
        // Make sure it is an Instance of the PluginInterface.
        if (o instanceof PluginInterface) {
            plugin = (PluginInterface) o;
            // Call the loaded Plugins init() Method.
            this.loaded = plugin.init();
            
            if(loaded) {
                System.out.println("Plugin LOADED!");
                return plugin;
            } else {
                System.out.println("Unable to Load Plugin.");
            }
        }
        
        return null;
        
    }
    
    public void unloadPlugin() {
        boolean unloaded = plugin.unload();
        if (!unloaded)
            System.out.println("[X]Unable to UNLOAD Plugin!");
        this.loaded = false;
        loadedClasses.remove(c);
    }
    
    /** Get an arraylist of loadedPlugins. */
    public ArrayList getPlugins() {
        return loadedPlugins;
    }
    
    public String getName() {
        return (props.getProperty("name"));
    }
    
    public String getDesc() {
        return (props.getProperty("description"));
    }
    
    public String getAuthor() {
        return (props.getProperty("author"));
    }
    
    public String getVersion() {
        return (props.getProperty("version"));
    }

    public String getStatus() {
        if(loaded) {
            return "Loaded";
        } else {
            return "";
        }
    }
}
