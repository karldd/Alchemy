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

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Preference class used to store preferences.
 */
class AlcPreferences implements AlcConstants {

    /** Preferences package */
    static Preferences prefs;
    /** The preferences window */
    private JDialog prefsWindow;
    /** Main content panel for the window */
    JPanel masterPanel;    //////////////////////////////////////////////////////////////
    // SIMPLE MODULES
    //////////////////////////////////////////////////////////////    
    /** Scroll pane for the module listing */
    JScrollPane scrollPane;
    /** Panel containing the modules */
    JPanel modulePanel;
    /** Default Modules for the simple interface */
    String[] simpleDefaultModules = {"Shapes", "Mirror", "Displace", "Mic Shapes", "Speed Shapes", "Camera Colour"};
    /** If the simple modules have been customised */
    boolean simpleModulesSet;
    /** Prefix for the preference node name */
    String simpleModulePrefix = "Simple Module - ";
    //////////////////////////////////////////////////////////////
    // SESSION
    //////////////////////////////////////////////////////////////
    /** Recording on or off at startup */
    boolean sessionRecordingState;
    /** Recording warning on or off at startup */
    boolean sessionRecordingWarning;
    /** Directory to save session files too */
    String sessionPath;
    /** Time delay between recording a new page */
    int sessionRecordingInterval;
    /** Auto clean the canvas after saving */
    boolean sessionAutoClear;
    /** Link to current setting */
    boolean sessionLink;
    //////////////////////////////////////////////////////////////
    // SWITCH
    //////////////////////////////////////////////////////////////
    /** Switch Vector Application */
    String switchVectorApp;
    /** Switch Bitmap Application */
    String switchBitmapApp;
    //////////////////////////////////////////////////////////////
    // WINDOWS
    //////////////////////////////////////////////////////////////
    /** State of the palette- attached or not */
    boolean paletteAttached;
    /** Palette Location */
    Point paletteLocation;
    /** Canvas Window Location */
    Point canvasLocation;
    /** Canvas Window size */
    Dimension canvasSize;
    /** Simplified toolbar for kids */
    boolean simpleToolBar;
    //////////////////////////////////////////////////////////////
    // DRAWING
    //////////////////////////////////////////////////////////////
    /** Canvas smoothing */
    boolean smoothing;
    /** Line smoothing */
    boolean lineSmoothing;
    /** Canvas background colour */
    int bgColour;
    /** Colour */
    int colour;

    AlcPreferences() {
        //super(owner);

        prefs = Preferences.userNodeForPackage(getClass());
        // Reset the preferences
//        try {
//            prefs.removeNode();
//        } catch (BackingStoreException ex) {
//            ex.printStackTrace();
//        }
//        prefs = Preferences.userNodeForPackage(getClass());

        sessionRecordingState = prefs.getBoolean("Recording State", false);
        sessionRecordingWarning = prefs.getBoolean("Recording Warning", true);
        sessionPath = prefs.get("Session Path", DESKTOP_DIR);
        sessionRecordingInterval = prefs.getInt("Recording Interval", 5000);
        sessionAutoClear = prefs.getBoolean("Auto Clear Canvas", false);
        sessionLink = prefs.getBoolean("Link to Current Session", true);
        switchVectorApp = prefs.get("Switch Vector Application", null);
        switchBitmapApp = prefs.get("Switch Bitmap Application", null);
        paletteAttached = prefs.getBoolean("Palette Attached", false);
        paletteLocation = stringToPoint(prefs.get("Palette Location", null));
        canvasLocation = stringToPoint(prefs.get("Canvas Location", null));
        canvasSize = stringToDimension(prefs.get("Canvas Size", null));
        simpleToolBar = prefs.getBoolean("Simple ToolBar", false);
        simpleModulesSet = prefs.getBoolean("Simple Modules Set", false);
        smoothing = prefs.getBoolean("Smoothing", true);
        lineSmoothing = prefs.getBoolean("Line Smoothing", true);
        bgColour = prefs.getInt("Background Colour", 0xFFFFFF);
        colour = prefs.getInt("Colour", 0x000000);
    }

