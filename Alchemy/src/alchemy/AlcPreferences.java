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

import alchemy.ui.AlcToolBar;
import java.awt.Point;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Preference class used to store preferences.
 */
public class AlcPreferences extends JDialog implements AlcConstants {

    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
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
    private boolean paletteAttached = false;
    /** Palette Location */
    private Point paletteLocation;
    /** Canvas Location */
    private Point canvasLocation;

    public AlcPreferences(AlcMain root) {

        super(root);

        recordingState = prefs.getBoolean("Recording State", false);
        recordingWarning = prefs.getBoolean("Recording Warning", true);
        sessionPath = prefs.get("Session Path", HOME_DIR);
        recordingInterval = prefs.getInt("Recording Delay", 0);
        autoClear = prefs.getBoolean("Auto Clear Canvas", false);
        switchVectorApp = prefs.get("Switch Vector Application", null);
        switchBitmapApp = prefs.get("Switch Bitmap Application", null);
        paletteAttached = prefs.getBoolean("Palette Attached", false);
        paletteLocation = stringToPoint(prefs.get("Palette Location", null));
        canvasLocation = stringToPoint(prefs.get("Canvas Location", null));

        JPanel masterPanel = new JPanel();
        masterPanel.setBackground(AlcToolBar.toolBarBgStartColour);
        masterPanel.add(new JLabel("Not yet implemented..."));

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

        this.getContentPane().add(masterPanel);
        this.setSize(320, 240);
        String title = "Alchemy Options";
        if (AlcMain.PLATFORM == MACOSX) {
            title = "Alchemy Preferences";
        }
        this.setTitle(title);
        this.setResizable(false);

    }

    /** Save the changes on exit */
    void writeChanges() {
        prefs.putBoolean("Recording State", this.recordingState);
        prefs.putBoolean("Recording Warning", this.recordingWarning);
        prefs.put("Session Path", this.sessionPath);
        prefs.putInt("Recording Delay", recordingInterval);
        prefs.putBoolean("Auto Clear Canvas", this.autoClear);
        prefs.putBoolean("Palette Attached", this.paletteAttached);

        if (switchVectorApp != null) {
            prefs.put("Switch Vector Application", this.switchVectorApp);
        }
        if (switchBitmapApp != null) {
            prefs.put("Switch Bitmap Application", this.switchBitmapApp);
        }
        if (paletteLocation != null) {
            prefs.put("Palette Location", pointToString(paletteLocation));
        }
        if (canvasLocation != null) {
            prefs.put("Canvas Location", pointToString(canvasLocation));
        }
    }

    //////////////////////////////////////////////////////////////
    // GETTER / SETTERS
    //////////////////////////////////////////////////////////////
    public boolean getRecordingState() {
        return this.recordingState;
    }

    public void setRecordingState(boolean b) {
        this.recordingState = b;
    }

    public boolean getRecordingWarning() {
        return this.recordingWarning;
    }

    public void setRecordingWarning(boolean b) {
        this.recordingWarning = b;
    }

    public String getSessionPath() {
        return sessionPath;
    }

    public void setSessionPath(String path) {
        this.sessionPath = path;
    }

    public int getRecordingInterval() {
        return this.recordingInterval;
    }

    public void setRecordingInterval(int i) {
        this.recordingInterval = i;
    }

    public boolean getAutoClear() {
        return this.autoClear;
    }

    public void setAutoClear(boolean b) {
        this.autoClear = b;
    }

    public String getSwitchVectorApp() {
        return switchVectorApp;
    }

    public void setSwitchVectorApp(String path) {
        this.switchVectorApp = path;
    }

    public String getSwitchBitmapApp() {
        return switchBitmapApp;
    }

    public void setSwitchBitmapApp(String path) {
        this.switchBitmapApp = path;
    }

    public boolean getPaletteAttached() {
        return this.paletteAttached;
    }

    public void setPaletteAttached(boolean b) {
        this.paletteAttached = b;
    }

    public Point getPaletteLocation() {
        return paletteLocation;
    }

    public void setPaletteLocation(Point location) {
        this.paletteLocation = location;
    }

    public Point getCanvasLocation() {
        return canvasLocation;
    }

    public void setCanvasLocation(Point location) {
        this.canvasLocation = location;
    }


    //////////////////////////////////////////////////////////////
    // UTILITIES
    //////////////////////////////////////////////////////////////
    /** Converts two numbers stored in the prefs such as:
     *  '10,30' into a Point
     *  
     * @param string    The number separated by a comma
     * @return          A Point object 
     */
    private Point stringToPoint(String string) {
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
    private String pointToString(Point point) {
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
}
