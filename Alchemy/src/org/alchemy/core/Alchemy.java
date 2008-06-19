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
import java.awt.geom.GeneralPath;
import java.io.File;
import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main class for Alchemy<br />
 * Handles all and everything - the meta 'root' reference
 */
public class Alchemy implements AlcConstants {

    /** Current PLATFORM in use, one of WINDOWS, MACOSX, LINUX or OTHER. */
    public static int PLATFORM;
    /** Modifier Key to show for tool tips - This looks like '\u2318' for Apple or 'Ctrl' otherwise */
    public static String MODIFIER_KEY_STRING = "Ctrl";
    public static String SHIFT_KEY_STRING = "Shift";
    public static String ALT_KEY_STRING = "Alt";
    

    static {
        if (PLATFORM_NAME.indexOf("Mac") != -1) {
            PLATFORM = MACOSX;
            // Unicode sequences to display the correct mac symbols for
            // Command/Apple, Shift, Alt/Option keys
            MODIFIER_KEY_STRING = "\u2318";
            SHIFT_KEY_STRING = "\u21E7";
            ALT_KEY_STRING = "\u2325";

        } else if (PLATFORM_NAME.indexOf("Windows") != -1) {
            PLATFORM = WINDOWS;

        } else if (PLATFORM_NAME.equals("Linux")) {
            PLATFORM = LINUX;

        } else {
            PLATFORM = OTHER;
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
    static AlcMath math = new AlcMath();
    /** Custom reusable colour chooser */
    static AlcColourChooser colourChooser;

    Alchemy() {

        if (PLATFORM == MACOSX) {
            Object appIcon = LookAndFeel.makeIcon(getClass(), "/org/alchemy/data/alchemy-logo64.png");
            UIManager.put("OptionPane.errorIcon", appIcon);
            UIManager.put("OptionPane.informationIcon", appIcon);
            UIManager.put("OptionPane.questionIcon", appIcon);
            UIManager.put("OptionPane.warningIcon", appIcon);
        }

        // LOAD PREFERENCES
        preferences = new AlcPreferences();

        // Create the window
        window = new AlcWindow();

        // LOAD RESOURCE BUNDLE
        bundle = ResourceBundle.getBundle("org/alchemy/core/AlcResourceBundle", LOCALE);
        bundleEn = ResourceBundle.getBundle("org/alchemy/core/AlcResourceBundle", new Locale("en_US"));

        // Create the preferences window
        preferences.setupWindow(window);

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

        // Colour chooser
        colourChooser = new AlcColourChooser(Color.WHITE);

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
        plugins.initialiseModules();

        if (Alchemy.preferences.simpleToolBar) {
            window.setFullscreen(true);
            menuBar.fullScreenItem.setSelected(true);
        }

        window.setVisible(true);

//        GeneralPath[] gps = AlcUtil.getPDFShapes(new File(HOME_DIR + "/Desktop/Alchemy-2008-06-18-22-32-36.pdf"), true);
//        if (gps != null) {
//            for (int i = 0; i < gps.length; i++) {
//                canvas.createShapes.add(new AlcShape(gps[i]));
//            }
//        }
    }

    public static void main(String[] args) {
        // Set system look and feel
        try {

            //System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + "lib");

            if (PLATFORM == MACOSX) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
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
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

        // TODO - Test Quartz Renderer on Leopard
        // apple.awt.graphics.UseQuartz=false?

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
