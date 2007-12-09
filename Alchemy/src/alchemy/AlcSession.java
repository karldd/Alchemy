package alchemy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;

/**
 *
 * @author karldd
 */
public class AlcSession implements ActionListener, AlcConstants {

    private AlcMain root;
    /** How many times the record function has been involved */
    private int saveCount = 1; // TODO - figure out a way to not over write files in the same directory
    /** Timer */
    private Timer timer;
    /** Recording interval array in milliseconds */
    public int[] recordingInterval = {5000, 15000, 30000, 60000, 120000, 300000, 600000};
    /** Recording interval array in readable form */
    public String[] recordingIntervalString = {"5 sec", "15 sec", "30 sec", "1 min", "2 mins", "5 mins", "10 mins"};
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

            String fileName = "Alchemy" + AlcUtil.dateStamp("-yyyy-MM-dd-") + AlcUtil.zeroPad(saveCount, 4) + ".pdf";
            currentPdfFile = new File(root.prefs.getSessionPath(), fileName);

            //currentPdfFile = root.prefs.getSessionPath() + FILE_SEPARATOR + "Alchemy" + AlcUtil.dateStamp("-yyyy-MM-dd-") + AlcUtil.zeroPad(saveCount, 4) + ".pdf";

            //Set up timer to save pages into the pdf
            if (timer == null) {
                timer = new Timer(root.prefs.getRecordingInterval(), this);
                timer.start();
            } else {
                if (timer.isRunning()) {
                    timer.stop();
                }
                timer.setDelay(root.prefs.getRecordingInterval());
                timer.start();
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
            saveCount++;
        } else {

            if (timer != null) {
                // if it is running then stop it
                if (timer.isRunning()) {
                    timer.stop();
                }
            }

            System.out.println("recording off..." + currentPdfFile);
            root.canvas.endPdf();
        //openFile(currentPdfFile);

        }
        //Remember the record start
        recordState = record;
    }

    public void setTimerInterval(int interval) {
        System.out.println("Interval: " + interval);
        // Set the interval in the prefs
        root.prefs.setRecordingInterval(interval);
        // Check if the timer has been initialised, if not don't do anything extra
        if (timer != null) {
            // if it is running then stop it, set the interval then restart it
            if (timer.isRunning()) {
                timer.stop();
                timer.setDelay(interval);
                timer.start();
            } else {
                timer.setDelay(interval);
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

    // Called by the timer
    public void actionPerformed(ActionEvent e) {
        // If the canvas has changed
        if (root.canvas.canvasChange()) {
            System.out.println("SAVE FRAME CALL FROM TIMER");
            root.canvas.savePdfFrame();
            if (root.prefs.getAutoClear()) {
                root.canvas.clear();
            }
            pageCount++;
        }

    }
}
