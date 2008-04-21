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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import com.sun.pdfview.*;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Class to control Alchemy 'sessions'
 * Timing and recording of drawing sessions in a PDF file using the iText Library 
 */
class AlcSession implements ActionListener, AlcConstants {

    /** Recording Timer */
    private javax.swing.Timer timer;
    /** Recording on or off */
    private boolean recordState;
    /** PDF write file */
    private File pdfWriteFile;
    /** Record Indicator Timer */
    private javax.swing.Timer indicatorTimer;
    /** PDF read file */
    private PDFFile pdfReadFile;
    /** PDF read page */
    PDFPage pdfReadPage;
    /**  Current page of the read PDF */
    private int currentPdfReadPage = 1;
    /** Number of pages of the read PDF */
    private int maxPdfReadPage;

    AlcSession() {
    }

    void setRecording(boolean record) {
        if (record) {

            int interval = Alchemy.preferences.getRecordingInterval();
            //Set up timer to save pages into the pdf
            if (timer == null) {
                timer = new javax.swing.Timer(interval, this);
                timer.start();
            } else {
                if (timer.isRunning()) {
                    timer.stop();
                }
                timer.setDelay(Alchemy.preferences.getRecordingInterval());
                timer.start();

            }
            Alchemy.canvas.resetCanvasChange();

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

    void setTimerInterval(int interval) {
        System.out.println("Interval: " + interval);
        // Set the interval in the preferences
        Alchemy.preferences.setRecordingInterval(interval);
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

    boolean isRecording() {
        return recordState;
    }

    /** Return the current file being created by the pdf */
    File getCurrentPdfPath() {
        return pdfWriteFile;
    }

    /** Manually save a pdfReadPage then restart the timer */
    void manualSavePage() {
        savePage();
        restartTimer();
    }

    /** Manually save and clear a pdfReadPage then restart the timer */
    void manualSaveClearPage() {
        saveClearPage();
        restartTimer();
    }

    /** Save a single pdfReadPage to the current pdf being created */
    boolean savePage() {
        // If this is the first time or if the file is not actually there
        if (pdfWriteFile == null || !pdfWriteFile.exists()) {
            String fileName = "Alchemy" + AlcUtil.dateStamp("-yyyy-MM-dd-HH-mm-ss") + ".pdf";
            pdfWriteFile = new File(Alchemy.preferences.getSessionPath(), fileName);
            System.out.println("Current PDF file: " + pdfWriteFile.getPath());
            return Alchemy.canvas.saveSinglePdf(pdfWriteFile);

        // Else save a temp file then join the two together
        } else {

            try {
                File temp = File.createTempFile("AlchemyPage", ".pdf");
                // Delete temp file when program exits.
                //temp.deleteOnExit();
                // Make the temp pdf
                Alchemy.canvas.saveSinglePdf(temp);
                boolean jointUp = Alchemy.canvas.addPageToPdf(pdfWriteFile, temp);
                if (jointUp) {
                    //System.out.println("Pdf files joint");
                    temp.delete();
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /** Save a single pdfReadPage to the current pdf being created, then clear the canvas */
    void saveClearPage() {
        savePage();
        //Alchemy.canvas.savePdfPage();
        Alchemy.canvas.clear();
    }

    private void restartTimer() {
        if (timer != null) {
            if (timer.isRunning()) {
                System.out.println("Timer Restarted");
                timer.restart();
            }
        }
    }

    void restartSession() {
        pdfWriteFile = null;
    }

    //////////////////////////////////////////////////////////////
    // PDF READER STUFF
    //////////////////////////////////////////////////////////////
    /** Load a session file to draw on top of */
    void loadSessionFile(File file) {
        try {
            //File file = new File("/Users/karldd/Alchemy/Code/svnAlchemy/ok.pdf");

            // set up the PDF reading
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            pdfReadFile = new PDFFile(buf);
            currentPdfReadPage = 1;
            maxPdfReadPage = pdfReadFile.getNumPages();
            pdfReadPage = pdfReadFile.getPage(currentPdfReadPage);
            Alchemy.canvas.redraw(true);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** Move to the next pdfReadPage in the session file */
    void nextPage() {
        if (pdfReadFile != null) {
            if (currentPdfReadPage + 1 <= maxPdfReadPage) {
                currentPdfReadPage++;
                //System.out.println(currentPdfReadPage + " " + maxPdfReadPage);
                pdfReadPage = pdfReadFile.getPage(currentPdfReadPage);
                Alchemy.canvas.redraw(true);
            } else {
                TOOLKIT.beep();
            }
        }
    }

    /** Move to the previous pdfReadPage in the session file */
    void previousPage() {
        if (pdfReadFile != null) {
            if (currentPdfReadPage - 1 >= 1) {
                currentPdfReadPage--;
                //System.out.println(currentPdfReadPage + " " + maxPdfReadPage);
                pdfReadPage = pdfReadFile.getPage(currentPdfReadPage);
                Alchemy.canvas.redraw(true);
            } else {
                TOOLKIT.beep();
            }
        }
    }

    /** Unload the session file and redraw the canvas */
    void unloadSessionFile() {
        pdfReadFile = null;
        pdfReadPage = null;
        Alchemy.canvas.redraw(true);
    }


    // Called by the timer
    public void actionPerformed(ActionEvent e) {
        // If the canvas has changed
        if (Alchemy.canvas.canvasChange()) {

            // If the pdfReadPage has been saved
            if (savePage()) {
                // Show this with a small red circle on the canvas
                Alchemy.canvas.setRecordIndicator(true);

                if (indicatorTimer == null) {
                    indicatorTimer = new javax.swing.Timer(500, new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            //System.out.println("indicatorTimer action called");
                            Alchemy.canvas.setRecordIndicator(false);
                            Alchemy.canvas.redraw();
                            indicatorTimer.stop();
                            indicatorTimer = null;
                        }
                    });
                    indicatorTimer.start();
                }
                Alchemy.canvas.redraw();
            }

            Alchemy.canvas.resetCanvasChange();
            if (Alchemy.preferences.getAutoClear()) {
                Alchemy.canvas.clear();
            }
        }
    }
}
