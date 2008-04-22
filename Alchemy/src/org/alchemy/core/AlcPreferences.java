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
package org.alchemy.core;

import java.awt.Dimension;
import java.awt.Point;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Preference class used to store preferences.
 */
class AlcPreferences implements AlcConstants {

    /** Preferences package */
    static Preferences prefs;
    /** The preferences window */
    private JDialog prefsWindow;
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
    boolean paletteAttached = false;
    /** Palette Location */
    Point paletteLocation;
    /** Canvas Window Location */
    Point canvasLocation;
    /** Canvas Window size */
    Dimension canvasSize;
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
        smoothing = prefs.getBoolean("Smoothing", true);
        lineSmoothing = prefs.getBoolean("Line Smoothing", true);
        bgColour = prefs.getInt("Background Colour", 0xFFFFFF);
        colour = prefs.getInt("Colour", 0x000000);
    }

    /** Initialise the preference window */
    void setupWindow(AlcWindow owner) {

        prefsWindow = new JDialog(owner);

        JPanel masterPanel = new JPanel();
        masterPanel.setOpaque(true);
        masterPanel.setBackground(AlcToolBar.toolBarBgStartColour);
        masterPanel.add(new JLabel("Not yet implemented..."));

        prefsWindow.getContentPane().add(masterPanel);
        prefsWindow.setSize(400, 240);
        String title = "Alchemy Options";
        if (Alchemy.PLATFORM == MACOSX) {
            title = "Alchemy Preferences";
        }
        prefsWindow.setTitle(title);
        prefsWindow.setResizable(false);
    }

    void showWindow() {
        Point loc = AlcUtil.calculateCenter(prefsWindow);
        prefsWindow.setLocation(loc.x, loc.y);
        prefsWindow.setVisible(true);
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