    /** Save the changes on exit */
    void writeChanges() {
        prefs.putBoolean("Recording State", sessionRecordingState);
        prefs.putBoolean("Recording Warning", sessionRecordingWarning);
        prefs.put("Session Path", sessionPath);
        prefs.putInt("Recording Interval", sessionRecordingInterval);
        prefs.putBoolean("Auto Clear Canvas", sessionAutoClear);
        prefs.putBoolean("Link to Current Session", sessionLink);
        prefs.putBoolean("Palette Attached", paletteAttached);
        prefs.putBoolean("Smoothing", Alchemy.canvas.getSmoothing());
        prefs.putBoolean("Line Smoothing", AlcShape.lineSmoothing);
        prefs.putBoolean("Simple ToolBar", simpleToolBar);
        prefs.putBoolean("Simple Modules Set", simpleModulesSet);

        prefs.putInt("Background Colour", Alchemy.canvas.getBgColour().getRGB());
        prefs.putInt("Colour", Alchemy.canvas.getForegroundColour().getRGB());

        if (switchVectorApp != null) {
            prefs.put("Switch Vector Application", switchVectorApp);
        }
        if (switchBitmapApp != null) {
            prefs.put("Switch Bitmap Application", switchBitmapApp);
        }
        if (paletteLocation != null) {
            prefs.put("Palette Location", pointToString(paletteLocation));
        }
        if (canvasLocation != null) {
            prefs.put("Canvas Location", pointToString(canvasLocation));
        }
        if (canvasSize != null) {
            prefs.put("Canvas Size", dimensionToString(canvasSize));
        }
    }

