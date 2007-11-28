/**
 * AlcPlugin.java
 *
 * Created on November 22, 2007, 6:38 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;

import java.util.ArrayList;

import java.net.URI;
import java.net.URL;

// JAVA PLUGIN FRAMEWORK
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;

public class AlcPlugin {
    
    // PLUGIN
    private PluginManager pluginManager;
    
    private int numberOfPlugins;
    
    /** Creates a new instance of AlcPlugin */
    public AlcPlugin() {
        
        pluginManager = ObjectFactory.newInstance().createManager();
        
        // Folder of the plugins
        File pluginsDir = new File("plugins");
        
        // Get all plugins that end with .zip
        File[] plugins = pluginsDir.listFiles(new FilenameFilter() {
            
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".zip");
            }
            
        });
        
        try {
            
            PluginLocation[] locations = new PluginLocation[plugins.length];
            
            // Number of plugins minus one for the core plugin
            numberOfPlugins = plugins.length-1;
            
            for (int i = 0; i < plugins.length; i++) {
                locations[i] = StandardPluginLocation.create(plugins[i]);
                //println(plugins[i].getAbsolutePath());
            }
            
            // Registers plug-ins and their locations with this plug-in manager.
            pluginManager.publishPlugins(locations);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        
    }
    
    public ArrayList<AlcModule> addPlugins(String pointName){
        
        ArrayList<AlcModule> plugins = new ArrayList<AlcModule>();
        
        try{
            PluginDescriptor core = pluginManager.getRegistry().getPluginDescriptor("alchemy.core");
            
            ExtensionPoint point = pluginManager.getRegistry().getExtensionPoint(core.getId(), pointName);
            
            for (Iterator<Extension> it = point.getConnectedExtensions().iterator(); it.hasNext();) {
                
                Extension ext = it.next();
                PluginDescriptor descr = ext.getDeclaringPluginDescriptor();
                pluginManager.activatePlugin(descr.getId());
                
                ClassLoader classLoader = pluginManager.getPluginClassLoader(descr);
                Class pluginCls = classLoader.loadClass(ext.getParameter("class").valueAsString());
                
                plugins.add( (AlcModule)pluginCls.newInstance() );
                AlcModule currentPlugin = plugins.get(plugins.size()-1);
                
                // GET THE FILE PATH & ICON NAME
                // Return the path of the XML file as a string
                String path = descr.getLocation().getPath();
                // Remove the XML file name after the "!" mark and make a URI
                URI pathUri = new URI(path.substring(0, path.lastIndexOf("!")));
                // Convert it into an abstract file name
                File pathFile = new File(pathUri);
                
                if(pathFile.exists()){
                    // TODO do we need this pathFile??
                    currentPlugin.setPluginPath(pathFile);
                    System.out.println("Loaded " + pathFile.getPath());
                }
                
                // Set the icon name and the decription name from the XML
                String descriptionParam = ext.getParameter("description").valueAsString();
                String iconParam = ext.getParameter("icon").valueAsString();
                String nameParam = ext.getParameter("name").valueAsString();
                
                URL iconUrl = null;
                
                if (iconParam != null) {
                    iconUrl = classLoader.getResource(iconParam);
                    currentPlugin.setIconUrl(iconUrl);
                }
               
                /*
                // Get the ID
                String name = descr.getId();
                name = name.substring(name.lastIndexOf(".")+1);
                println(name);
                 */
                
                currentPlugin.setName(nameParam);
                currentPlugin.setIconName(iconParam);
                currentPlugin.setDescriptionName(descriptionParam);
                currentPlugin.setId(plugins.size()-1);
                
                //textFont(font);
                //text(modules.getName(), 15, 60, -30);
                //println(modules.getName());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return plugins;
    }
    
    public int getNumberOfPlugins(){
        return numberOfPlugins;
    }
    
}
