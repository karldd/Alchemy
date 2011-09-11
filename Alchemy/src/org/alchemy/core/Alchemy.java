/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
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

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main class for Alchemy<br />
 * Handles all and everything - the meta 'root' reference
 */
public class Alchemy implements AlcConstants {

    /** Current OS in use, one of OS_WINDOWS, OS_MAC, OS_LINUX or OS_OTHER. */
    public static int OS;
    /** Are you tolerant enough to user the dreaded Windows Vista? */
    public static boolean OS_IS_VISTA = false;
    /** Modifier Key String - This looks like <en>?</em> for Mac or <em>Ctrl</em> otherwise */
    public static String KEY_MODIFIER_STRING = "Ctrl";
    /** Shift Key String - This looks like <en>?</em> for Mac or <em>Shift</em> otherwise */
    public static String KEY_SHIFT_STRING = "Shift";
    /** Alt Key String - This looks like <en>?</em> for Mac or <em>Alt</em> otherwise */
    public static String KEY_ALT_STRING = "Alt";
    

    static {
        if (OS_NAME.indexOf("Mac") != -1) {
            OS = OS_MAC;
            // Unicode sequences to display the correct mac symbols for
            // Command/Apple, Shift, Alt/Option keys
            KEY_MODIFIER_STRING = "?";
            KEY_SHIFT_STRING = "?";
            KEY_ALT_STRING = "?";

        } else if (OS_NAME.indexOf("Windows") != -1) {
            OS = OS_WINDOWS;
            if (OS_NAME.indexOf("Vista") != -1){
                OS_IS_VISTA = true;
            }
           
        } else if (OS_NAME.equals("Linux")) {
            OS = OS_LINUX;

        } else {
            OS = OS_OTHER;
        }
    }
    //////////////////////////////////////////////////////////////
    // ALCHEMY REFERENCES
    //////////////////////////////////////////////////////////////
    /** The Alchemy window */
    static AlcWindow window;
    /** Canvas to draw on to */
    static AlcCanvas canvas;
    /** User Interface Tool Bar */
    static AlcAbstractToolBar toolBar;
    /** Class to take care of plugin loading and activation */
    static AlcPlugins plugins;
    /** Palette for the toolbar when detached */
    static AlcPalette palette;
    /** The menu bar */
    static AlcMenuBar menuBar;
    /** Preferences class */
    static AlcPreferences preferences;
    /** Shortcut manager class */
    static AlcShortcuts shortcuts;
    /** Session class - controls automatic saving of the canvas */
    static AlcSession session;
    /** Resource Bundle containing language specific text */
    static ResourceBundle bundle;
    /** Resource bundle containing English language text
     *  Used for storing variable names in standard ascii characters */
    static ResourceBundle bundleEn;
    /** Class of utility math functions */
    static final AlcMath math = new AlcMath();
    /** Custom reusable color selector */
    static AlcColorSelector colorSelector;
    
    static AlcColourIO colourIO;

