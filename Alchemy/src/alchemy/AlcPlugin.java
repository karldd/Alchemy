/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007 Karl D.D. Willis
 * 
 *  Alchemy is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Alchemy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package alchemy;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.net.URL;
// JAVA PLUGIN FRAMEWORK
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;

/**
 * Handles the loading of Alchemy 'modules' from the plugins/ folder
 * Interacts with the Java Plugin Framework
 */
public class AlcPlugin implements AlcConstants {

    // PLUGIN
    private PluginManager pluginManager;
    private int numberOfPlugins;
    private int numberOfCreateModules = 0;
    private int numberOfAffectModules = 0;
    private final AlcMain root;

    /** Creates a new instance of AlcPlugin */
    public AlcPlugin(AlcMain root) {

        this.root = root;

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
            numberOfPlugins = plugins.length - 1;

            for (int i = 0; i < plugins.length; i++) {
                locations[i] = StandardPluginLocation.create(plugins[i]);

                // Check for each type of module using the filename
                if (plugins[i].getName().startsWith("alchemy.create")) {
                    numberOfCreateModules++;
                } else if (plugins[i].getName().startsWith("alchemy.affect")) {
                    numberOfAffectModules++;
                }
            //System.out.println(plugins[i].getName());
            }

            // Registers plug-ins and their locations with this plug-in manager.
            pluginManager.publishPlugins(locations);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public AlcModule[] addPlugins(String pointName, int numberOfModules, int moduleType, String[] order) {

        AlcModule[] plugins = new AlcModule[numberOfModules];
        int noMatchCount = 0;

        try {
            PluginDescriptor core = pluginManager.getRegistry().getPluginDescriptor("alchemy.core");

            ExtensionPoint point = pluginManager.getRegistry().getExtensionPoint(core.getId(), pointName);

            for (Iterator<Extension> it = point.getConnectedExtensions().iterator(); it.hasNext();) {

                Extension ext = it.next();
                PluginDescriptor descr = ext.getDeclaringPluginDescriptor();
                pluginManager.activatePlugin(descr.getId());

                ClassLoader classLoader = pluginManager.getPluginClassLoader(descr);
                Class pluginCls = classLoader.loadClass(ext.getParameter("class").valueAsString());

                /*
                // Get the constructor 
                Constructor constructor = pluginCls.getConstructor(new Class[]{AlcMain.class});
                // Passing the parameters to the constructor
                AlcModule currentPlugin = (AlcModule) constructor.newInstance(new Object[]{root});
                 */

                AlcModule currentPlugin = (AlcModule) pluginCls.newInstance();

                // Set the icon name and the decription name from the XML
                String descriptionParam = ext.getParameter("description").valueAsString();
                String iconParam = ext.getParameter("icon").valueAsString();
                String nameParam = ext.getParameter("name").valueAsString();

                // Set the index to negative so we can test if it has been set later
                int index = -1;
                // Loop through the order list given - somewhat inefficient?
                for (int i = 0; i < order.length; i++) {
                    // Check if this one matches
                    if (order[i].equals(nameParam)) {
                        plugins[i] = currentPlugin;
                        index = i;
                    }
                }

                // If there was no match, then add the module on to the end
                if (index < 0) {
                    index = order.length + noMatchCount;
                    //System.out.println(nameParam);
                    //System.out.println("Plugins: " + index + " / " + numberOfPlugins + " No Match: " + noMatchCount);
                    plugins[index] = currentPlugin;
                    // Keep track of how many non-matches
                    noMatchCount++;
                }

                URL iconUrl = null;

                if (iconParam != null) {
                    iconUrl = classLoader.getResource(iconParam);
                    plugins[index].setIconUrl(iconUrl);
                }

                plugins[index].setModuleType(moduleType);
                plugins[index].setName(nameParam);
                plugins[index].setIconName(iconParam);
                plugins[index].setDescription(descriptionParam);
                plugins[index].setIndex(index);
                plugins[index].setClassLoader(classLoader);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return plugins;
    }

    public int getNumberOfPlugins() {
        return numberOfPlugins;
    }

    public int getNumberOfCreateModules() {
        return numberOfCreateModules;
    }

    public int getNumberOfAffectModules() {
        return numberOfAffectModules;
    }
}
