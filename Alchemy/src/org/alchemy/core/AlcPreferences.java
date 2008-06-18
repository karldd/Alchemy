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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.prefs.BackingStoreException;
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
    JPanel masterPanel;
    /** Default / Cancel / OK Button Pane */
    JPanel buttonPane;
    /** Ok Button */
    JButton okButton;
    //////////////////////////////////////////////////////////////
    //  MODULES
    //////////////////////////////////////////////////////////////    
    /** Scroll pane for the module listing */
    JScrollPane scrollPane;
    /** Panel containing the modules */
    JPanel modulePanel;
    /** A module list loaded from /modules/modules.txt */
    String[] moduleList;
    /** If the simple modules have been customised */
    boolean modulesSet;
    /** Prefix for the preference node name */
    String modulePrefix = "Module - ";
    /** Change modules when the prefs window is closed */
    boolean changeModules = false;
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
        moduleList = loadModuleList();
        loadPreferences();
    }

    /** Load the preference nodes */
    private void loadPreferences() {
        prefs = Preferences.userNodeForPackage(getClass());

        sessionRecordingState = prefs.getBoolean("Recording State", true);
        sessionRecordingWarning = prefs.getBoolean("Recording Warning", true);
        sessionPath = prefs.get("Session Path", DESKTOP_DIR);
        sessionRecordingInterval = prefs.getInt("Recording Interval", 30000);
        sessionAutoClear = prefs.getBoolean("Auto Clear Canvas", false);
        sessionLink = prefs.getBoolean("Link to Current Session", true);
        switchVectorApp = prefs.get("Switch Vector Application", null);
        switchBitmapApp = prefs.get("Switch Bitmap Application", null);
        paletteAttached = prefs.getBoolean("Palette Attached", false);
        paletteLocation = stringToPoint(prefs.get("Palette Location", null));
        canvasLocation = stringToPoint(prefs.get("Canvas Location", null));
        canvasSize = stringToDimension(prefs.get("Canvas Size", null));
        simpleToolBar = prefs.getBoolean("Simple ToolBar", true);
        modulesSet = prefs.getBoolean("Modules Set", false);
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
        prefs.putBoolean("Modules Set", modulesSet);

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

    /** Reset the preferences */
    private void resetPreferences() {
        try {
            prefs.removeNode();
            loadPreferences();


        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }

    /** Reset the modules to defaults */
    private void resetModules(AlcModule[] modules) {
        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            String moduleName = currentModule.getName();
            final String moduleNodeName = modulePrefix + moduleName;

            if (moduleList == null) {
                prefs.putBoolean(moduleNodeName, true);

            // MODULE LIST
            } else {
                // No preferences, check if it is a default
                boolean hit = false;
                for (int j = 0; j < moduleList.length; j++) {
                    if (moduleName.equals(moduleList[j])) {
                        prefs.putBoolean(moduleNodeName, true);
                        hit = true;
                        break;
                    }
                }
                // Set the preference to false
                if (!hit) {
                    prefs.putBoolean(moduleNodeName, false);
                }

                prefs.putBoolean("Modules Set", modulesSet);
            }
        }
    }

    /** Initialise the preference window */
    void setupWindow(AlcWindow owner) {

        //////////////////////////////////////////////////////////////
        // WINDOW
        //////////////////////////////////////////////////////////////
        prefsWindow = new JDialog(owner);
        prefsWindow.setPreferredSize(new Dimension(400, 300));
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


        //////////////////////////////////////////////////////////////
        // MASTER PANEL
        //////////////////////////////////////////////////////////////
        masterPanel = new JPanel();
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.PAGE_AXIS));
        masterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        masterPanel.setOpaque(true);
        masterPanel.setBackground(AlcToolBar.toolBarBgStartColour);


        //////////////////////////////////////////////////////////////
        // INTERFACE SELECTOR
        //////////////////////////////////////////////////////////////
        JPanel centreRow = new JPanel();
        centreRow.setOpaque(false);
        centreRow.add(new JLabel(Alchemy.bundle.getString("interface") + ": "));


        String[] interfaceType = {Alchemy.bundle.getString("standard"), Alchemy.bundle.getString("simple")};

        final JComboBox interfaceBox = new JComboBox(interfaceType);
        interfaceBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // STANDARD                
                if (interfaceBox.getSelectedIndex() == 0) {
                    Alchemy.preferences.simpleToolBar = false;
                // SIMPLE
                } else {
                    Alchemy.preferences.simpleToolBar = true;
                }
            }
        });

        if (Alchemy.preferences.simpleToolBar) {
            interfaceBox.setSelectedIndex(1);
        }
        centreRow.add(interfaceBox);

        JLabel restart = new JLabel("* " + Alchemy.bundle.getString("restartRequired"));
        restart.setFont(new Font("sansserif", Font.PLAIN, 10));
        restart.setForeground(Color.GRAY);
        centreRow.add(restart);

        masterPanel.add(centreRow);

        //////////////////////////////////////////////////////////////
        // RESTORE DEFAULT BUTTON
        //////////////////////////////////////////////////////////////
        JButton defaultButton = new JButton(Alchemy.bundle.getString("restoreDefaults"));
        defaultButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        resetModules(Alchemy.plugins.creates);
                        resetModules(Alchemy.plugins.affects);
                        refreshModulePanel();
                    }
                });

        JLabel restart2 = new JLabel(restart.getText());
        restart2.setFont(restart.getFont());
        restart2.setForeground(restart.getForeground());


        //////////////////////////////////////////////////////////////
        // CANCEL BUTTON
        //////////////////////////////////////////////////////////////
        JButton cancelButton = new JButton(Alchemy.bundle.getString("cancel"));
        cancelButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        prefsWindow.setVisible(false);
                        refreshModulePanel();
                    }
                });

        //////////////////////////////////////////////////////////////
        // OK BUTTON
        //////////////////////////////////////////////////////////////
        okButton = new JButton(Alchemy.bundle.getString("ok"));
        okButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        changeModules();
                        prefsWindow.setVisible(false);
                    }
                });

        buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPane.add(defaultButton);
        buttonPane.add(restart2);
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(okButton);

        //masterPanel.add(buttonPane);

        prefsWindow.getContentPane().add(masterPanel);
    }

    void showWindow() {
        changeModules = false;
        if (scrollPane == null) {
            setupModulePanel();
            //Add the scroll pane to this panel.
            masterPanel.add(scrollPane);
            masterPanel.add(buttonPane);
            prefsWindow.pack();
        //prefsWindow.getRootPane().setDefaultButton(okButton);
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
        modulesSet = true;


        //Create the scroll pane and add the panel to it.
        scrollPane = new JScrollPane(modulePanel);
        //scrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(300, 200));

    }

    private void refreshModulePanel() {
        masterPanel.remove(scrollPane);
        setupModulePanel();
        masterPanel.add(scrollPane);
        masterPanel.add(buttonPane);
        masterPanel.revalidate();
    }

    private void setupModules(AlcModule[] modules) {

        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            String moduleName = currentModule.getName();
            final String moduleNodeName = modulePrefix + moduleName;
            final JCheckBox checkBox = new JCheckBox(moduleName);
            checkBox.setBackground(AlcToolBar.toolBarBgStartColour);

            // CUSTOM MODULES
            if (modulesSet) {
                // Set the state of the checkbox
                checkBox.setSelected(prefs.getBoolean(moduleNodeName, false));

            // DEFAULT - ALL ON    
            } else if (moduleList == null) {
                checkBox.setSelected(true);
                prefs.putBoolean(moduleNodeName, true);

            // MODULE LIST
            } else {
                // No preferences, check if it is a default
                boolean hit = false;
                for (int j = 0; j < moduleList.length; j++) {
                    if (moduleName.equals(moduleList[j])) {
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

                prefs.putBoolean("Modules Set", modulesSet);
            }

            checkBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    changeModules = true;
                //prefs.putBoolean(moduleNodeName, checkBox.isSelected());
                }
            });

            modulePanel.add(checkBox);
        }
    }

    private void changeModules() {
        // If there has actually been some changes
        if (changeModules) {
            Component[] components = modulePanel.getComponents();
            int creates = Alchemy.plugins.getNumberOfCreateModules();
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) components[i];
                    String moduleName;
                    if (i < creates) {
                        moduleName = Alchemy.plugins.creates[i].getName();
                    //System.out.println("CREATE: " + checkBox.getText() + " " + Alchemy.plugins.creates[i].getName());
                    } else {
                        moduleName = Alchemy.plugins.affects[i - creates].getName();
                    //System.out.println("AFFECT: " + checkBox.getText() + " " + Alchemy.plugins.affects[i - creates].getName());
                    }
                    String moduleNodeName = modulePrefix + moduleName;
                    prefs.putBoolean(moduleNodeName, checkBox.isSelected());
                }

            }
        }
    }

    private String[] loadModuleList() {
        ArrayList<String> modules = new ArrayList<String>();
        Scanner s = null;
        try {
            s = new Scanner(new BufferedReader(new FileReader("modules" + FILE_SEPARATOR + "modules.txt")));
            s.useDelimiter(", ");
            while (s.hasNext()) {

                String module = s.next();
                modules.add(module);
            //System.out.println("Module Name:" + module);

            }
            String[] moduleArray = new String[modules.size()];
            moduleArray = modules.toArray(moduleArray);
            return moduleArray;
        } catch (FileNotFoundException ex) {
            //ex.printStackTrace();
            return null;
        } finally {
            if (s != null) {
                s.close();
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
