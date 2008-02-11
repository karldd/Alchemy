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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Class to control Alchemy 'sessions'
 * Timing and recording of drawing sessions in a PDF file using the iText Library 
 */
public class AlcSession implements ActionListener, AlcConstants {

    private AlcMain root;
    /** Timer */
    private javax.swing.Timer timer;
    /** Recording on or off */
    private boolean recordState;
    /** Current file path */
    private File currentPdfFile;

    public AlcSession(AlcMain root) {
        this.root = root;
    }

    public void setRecording(boolean record) {
        if (record) {

            int interval = root.prefs.getRecordingInterval();
            //Set up timer to save pages into the pdf
            if (timer == null) {
                timer = new javax.swing.Timer(interval, this);
                timer.start();
            } else {
                if (timer.isRunning()) {
                    timer.stop();
                }
                timer.setDelay(root.prefs.getRecordingInterval());
                timer.start();

            }
            root.canvas.resetCanvasChange();

        } else {

            if (timer != null) {
                // if it is running then stop it
                if (timer.isRunning()) {
                    timer.stop();
                }
            }

        }
        //Remember the record start
        recordState = record;
    }

    public void setTimerInterval(int interval) {
        System.out.println("Interval: " + interval);
        // Set the interval in the prefs
        root.prefs.setRecordingInterval(interval);
        // If recording is on
        if (recordState) {
            // Check if the timer has been initialised, if not don't do anything extra
            if (timer != null) {
                // if it is running then stop it, set the interval then restart it
                if (timer.isRunning()) {
                    timer.stop();
                    timer = null;
                    if (interval > 0) {
                        timer = new javax.swing.Timer(interval, this);
                        timer.start();
                    }
                } else {
                    timer = null;
                    timer = new javax.swing.Timer(interval, this);
                    timer.start();
                }
            } else {
                if (interval > 0) {
                    timer = new javax.swing.Timer(interval, this);
                    timer.start();
                }
            }
        }
    }

    public boolean isRecording() {
        return recordState;
    }

    /** Return the current file being created by the pdf */
    public File getCurrentPdfPath() {
        return currentPdfFile;
    }

    /** Manually save a page then restart the timer */
    public void manualSavePage() {
        savePage();
        restartTimer();
    }

    /** Manually save and clear a page then restart the timer */
    public void manualSaveClearPage() {
        saveClearPage();
        restartTimer();
    }

    /** Save a single page to the current pdf being created */
    public void savePage() {
        // If this is the first time or if the file is not actually there
        if (currentPdfFile == null || !currentPdfFile.exists()) {
            String fileName = "Alchemy" + AlcUtil.dateStamp("-yyyy-MM-dd-HH-mm-ss") + ".pdf";
            currentPdfFile = new File(root.prefs.getSessionPath(), fileName);
            System.out.println("Current PDF file: " + currentPdfFile.getPath());
            root.canvas.saveSinglePdf(currentPdfFile);
        // Else save a temp file then join the two together
        } else {

            try {
                File temp = File.createTempFile("AlchemyPage", ".pdf");
                // Delete temp file when program exits.
                //temp.deleteOnExit();
                // Make the temp pdf
                root.canvas.saveSinglePdf(temp);
                boolean jointUp = root.canvas.addPageToPdf(currentPdfFile, temp);
                if (jointUp) {
                    //System.out.println("Pdf files joint");
                    temp.delete();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /** Save a single page to the current pdf being created, then clear the canvas */
    public void saveClearPage() {
        savePage();
        //root.canvas.savePdfPage();
        root.canvas.clear();
    }

    private void restartTimer() {
        if (timer != null) {
            if (timer.isRunning()) {
                System.out.println("Timer Restarted");
                timer.restart();
            }
        }
    }

    public void restartSession() {
        currentPdfFile = null;
    }


    // Called by the timer
    public void actionPerformed(ActionEvent e) {
        // If the canvas has changed
        if (root.canvas.canvasChange()) {
            System.out.println("SAVE FRAME CALL FROM TIMER");
            savePage();
            root.canvas.resetCanvasChange();
            //root.canvas.savePdfPage();
            if (root.prefs.getAutoClear()) {
                root.canvas.clear();
            }
        }

    }
}
