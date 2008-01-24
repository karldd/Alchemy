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

    public AlcPreferences(AlcMain root) {

        super(root);
        
        recordingState = prefs.getBoolean("Recording State", true);
        recordingWarning = prefs.getBoolean("Recording Warning", true);
        sessionPath = prefs.get("Session Path", HOME_DIR);
        recordingInterval = prefs.getInt("Recording Delay", 60000);
        autoClear = prefs.getBoolean("Auto Clear Canvas", false);
        switchVectorApp = prefs.get("Switch Vector Application", null);
        switchBitmapApp = prefs.get("Switch Bitmap Application", null);

        //System.out.println("PREFS SAYS:" + switchVectorApp + " " + switchBitmapApp);
        //System.out.println(prefs);


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

        if (switchVectorApp != null) {
            prefs.put("Switch Vector Application", this.switchVectorApp);
        }
        if (switchBitmapApp != null) {
            prefs.put("Switch Bitmap Application", this.switchBitmapApp);
        }
    }

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
}
