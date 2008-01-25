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
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * Class to control Alchemy 'sessions'
 * Timing and recording of drawing sessions in a PDF file using the iText Library 
 */
public class AlcSession implements ActionListener, AlcConstants {

    private AlcMain root;
    /** Timer */
    private Timer timer;
    /** Recording interval array in milliseconds */
    public int[] recordingInterval = {0, 5000, 15000, 30000, 60000, 120000, 300000, 600000};
    /** Recording interval array in readable form */
    public String[] recordingIntervalString = {"Manual", "5 sec", "15 sec", "30 sec", "1 min", "2 mins", "5 mins", "10 mins"};
    /** Recording on or off */
    private boolean recordState;
    /** Current file path */
    private File currentPdfFile;
    /** Count of pages affed to the pdf */
    private int pageCount = 0;

    public AlcSession(AlcMain root) {
        this.root = root;
    }

    public void setRecording(boolean record) {
        if (record) {

            if (root.prefs.getRecordingWarning()) {
                JOptionPane.showMessageDialog(
                        null,
                        "The session pdf will be saved to:\n\n" +
                        root.prefs.getSessionPath() +
                        "\n\nWhen finished be sure to toggle recording off\n" +
                        "in order to view the PDF file",
                        "Recording Started",
                        JOptionPane.INFORMATION_MESSAGE);
                root.prefs.setRecordingWarning(false);
            }

            String fileName = "Alchemy" + AlcUtil.dateStamp("-yyyy-MM-dd-mm-ss") + ".pdf";
            currentPdfFile = new File(root.prefs.getSessionPath(), fileName);

            //currentPdfFile = root.prefs.getSessionPath() + FILE_SEPARATOR + "Alchemy" + AlcUtil.dateStamp("-yyyy-MM-dd-") + AlcUtil.zeroPad(saveCount, 4) + ".pdf";

            int interval = root.prefs.getRecordingInterval();
            //Set up timer to save pages into the pdf
            if (timer == null) {
                if (interval > 0) {
                    timer = new Timer(interval, this);
                    timer.start();
                }
            } else {
                if (timer.isRunning()) {
                    timer.stop();
                }
                if (interval > 0) {
                    timer.setDelay(root.prefs.getRecordingInterval());
                    timer.start();
                }
            }
            //timer.setInitialDelay(root.prefs.getRecordingInterval());
            //timer.setRepeats(boolean flag);

            // Keep track of the amount of shapes
            //if (root.canvas != null) {
            root.canvas.resetCanvasChange();
            //}

            // Start the timer


            System.out.println("Set Recording called: " + currentPdfFile.toString());
            root.canvas.startPdf(currentPdfFile);
            pageCount = 0;

        } else {

            if (timer != null) {
                // if it is running then stop it
                if (timer.isRunning()) {
                    timer.stop();
                }
            }

            System.out.println("recording off..." + currentPdfFile);
            root.canvas.endPdf();

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
                        timer = new Timer(interval, this);
                        timer.start();
                    }
                } else {
                    timer = null;
                    timer = new Timer(interval, this);
                    timer.start();
                }
            } else {
                if (interval > 0) {
                    timer = new Timer(interval, this);
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
        System.out.println("get Current pdf path called : " + currentPdfFile.toString());
        return currentPdfFile;
    }

    /** Return the current amount of pages added to the pdf */
    public int getPageCount() {
        return pageCount;
    }

    /** Save a single page to the current pdf being created */
    public void savePage() {
        if (recordState) {
            root.canvas.savePdfPage();
            pageCount++;
            if (timer != null) {
                if (timer.isRunning()) {
                    System.out.println("Timer Restarted");
                    timer.restart();
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Please turn on recording using the : \n " +
                    "Session > Toggle Recording menu", "Recording not activated",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Save a single page to the current pdf being created, then clear the canvas */
    public void saveClearPage() {
        if (recordState) {
            root.canvas.savePdfPage();
            root.canvas.clear();
            pageCount++;
            if (timer != null) {
                if (timer.isRunning()) {
                    System.out.println("Timer Restarted");
                    timer.restart();
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Please turn on recording using the : \n " +
                    "Session > Toggle Recording menu", "Recording not activated",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    // Called by the timer
    public void actionPerformed(ActionEvent e) {
        // If the canvas has changed
        if (root.canvas.canvasChange()) {
            System.out.println("SAVE FRAME CALL FROM TIMER");
            root.canvas.savePdfPage();
            if (root.prefs.getAutoClear()) {
                root.canvas.clear();
            }
            pageCount++;
        }

    }
}
