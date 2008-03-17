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
    /** Recording on or off at startup */
    private boolean recordingState;
    /** Recording warning on or off at startup */
    private boolean recordingWarning;
    /** Directory to save session files too */
    private String sessionPath;
    /** Time delay between recording a new page */
    private int recordingInterval;
    /** Auto clean the canvas after saving */
    private boolean autoClear;
    /** Switch Vector Application */
    private String switchVectorApp;
    /** Switch Bitmap Application */
    private String switchBitmapApp;
    /** State of the palette- attached or not */
    protected boolean paletteAttached = false;
    /** Palette Location */
    private Point paletteLocation;
    /** Canvas Window Location */
    private Point canvasLocation;
    /** Canvas Window size */
    private Dimension canvasSize;
    /** Canvas smoothing */
    private boolean smoothing;
    /** Line smoothing */
    private boolean lineSmoothing;
    /** Canvas background colour */
    private int bgColour;
    /** Colour */
    private int colour;

    AlcPreferences() {
        //super(owner);

        prefs = Preferences.userNodeForPackage(getClass());

        recordingState = prefs.getBoolean("Recording State", false);
        recordingWarning = prefs.getBoolean("Recording Warning", true);
        sessionPath = prefs.get("Session Path", DESKTOP_DIR);
        recordingInterval = prefs.getInt("Recording Interval", 5000);
        autoClear = prefs.getBoolean("Auto Clear Canvas", false);
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


    /*
    JCheckBox resetWarnings = new JCheckBox("Reset Warning Dialogs");
    resetWarnings.setSelected(recordingWarning);
    resetWarnings.setToolTipText("Reset the display of warning dialogs");
    resetWarnings.addItemListener(
    new ItemListener() {
    public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
    recordingWarning = true;
    } else {
    recordingWarning = false;
    }
    }
    });
    masterPanel.add(resetWarnings);
     */
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
        prefs.putBoolean("Recording State", recordingState);
        prefs.putBoolean("Recording Warning", recordingWarning);
        prefs.put("Session Path", sessionPath);
        prefs.putInt("Recording Interval", recordingInterval);
        prefs.putBoolean("Auto Clear Canvas", autoClear);
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
    // GETTER / SETTERS
    //////////////////////////////////////////////////////////////
    boolean getRecordingState() {
        return recordingState;
    }

    void setRecordingState(boolean b) {
        recordingState = b;
    }

    boolean getRecordingWarning() {
        return recordingWarning;
    }

    void setRecordingWarning(boolean b) {
        recordingWarning = b;
    }

    String getSessionPath() {
        return sessionPath;
    }

    void setSessionPath(String path) {
        sessionPath = path;
    }

    int getRecordingInterval() {
        return recordingInterval;
    }

    void setRecordingInterval(int i) {
        recordingInterval = i;
    }

    boolean getAutoClear() {
        return autoClear;
    }

    void setAutoClear(boolean b) {
        autoClear = b;
    }

    String getSwitchVectorApp() {
        return switchVectorApp;
    }

    void setSwitchVectorApp(String path) {
        switchVectorApp = path;
    }

    String getSwitchBitmapApp() {
        return switchBitmapApp;
    }

    void setSwitchBitmapApp(String path) {
        switchBitmapApp = path;
    }

    boolean getPaletteAttached() {
        return paletteAttached;
    }

    void setPaletteAttached(boolean b) {
        paletteAttached = b;
    }

    Point getPaletteLocation() {
        return paletteLocation;
    }

    void setPaletteLocation(Point location) {
        paletteLocation = location;
    }

    Point getCanvasLocation() {
        return canvasLocation;
    }

    void setCanvasLocation(Point location) {
        canvasLocation = location;
    }

    Dimension getCanvasSize() {
        return canvasSize;
    }

    void setCanvasSize(Dimension size) {
        canvasSize = size;
    }

    boolean getSmoothing() {
        return smoothing;
    }

    boolean getLineSmoothing() {
        return lineSmoothing;
    }

    int getBgColour() {
        return bgColour;
    }

    int getColour() {
        return colour;
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
