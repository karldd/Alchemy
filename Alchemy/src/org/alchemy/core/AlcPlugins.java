/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2008 Karl D.D. Willis
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
package org.alchemy.core;

import java.io.*;
import java.util.Iterator;
import java.net.URL;
// JAVA PLUGIN FRAMEWORK
import java.util.Arrays;
import java.util.Comparator;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;

/**
 * Handles the loading of Alchemy 'modules' from the modules/ folder
 * Interacts with the Java Plugin Framework
 */
class AlcPlugins implements AlcConstants {

    /** Array of the installed 'create' modules */
    AlcModule[] creates;
    /** Array of the installed 'affect' modules */
    AlcModule[] affects;
    /** The currently selected create module - set to -1 initially when nothing is selected */
    int currentCreate = 0;
    /** The currently selected affect modules */
    boolean[] currentAffects;
    /** The number of affect modules currently selected */
    private int numberOfCurrentAffects = 0;

    // PLUGIN
    private PluginManager pluginManager;
    private int numberOfPlugins;
    private int numberOfCreateModules = 0;
    private int numberOfAffectModules = 0;

    /** Creates a new instance of AlcPlugins */
    AlcPlugins() {

        pluginManager = ObjectFactory.newInstance().createManager();

        /**
         * So that we don't have to have the "core" plugin inside the "plugins" folder
         * where it could get accidentally deleted or moved, we store the core plugin .zip
         * inside the Alchemy JAR file.
         * 
         * However the plugin manager does not like accessing it from inside there.
         * At the moment the code below copies the plugin to the temp directory and
         * acesses it from there. That temp file is deleted on exit.
         * 
         * This is very hacky and not ideal! 
         */

        //Get the Core Plugin as as a resource from the JAR
        InputStream coreStream = this.getClass().getResourceAsStream("/org/alchemy/data/org.alchemy.core-1.0.0.zip");

        File tempCore = null;

        try {
            // Create temp file.
            tempCore = new File(TEMP_DIR, "org.alchemy.core-1.0.0.zip");

            // Delete temp file when program exits.
            tempCore.deleteOnExit();
            // Copy to the temp directory
            AlcUtil.copyFile(coreStream, tempCore);

            //
            if (!tempCore.exists()) {
                System.err.println("ERROR - Core plugin could not be copied to the temp dir: " + TEMP_DIR);
            }

        } catch (IOException e) {
            System.err.println(e);
        }

        //System.out.println(tempCore.toString());

        // Folder of the plugins
        File pluginsDir = new File("modules");

        // Get all plugins that end with .zip
        File[] externalPlugins = pluginsDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".zip");
            }
        });

        // Reorder the plugins array
        File[] plugins = new File[externalPlugins.length + 1];
        //System.out.println(plugins.length);
        plugins[0] = tempCore;
        for (int i = 1; i < plugins.length; i++) {
            //System.out.println(i);
            plugins[i] = externalPlugins[i - 1];
        }


        try {

            PluginLocation[] locations = new PluginLocation[plugins.length];

            // Number of plugins minus one for the core plugin
            numberOfPlugins = plugins.length - 1;

            for (int i = 0; i < plugins.length; i++) {
                //System.out.println(plugins[i].toString());
                locations[i] = StandardPluginLocation.create(plugins[i]);

                // Check for each type of module using the filename
                if (plugins[i].getName().startsWith("org.alchemy.create")) {
                    numberOfCreateModules++;
                } else if (plugins[i].getName().startsWith("org.alchemy.affect")) {
                    numberOfAffectModules++;
                }
            //System.out.println(plugins[i].getName());
            }

            // Registers plug-ins and their locations with this plug-in manager.

            pluginManager.publishPlugins(locations);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Load affects first - zero number of affects is not a problem
        if (getNumberOfAffectModules() > 0) {
            // Initialise the on/off array for current affects
            currentAffects = new boolean[getNumberOfAffectModules()];
            String[] affectsOrder = {"Displace", "Mirror", "Blindness", "Random"};
            // Extension Point Name, Array Size, Module Type
            affects = addPlugins("Affect", getNumberOfAffectModules(), AFFECT, affectsOrder);
        }
        // Load create - zero creates = exit!
        if (getNumberOfCreateModules() > 0) {
            String[] createsOrder = {"Shapes", "Mic Shapes", "Speed Shapes"};
            // Extension Point Name, Array Size, Module Type
            creates = addPlugins("Create", getNumberOfCreateModules(), CREATE, createsOrder);
        } else {
            // Tell the user that there must be at least one create module loaded
            AlcUtil.showConfirmDialog("noCreateModulesDialogTitle", "noCreateModulesDialogMessage", Alchemy.bundle);
            System.exit(0);
        }
    }

    void initialiseModules() {
        // Set the global access to root, canvas, and toolBar for each module
        for (int i = 0; i < creates.length; i++) {
            creates[i].setGlobals(Alchemy.canvas, Alchemy.toolBar, Alchemy.math, Alchemy.colourChooser);
        }
        if (getNumberOfAffectModules() > 0) {
            for (int i = 0; i < affects.length; i++) {
                affects[i].setGlobals(Alchemy.canvas, Alchemy.toolBar, Alchemy.math, Alchemy.colourChooser);
            }
        }

        // Set the default create module
        currentCreate = 0;
        creates[currentCreate].setup();
    }

    AlcModule[] addPlugins(String pointName, int numberOfModules, int moduleType, String[] order) {

        AlcModule[] plugins = new AlcModule[numberOfModules];
        int index = 0;
        int noMatchCount = 0;

        try {
            PluginDescriptor core = pluginManager.getRegistry().getPluginDescriptor("org.alchemy.core");
            //System.out.println("Core ID: " + core.getId());
            ExtensionPoint point = pluginManager.getRegistry().getExtensionPoint(core.getId(), pointName);

            for (Iterator it = point.getConnectedExtensions().iterator(); it.hasNext();) {

                Extension ext = (Extension) it.next();
                PluginDescriptor descr = ext.getDeclaringPluginDescriptor();
                pluginManager.activatePlugin(descr.getId());

                ClassLoader classLoader = pluginManager.getPluginClassLoader(descr);
                Class pluginCls = classLoader.loadClass(ext.getParameter("class").valueAsString());

                /*
                // Get the constructor 
                Constructor constructor = pluginCls.getConstructor(new Class[]{Alchemy.class});
                // Passing the parameters to the constructor
                AlcModule currentPlugin = (AlcModule) constructor.newInstance(new Object[]{root});
                 */

                AlcModule currentPlugin = (AlcModule) pluginCls.newInstance();

                // Set the icon name and the decription name from the XML
                String descriptionParam = ext.getParameter("description").valueAsString();
                String iconParam = ext.getParameter("icon").valueAsString();
                String nameParam = ext.getParameter("name").valueAsString();



                int sortIndex = -1;
                // Assign a sort index to each matching plugin
                for (int i = 0; i < order.length; i++) {
                    if (nameParam.equals(order[i])) {
                        sortIndex = i;
                    }
                }
                // If there were no matches
                if (sortIndex == -1) {
                    noMatchCount++;
                    sortIndex = 100 + noMatchCount;
                }

                plugins[index] = currentPlugin;

                URL iconUrl = null;

                if (iconParam != null) {
                    iconUrl = classLoader.getResource(iconParam);
                    plugins[index].setIconUrl(iconUrl);
                }

                plugins[index].setModuleType(moduleType);
                plugins[index].setName(nameParam);
                plugins[index].setIconName(iconParam);
                plugins[index].setDescription(descriptionParam);
                plugins[index].setSortOrderIndex(sortIndex);
                plugins[index].setClassLoader(classLoader);
                index++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Arrays.sort(plugins, (Comparator<AlcModule>)new PluginComparator());
        Arrays.sort(plugins, new PluginComparator());

        // Loop through once again and set the index
        for (int i = 0; i < plugins.length; i++) {
            plugins[i].setIndex(i);
        }


        return plugins;
    }

    /** Get the number of plugins */
    int getNumberOfPlugins() {
        return numberOfPlugins;
    }

    /** Get the number of create modules */
    int getNumberOfCreateModules() {
        return numberOfCreateModules;
    }

    /** Get the number of affect modules */
    int getNumberOfAffectModules() {
        return numberOfAffectModules;
    }

    /** Return true if there are affect modules currently loaded */
    boolean hasCurrentAffects() {
        if (numberOfCurrentAffects > 0) {
            return true;
        } else {
            return false;
        }
    }

    /** Set the current create function */
    void setCurrentCreate(int i) {
        // Deselect the old create module
        creates[currentCreate].deselect();
        currentCreate = i;

        // Call that module
        // Check to see if it has been loaded, if not load it and run setup()
        if (creates[i].getLoaded()) {
            creates[i].reselect();
        } else {
            creates[i].setLoaded(true);
            creates[i].setup();
        }
    }

    /** Add an affect to the current affect array to be processed
     *  The affect is added at its index value so it can easily be removed later
     */
    void addAffect(int i) {
        numberOfCurrentAffects++;
        currentAffects[i] = true;

        // Call that module
        if (affects[i].getLoaded()) {
            affects[i].reselect();
        } else {
            affects[i].setLoaded(true);
            affects[i].setup();
        }
    }

    /** Remove an affect from the current affect array
     *  The affect is removed at its index value
     */
    void removeAffect(int i) {
        numberOfCurrentAffects--;
        currentAffects[i] = false;
        affects[i].deselect();
    }
}

class PluginComparator implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        AlcModule module1 = ((AlcModule) o1);
        Integer int1 = new Integer(module1.getSortOrderIndex());

        AlcModule module2 = ((AlcModule) o2);
        Integer int2 = new Integer(module2.getSortOrderIndex());

        return int1.compareTo(int2);
    }
}
