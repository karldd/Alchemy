/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
 *  Copyright (c) 2009 Steren Giannini (steren.giannini@gmail.com)
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

import java.awt.event.*;
import java.io.*;
import com.sun.pdfview.*;
import eu.medsea.util.MimeUtil;
import java.awt.Graphics2D;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.xml.xmp.*;

// BATIK (for svg)
import java.awt.Dimension;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.dom.GenericDOMImplementation;
//import org.w3c.dom.Document; //because of Document class conflict, do not import
import org.w3c.dom.DOMImplementation;

/**
 * Class to control Alchemy 'sessions'
 * Timing, recording, loading of PDF drawing sessions 
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

    //////////////////////////////////////////////////////////////
    // RECORDING / TIMER
    //////////////////////////////////////////////////////////////
    /** Start or end recording
     * @param record    The new recording state
     */
    void setRecording(boolean record) {
        if (record) {

            int interval = Alchemy.preferences.sessionRecordingInterval;
            //Set up timer to save pages into the pdf
            if (timer == null) {
                timer = new javax.swing.Timer(interval, this);
                timer.start();
            } else {
                if (timer.isRunning()) {
                    timer.stop();
                }
                timer.setDelay(Alchemy.preferences.sessionRecordingInterval);
                timer.start();

            }
        //Alchemy.canvas.resetCanvasChanged();

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

    /** Return if currently recording
     * @return
     */
    boolean isRecording() {
        return recordState;
    }

    /** Set the session timer interval
     * @param interval  An interval in milliseconds
     */
    void setTimerInterval(int interval) {
        System.out.println("Interval: " + interval);
        // Set the interval in the preferences
        Alchemy.preferences.sessionRecordingInterval = interval;
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

    /** Restart the session timer */
    private void restartTimer() {
        if (timer != null) {
            if (timer.isRunning()) {
                System.out.println("Timer Restarted");
                timer.restart();
            }
        }
    }

    /** Restart the session */
    void restartSession() {
        pdfWriteFile = null;
    }
    //////////////////////////////////////////////////////////////
    // SAVE PDF 
    //////////////////////////////////////////////////////////////
    /** Return the current file being created by the pdf */
    File getCurrentPdfPath() {
        return pdfWriteFile;
    }

    /** Manually save a pdf page then restart the timer */
    void manualSavePage() {
        savePage();
        restartTimer();
        progressPage();
    }

    /** Manually save a pdf page, then clear, then restart the timer */
    void manualSaveClearPage() {
        saveClearPage();
        restartTimer();
    }

    /** Save a single pdf page to the current pdf being created */
    boolean savePage() {
        // If this is the first time or if the file is not actually there
        if (pdfWriteFile == null || !pdfWriteFile.exists()) {
            String fileName = Alchemy.preferences.sessionFilePreName + AlcUtil.dateStamp(Alchemy.preferences.sessionFileDateFormat) + ".pdf";
            pdfWriteFile = new File(Alchemy.preferences.sessionPath, fileName);
            System.out.println("Current PDF file: " + pdfWriteFile.getPath());
            return saveSinglePdf(pdfWriteFile);

        // Else save a temp file then join the two together
        } else {

            try {
                File temp = File.createTempFile("AlchemyPage", ".pdf");
                // Delete temp file when program exits.
                //temp.deleteOnExit();
                // Make the temp pdf
                saveSinglePdf(temp);
                boolean jointUp = addPageToPdf(pdfWriteFile, temp);
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
        Alchemy.canvas.clear();
        progressPage();
    }

    /** Save the canvas to a single paged PDF file
     * 
     * @param file  The file object to save the pdf to
     * @return      True if save worked, otherwise false
     */
    boolean saveSinglePdf(File file) {
        // Get the current 'real' size of the canvas without margins/borders
        java.awt.Rectangle bounds = Alchemy.canvas.getVisibleRect();
        //int singlePdfWidth = Alchemy.window.getWindowSize().width;
        //int singlePdfHeight = Alchemy.window.getWindowSize().height;
        com.lowagie.text.Document document = new com.lowagie.text.Document(new com.lowagie.text.Rectangle(bounds.width, bounds.height), 0, 0, 0, 0);
        System.out.println("Save Single Pdf Called: " + file.toString());
        boolean noError = true;

        try {

            PdfWriter singleWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.addTitle("Alchemy Session");
            document.addAuthor(USER_NAME);
            document.addCreator("Alchemy <http://al.chemy.org>");

            // Add metadata and open the document
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            XmpWriter xmp = new XmpWriter(os);
            PdfSchema pdf = new PdfSchema();
            pdf.setProperty(PdfSchema.KEYWORDS, "Alchemy <http://al.chemy.org>");
            //pdf.setProperty(PdfSchema.VERSION, "1.4");
            xmp.addRdfDescription(pdf);
            xmp.close();
            singleWriter.setXmpMetadata(os.toByteArray());

            // To avoid transparent colurs being converted from RGB>CMYK>RGB
            // We have to add everything to a transparency group
            PdfTransparencyGroup transGroup = new PdfTransparencyGroup();
            transGroup.put(PdfName.CS, PdfName.DEVICERGB);
            
            document.open();
            
            PdfContentByte cb = singleWriter.getDirectContent();            
            PdfTemplate tp = cb.createTemplate(bounds.width, bounds.height);
            
            document.newPage();
            
            cb.getPdfWriter().setGroup(transGroup);
            // Make sure the color space is Device RGB
            cb.setDefaultColorspace(PdfName.CS, PdfName.DEVICERGB);

            // Draw into the template and add it to the PDF 
            Graphics2D g2pdf = tp.createGraphics(bounds.width, bounds.height);
            Alchemy.canvas.setGuide(false);
            Alchemy.canvas.vectorCanvas.paintComponent(g2pdf);
            Alchemy.canvas.setGuide(true);
            g2pdf.dispose();
            cb.addTemplate(tp, 0, 0);


        } catch (DocumentException ex) {
            System.err.println(ex);
            noError = false;
        } catch (IOException ex) {
            System.err.println(ex);
            noError = false;
        }

        document.close();

        return noError;
    }

    /** Save the canvas to a single paged SVG file
     *
     * @param file  The file object to save the svg to
     * @return      True if save worked, otherwise false
     */
    boolean saveSVG(File file) {
        boolean noError = true;
        System.out.println("Save SVG Called: " + file.toString());

        // Get the current 'real' size of the canvas without margins/borders
        java.awt.Rectangle bounds = Alchemy.canvas.getVisibleRect();
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        org.w3c.dom.Document document = domImpl.createDocument(svgNS, "svg", null);
        //Set custom comment
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
        ctx.setComment("Generated by Alchemy (http://al.chemy.org) with Batik SVG Generator");

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, false);

        //set the canvas size
        svgGenerator.setSVGCanvasSize(new Dimension(bounds.width, bounds.height));
        // Ask vectorCanvas to render into the SVG Graphics2D implementation.
        Alchemy.canvas.setGuide(false);
        Alchemy.canvas.vectorCanvas.paintComponent(svgGenerator);
        Alchemy.canvas.setGuide(true);
        svgGenerator.dispose();
            
        boolean useCSS = true; // we want to use CSS style attributes

        //Write to the file
        FileWriter out;
        try {
            out = new FileWriter(file);
            svgGenerator.stream(out, useCSS);
        } catch (IOException ex) {
            System.err.println(ex);
            noError = false;
        } 

        return noError;
    }

    /** Adds a pdfReadPage to an existing pdf file
     * 
     * @param mainPdf   The main pdf with multiple pages.
     *                  Also used as the destination file.
     * @param tempPdf   The 'new' pdf with one pdfReadPage to be added to the main pdf
     * @return
     */
    boolean addPageToPdf(File mainPdf, File tempPdf) {
        try {
            // Destination file created in the temp dir then we will move it
            File dest = new File(DIR_TEMP, "Alchemy.pdf");
            OutputStream output = new FileOutputStream(dest);

            PdfReader reader = new PdfReader(mainPdf.getPath());
            PdfReader newPdf = new PdfReader(tempPdf.getPath());

            // See if the size of the canvas has increased
            // Size of the most recent temp PDF
            com.lowagie.text.Rectangle currentSize = newPdf.getPageSizeWithRotation(1);
            // Size of the session pdf at present
            com.lowagie.text.Rectangle oldSize = reader.getPageSizeWithRotation(1);
            // Sizes to be used from now on
            float pdfWidth = oldSize.getWidth();
            float pdfHeight = oldSize.getHeight();
            if (currentSize.getWidth() > pdfWidth) {
                pdfWidth = currentSize.getWidth();
            }
            if (currentSize.getHeight() > pdfHeight) {
                pdfHeight = currentSize.getHeight();
            }

            // Use the new bigger canvas size if required
            com.lowagie.text.Document document = new com.lowagie.text.Document(new com.lowagie.text.Rectangle(pdfWidth, pdfHeight), 0, 0, 0, 0);
            PdfCopy copy = new PdfCopy(document, output);

            // Copy the meta data
            document.addTitle("Alchemy Session");
            document.addAuthor(USER_NAME);
            document.addCreator("Alchemy <http://al.chemy.org>");
            copy.setXmpMetadata(reader.getMetadata());
            document.open();

            // Holds the PDF
            PdfContentByte cb = copy.getDirectContent();

            // Add each page from the main PDF
            for (int i = 0; i < reader.getNumberOfPages();) {
                ++i;
                document.newPage();
                cb.setDefaultColorspace(PdfName.CS, PdfName.DEVICERGB);
                PdfImportedPage page = copy.getImportedPage(reader, i);
                copy.addPage(page);
            }
            // Add the last (new) page
            document.newPage();
            PdfImportedPage lastPage = copy.getImportedPage(newPdf, 1);
            copy.addPage(lastPage);
            output.flush();
            document.close();
            output.close();

            if (dest.exists()) {
                // Save the location of the main pdf
                String mainPdfPath = mainPdf.getPath();
                // Delete the old file
                if (mainPdf.exists()) {
                    mainPdf.delete();
                }
                // The final joined up pdf file
                File joinPdf = new File(mainPdfPath);
                // Rename the file
                boolean success = dest.renameTo(joinPdf);
                if (!success) {
                    System.err.println("Error moving Pdf");
                    return false;
                }

            } else {
                System.err.println("File does not exist?!: " + dest.getAbsolutePath());
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //////////////////////////////////////////////////////////////
    // LOAD PDF
    //////////////////////////////////////////////////////////////
    /** Load a session file to draw on top of */
    boolean loadSessionFile(File file) {

        try {

            // Check this is a pdf file
            String mime = MimeUtil.getMimeType(file.getAbsoluteFile());
            if (!mime.equals("application/pdf")) {
                AlcUtil.showConfirmDialogFromBundle("notPDFDialogTitle", "notPDFDialogMessage");
                return false;
            }

            // First make sure we are not loading the current session file
            if (file.equals(pdfWriteFile)) {

                boolean result = AlcUtil.showConfirmDialogFromBundle("loadSessionPDFDialogTitle", "loadSessionPDFDialogMessage");

                if (result) {
                    restartSession();
                } else {
                    return false;
                }

            }

            // Secondly check the meta data to see if this is an Alchemy session
            PdfReader reader = new PdfReader(file.getPath());
            String metaData = new String(reader.getMetadata());

            // If the pdf is not an Alchemy pdf
            if (!metaData.contains("Alchemy") && !file.getName().startsWith("Alchemy")) {

                boolean result = AlcUtil.showConfirmDialogFromBundle("loadForeignPDFDialogTitle", "loadForeignPDFDialogMessage");
                if (!result) {
                    return false;
                }
            }

            // set up the PDF reading
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            pdfReadFile = new PDFFile(buf);
            currentPdfReadPage = 1;
            maxPdfReadPage = pdfReadFile.getNumPages();
            pdfReadPage = pdfReadFile.getPage(currentPdfReadPage);
            Alchemy.canvas.redraw(true);
            return true;


        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
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

    /** When a session pdf is loaded and linked to the current pdf
     *  this function is used to loop through the pages
     */
    void progressPage() {
        // Go to the next page if the session pdf is linked
        if (Alchemy.preferences.sessionLink && pdfReadFile != null) {
            if (currentPdfReadPage + 1 <= maxPdfReadPage) {
                currentPdfReadPage++;
                //System.out.println(currentPdfReadPage + " " + maxPdfReadPage);
                pdfReadPage = pdfReadFile.getPage(currentPdfReadPage);
                Alchemy.canvas.redraw(true);
            } else {
                currentPdfReadPage = 0;
                pdfReadPage = pdfReadFile.getPage(currentPdfReadPage);
                Alchemy.canvas.redraw(true);
            }
        }
    }

// Called by the timer
    public void actionPerformed(ActionEvent e) {
        // If the canvas has changed
        if (Alchemy.canvas.canvasChanged()) {

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
                            Alchemy.canvas.resetCanvasChanged();
                        }
                    });
                    indicatorTimer.start();
                }

                Alchemy.canvas.redraw();

                if (Alchemy.preferences.sessionAutoClear) {
                    Alchemy.canvas.clear();
                }
                progressPage();
                // This may not be needed, because of the 
                Alchemy.canvas.resetCanvasChanged();
            }
        }
    }
}
