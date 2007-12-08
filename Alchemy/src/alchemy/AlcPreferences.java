/*
 *   Part of the Alchemy project - http://al.chemy.org
 * 
 *   Copyright (c) 2007 Karl D.D. Willis
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General
 *   Public License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA  02111-1307  USA
 */
package alchemy;

import java.util.prefs.Preferences;

/**
 * AlcPreferences
 * Class used to store preferences.
 * 
 */
public class AlcPreferences implements AlcConstants {

    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    /** Recording on or off at startup */
    private boolean recordingState;
    /** Directory to save session files too */
    private String sessionPath;
    /** Time delay between recording a new page */
    private int recordingInterval;
    /** Auto clean the canvas after saving */
    private boolean autoClear;

    public AlcPreferences() {

        recordingState = prefs.getBoolean("Recording State", true);
        sessionPath = prefs.get("Session Path", HOME_DIRECTORY);
        recordingInterval = prefs.getInt("Recording Delay", 60000);
        autoClear = prefs.getBoolean("Auto Clear Canvas", false);

        System.out.println("PREFS SAYS:" + recordingState + " " + sessionPath + " " + recordingInterval);

    }

    void writeChanges() {
        prefs.putBoolean("Recording State", this.recordingState);
        prefs.put("Session Path", this.sessionPath);
        prefs.putInt("Recording Delay", recordingInterval);
        prefs.putBoolean("Auto Clear Canvas", this.autoClear);
    }

    public boolean getRecordingState() {
        return this.recordingState;
    }

    public void setRecordingState(boolean b) {
        this.recordingState = b;
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
}