    /** Initialise the preference window */
    void setupWindow(AlcWindow owner) {

        // The actual prefs window
        prefsWindow = new JDialog(owner);
        prefsWindow.setSize(400, 300);
        String title = "Alchemy Preferences";
        if (Alchemy.PLATFORM == WINDOWS) {
            title = "Alchemy Options";
        }
        prefsWindow.setTitle(title);
        prefsWindow.setResizable(false);
        // Action to close the window when you hit the escape key
        AbstractAction closeAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                prefsWindow.setVisible(false);
            }
        };
        // Link the action to the Escape Key
        prefsWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
        prefsWindow.getRootPane().getActionMap().put("Escape", closeAction);

        // The master panel holding everything
        masterPanel = new JPanel();
        masterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        masterPanel.setOpaque(true);
        masterPanel.setBackground(AlcToolBar.toolBarBgStartColour);

        // TODO - Japanese translation
        masterPanel.add(new JLabel("Interface Mode:"));

        String[] interfaceType = {"Standard", "Simple"};

        final JComboBox interfaceBox = new JComboBox(interfaceType);
        interfaceBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String interfaceMode = interfaceBox.getSelectedItem().toString();
                if (interfaceMode.equals("Standard")) {
                    Alchemy.preferences.simpleToolBar = false;
                    if (modulePanel != null) {
                        setScrollPaneEnabled(false);
                    }
                } else {
                    Alchemy.preferences.simpleToolBar = true;
                    if (modulePanel != null) {
                        setScrollPaneEnabled(true);
                    }
                }
            }
        });

        if (Alchemy.preferences.simpleToolBar) {
            interfaceBox.setSelectedIndex(1);
        }
        masterPanel.add(interfaceBox);

        JLabel restart = new JLabel("* Restart Required");
        restart.setFont(new Font("sansserif", Font.PLAIN, 10));
        restart.setForeground(Color.GRAY);
        masterPanel.add(restart);

        prefsWindow.getContentPane().add(masterPanel);



    }

    void showWindow() {

        if (scrollPane == null) {
            setupModulePanel();
            //Add the scroll pane to this panel.
            masterPanel.add(scrollPane);
        }

        Point loc = AlcUtil.calculateCenter(prefsWindow);
        prefsWindow.setLocation(loc.x, loc.y);
        prefsWindow.setVisible(true);
    }

    private void setupModulePanel() {
        modulePanel = new JPanel();
        modulePanel.setOpaque(true);
        modulePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        modulePanel.setBackground(AlcToolBar.toolBarBgStartColour);
        int plugins = Alchemy.plugins.creates.length + Alchemy.plugins.affects.length;
        modulePanel.setLayout(new GridLayout(plugins, 1, 5, 5));
        //JCheckBox[] checkBoxes = new JCheckBox[plugins];

        setupModules(Alchemy.plugins.creates);
        setupModules(Alchemy.plugins.affects);


        //Create the scroll pane and add the panel to it.
        scrollPane = new JScrollPane(modulePanel);
        //scrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        if (!simpleToolBar) {
            setScrollPaneEnabled(false);
        }
    }

    private void setupModules(AlcModule[] modules) {

        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            String moduleName = currentModule.getName();
            final String moduleNodeName = simpleModulePrefix + moduleName;
            final JCheckBox checkBox = new JCheckBox(moduleName);
            
                // CUSTOM MODULES
                if (simpleModulesSet) {
                    // Set the state of the checkbox
                    checkBox.setSelected(prefs.getBoolean(moduleNodeName, false));

                // DEFAULT MODULES
                } else {
                    // No preferences, check if it is a default
                    boolean hit = false;
                    for (int j = 0; j < simpleDefaultModules.length; j++) {
                        if (moduleName.equals(simpleDefaultModules[j])) {
                            checkBox.setSelected(true);
                            prefs.putBoolean(moduleNodeName, true);
                            hit = true;
                            break;
                        }
                    }
                    // Set the preference to false
                    if (!hit) {
                        prefs.putBoolean(moduleNodeName, false);
                    }
                    simpleModulesSet = true;
                    prefs.putBoolean("Simple Modules Set", simpleModulesSet);
                }
            
            checkBox.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            prefs.putBoolean(moduleNodeName, checkBox.isSelected());
                        }
                    });

            modulePanel.add(checkBox);
        }
    }

    private void setScrollPaneEnabled(boolean enabled) {
        // Disabling a parent does not disable it's children!
        modulePanel.setEnabled(enabled);
        Component[] components = modulePanel.getComponents();
        if (components != null && components.length > 0) {
            int count = components.length;
            for (int i = 0; i < count; i++) {
                components[i].setEnabled(enabled);
            }
        }
        scrollPane.setEnabled(enabled);
        components = scrollPane.getComponents();
        if (components != null && components.length > 0) {
            int count = components.length;
            for (int i = 0; i < count; i++) {
                components[i].setEnabled(enabled);
            }
        }
    }
    //////////////////////////////////////////////////////////////
    // UTILITIES
    //////////////////////////////////////////////////////////////
    /** Converts two numbers stored in the preferences such as:
     *  '10,30' into a Point
     *  
     * @param string    The numbers separated by a comma
     * @return          A Point object 
     */
    private static Point stringToPoint(String string) {
        if (string != null) {
            String[] splitString = string.split(",", 2);
            int x = new Integer(splitString[0]).intValue();
            int y = new Integer(splitString[1]).intValue();
            Point point = new Point(x, y);
            //System.out.println(point);
            return point;
        } else {
            return null;
        }
    }

    /** Converts a point into a string such as:
     *  '10,30'
     * @param point    The point to be converted
     * @return         A string with the points numbers
     */
    private static String pointToString(Point point) {
        if (point != null) {
            String x = String.valueOf(point.x);
            String y = String.valueOf(point.y);
            String xy = x + "," + y;
            //System.out.println(xy);
            return xy;
        } else {
            return null;
        }
    }

    /** Converts two numbers stored in the preferences such as:
     *  '10,30' into a Dimension
     *  
     * @param string    The numbers separated by a comma
     * @return          A Dimension object 
     */
    private static Dimension stringToDimension(String string) {
        if (string != null) {
            String[] splitString = string.split(",", 2);
            int width = new Integer(splitString[0]).intValue();
            int height = new Integer(splitString[1]).intValue();
            Dimension dimension = new Dimension(width, height);
            return dimension;
        } else {
            return null;
        }
    }

    /** Converts a Dimension into a string such as:
     *  '10,30'
     * @param point    The Dimension to be converted
     * @return         A string with the points numbers
     */
    private static String dimensionToString(Dimension dimension) {
        if (dimension != null) {
            String width = String.valueOf(dimension.width);
            String height = String.valueOf(dimension.height);
            String widthHeight = width + "," + height;
            //System.out.println(xy);
            return widthHeight;
        } else {
            return null;
        }
    }
}