    Alchemy() {

        if (OS == OS_MAC) {
            Object appIcon = LookAndFeel.makeIcon(getClass(), "/org/alchemy/data/alchemy-logo64.png");
            UIManager.put("OptionPane.errorIcon", appIcon);
            UIManager.put("OptionPane.informationIcon", appIcon);
            UIManager.put("OptionPane.questionIcon", appIcon);
            UIManager.put("OptionPane.warningIcon", appIcon);
        }

        // Load the Bundle
        try {
            bundleEn = ResourceBundle.getBundle("org/alchemy/core/AlcResourceBundle", new Locale("en"));
        } catch (Exception ex) {
            ex.printStackTrace();
            bundleEn = ResourceBundle.getBundle("org/alchemy/core/AlcResourceBundle");
        }
        try { // Try and get the default bundle
            
            // For Hong Kong lets keep it traditional and use the traditional chinese from the taiwan bundle
            if (LOCALE.getLanguage().equals("zh") && LOCALE.getCountry().equals("HK")) {
                bundle = ResourceBundle.getBundle("org/alchemy/core/AlcResourceBundle", new Locale("zh", "TW"));
            } else {
                bundle = ResourceBundle.getBundle("org/alchemy/core/AlcResourceBundle", LOCALE);
            }
        } catch (Exception ex) {
            // If that fails lets practice our English!
            ex.printStackTrace();
            bundle = bundleEn;
        }

        // LOAD PREFERENCES
        preferences = new AlcPreferences();
        
        // Initiate ColourLovers.com Class
        colourIO = new AlcColourIO();

        // Create the window
        window = new AlcWindow();

        // LOCALE specific text for the Swing components
        UIManager.put("FileChooser.cancelButtonText", bundle.getString("cancel"));
        UIManager.put("FileChooser.newFolderButtonText", bundle.getString("newFolder"));
        UIManager.put("FileChooser.openButtonText", bundle.getString("open"));

        UIManager.put("FileChooser.openDialogTitleText", bundle.getString("open"));
        UIManager.put("FileChooser.saveDialogTitleText", bundle.getString("save"));

        UIManager.put("OptionPane.yesButtonText", bundle.getString("yes"));
        UIManager.put("OptionPane.noButtonText", bundle.getString("no"));
        UIManager.put("OptionPane.okButtonText", bundle.getString("ok"));
        UIManager.put("OptionPane.cancelButtonText", bundle.getString("cancel"));

        // LOAD SHORTCUTS
        shortcuts = new AlcShortcuts(window);

        // Color Selector
        colorSelector = new AlcColorSelector(bundle.getString("colorTitle"));
        //cs.setVisible(true);

        // LOAD PLUGINS
        plugins = new AlcPlugins();
        System.out.println("Number of Plugins: " + plugins.getNumberOfPlugins());

        // LOAD CANVAS
        canvas = new AlcCanvas();
        // LOAD SESSION
        session = new AlcSession();
        // Load the palette
        palette = new AlcPalette(window);

        // User Interface toolbar
        if (preferences.simpleToolBar) {
           toolBar = new AlcSimpleToolBar();
        } else {
           toolBar = new AlcToolBar();
        }

        // Menu Bar
        menuBar = new AlcMenuBar();

        window.setupWindow();
        shortcuts.setupWindow();
        preferences.setupWindow();
        plugins.initialiseModules();

        if (Alchemy.preferences.simpleToolBar) {
            window.setFullscreen(true);
            menuBar.fullScreenItem.setSelected(true);
        }


//        toolBar.removeSubToolBarSection(0);
//        plugins.setCurrentCreate(8);
        

        window.setVisible(true);

        


        // Check for missing language keys
//        new AlcResourceBundleChecker();

        System.out.println("Java Version: " + JAVA_VERSION_NAME);
        System.out.println("Platform: " + OS_NAME);
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("Architecture: " + System.getProperty("os.arch"));
        System.out.println("Language: " + LOCALE.getLanguage());
        System.out.println("Country: " + LOCALE.getCountry());

    }

    public static void main(String[] args) {
        // Set system look and feel
        try {

            //System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + "lib");

            if (OS == OS_MAC) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                //System.setProperty("apple.awt.draggableWindowBackground", "true");
                //System.setProperty("com.apple.mrj.application.growbox.intrudes","false");
                UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");

                String css = "<head>" +
                        "<style type=\"text/css\">" +
                        "b { font: 13pt \"Lucida Grande\" }" +
                        "p { font: 11pt \"Lucida Grande\"; margin-top: 8px }" +
                        "</style>" +
                        "</head>";
                UIManager.put("OptionPane.css", css);

            } else {
//                if(PLATFORM == WINDOWS){
//                    System.setProperty("sun.java2d.noddraw", "true");
//                }
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

        // Custom repaint class to manage transparency and redraw better
        // RepaintManager.setCurrentManager(new AlcRepaintManager());
        // RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
        // JFrame.setDefaultLookAndFeelDecorated(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        new Alchemy();

    }
}
