package org.antitech.jgir.pluginmanager;

public class JarClassLoader extends PluginClassLoader {


    private JarResources jarResources;

    /** jarName is the .jar File. */
    public JarClassLoader(String jarName) {
        jarResources = new JarResources(jarName);
    }

    protected byte[] loadClassBytes(String className)
    {
        className = formatClassName(className);

        return (jarResources.getResource(className));
    }
}

