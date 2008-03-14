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

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import javax.swing.*;
import java.awt.event.*;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;

// iText
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
//
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.print.Printable;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/** 
 * The Alchemy canvas
 * Stores all shapes created and handles all graphics related stuff
 * Think saving pdfs, printing, and of course displaying! 
 */
public class AlcCanvas extends JPanel implements AlcConstants, MouseMotionListener, MouseListener, Printable {

    //////////////////////////////////////////////////////////////
    // GLOBAL SETTINGS
    ////////////////////////////////////////////////////////////// 
    /** Background colour */
    Color bgColour;
    /** 'Redraw' on or off **/
    private boolean redraw = true;
    /** MouseEvents on or off - stop mouse events to the modules when inside the UI */
    private boolean mouseEvents = true;
    private boolean createMouseEvents = true;
    private boolean affectMouseEvents = true;
    /** Smoothing on or off */
    boolean smoothing;
    /** Boolean used by the timer to determine if there has been canvas activity */
    private boolean canvasChanged = false;
    //////////////////////////////////////////////////////////////
    // GLOBAL SHAPE SETTINGS
    //////////////////////////////////////////////////////////////
    /** Colour of this shape */
    private Color colour;
    /** Alpha of this shape */
    private int alpha = 255;
    /** Style of this shape - (1) LINE or (2) SOLID FILL */
    private int style = LINE;
    /** Line Weight if the style is line */
    private float lineWidth = 1F;
    //////////////////////////////////////////////////////////////
    // DRAWING
    //////////////////////////////////////////////////////////////
    /** Array list containing shapes that have been archived */
    public ArrayList shapes;
    /** Array list containing shapes made by create modules */
    public ArrayList createShapes;
    /** Array list containing shapes made by affect modules */
    public ArrayList affectShapes;
    /** Array list containing shapes used as visual guides - not actual geometry */
    public ArrayList guideShapes;
    /** Full shape array of each array list */
    ArrayList[] fullShapeList = new ArrayList[3];
    /** Active shape list plus guides */
    ArrayList[] activeShapeList = new ArrayList[2];
    //////////////////////////////////////////////////////////////
    // DISPLAY
    //////////////////////////////////////////////////////////////
    /** Flattened image drawn behind the canvas */
    Image flatImage;
    /** Image than can be drawn on the canvas */
    Image image;
    /** Display the Image or not */
    boolean displayImage = false;
    /** Image to draw on the canvas */
    Image bufferImage;
    /** Display the flatImage or not */
    boolean displayBufferImage = false;
    /** Record indicator on/off */
    boolean recordIndicator = false;

    //////////////////////////////////////////////////////////////
    // RENDERING
    //////////////////////////////////////////////////////////////
    /** Draw guides */
    boolean guides = true;
    /** Graphics Envrionment - updated everytime the volatile image is refreshed */
    GraphicsEnvironment ge;
    /** Graphics Configuration - updated everytime the volatile image is refreshed */
    GraphicsConfiguration gc;
    /** A Vector based canvas for full redrawing */
    static VectorCanvas vectorCanvas;
//  PDF READER
//  PDFFile pdffile;

    
    /** Creates a new instance of AlcCanvas*/
    AlcCanvas() {

        this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        this.smoothing = Alchemy.preferences.getSmoothing();
        this.bgColour = new Color(Alchemy.preferences.getBgColour());
        this.colour = new Color(Alchemy.preferences.getColour());

        addMouseListener(this);
        addMouseMotionListener(this);
        this.setBounds(0, 0, Alchemy.window.getWindowSize().width, Alchemy.window.getWindowSize().height);

        shapes = new ArrayList(100);
        shapes.ensureCapacity(100);
        createShapes = new ArrayList(25);
        createShapes.ensureCapacity(25);
        affectShapes = new ArrayList(25);
        affectShapes.ensureCapacity(25);
        guideShapes = new ArrayList(25);
        guideShapes.ensureCapacity(25);

        fullShapeList[0] = shapes;
        fullShapeList[1] = createShapes;
        fullShapeList[2] = affectShapes;

        activeShapeList[0] = createShapes;
        activeShapeList[1] = affectShapes;


        vectorCanvas = new VectorCanvas();


//        renderMode = VECTOR;
//        flatImage = getVolatileImage();
//        renderMode = BITMAP;


//       PDF READER
//        try {
//            File file = new File("/Users/karldd/Alchemy/Code/svnAlchemy/ok.pdf");
//
//            // set up the PDF reading
//            RandomAccessFile raf = new RandomAccessFile(file, "r");
//            FileChannel channel = raf.getChannel();
//            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
//            pdffile = new PDFFile(buf);
//
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    /** Paint Component that draws all shapes to the canvas */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = this.getWidth();
        int h = this.getHeight();

        if (displayImage && image != null) {
            g2.drawImage(image, 0, 0, null);
        } else {
            // Paint background.
            g2.setColor(bgColour);
            g2.fillRect(0, 0, w, h);
        }

        if (smoothing) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        // Draw the flattened image
        if (flatImage != null) {
//            do {
//                int valid = flatImage.validate(gc);
//                if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
//                    System.out.println("LOST");
//                    flatImage = getVolatileImage(true);
//                }
                g2.drawImage(flatImage, 0, 0, null);

//            } while (flatImage.contentsLost());
        }

        // Draw the create, affect, and guide lists
        for (int j = 0; j < activeShapeList.length; j++) {
            for (int i = 0; i < activeShapeList[j].size(); i++) {
                AlcShape currentShape = (AlcShape) activeShapeList[j].get(i);
                // LINE
                if (currentShape.style == LINE) {
                    //g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                    g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                    g2.setColor(currentShape.colour);
                    g2.draw(currentShape.path);
                // SOLID
                } else {
                    g2.setColor(currentShape.colour);
                    g2.fill(currentShape.path);
                }
            }
        }

        // Draw a red circle when saving a frame
        if (recordIndicator) {
            Ellipse2D.Double recordCircle = new Ellipse2D.Double(5, h - 35, 7, 7);
            g2.setColor(Color.RED);
            g2.fill(recordCircle);
        }


        // Draw the guides as required
        if (guides) {
            for (int i = 0; i < guideShapes.size(); i++) {
                AlcShape currentShape = (AlcShape) guideShapes.get(i);
                // LINE
                if (currentShape.style == LINE) {
                    //g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                    g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                    g2.setColor(currentShape.colour);
                    g2.draw(currentShape.path);
                // SOLID
                } else {
                    g2.setColor(currentShape.colour);
                    g2.fill(currentShape.path);
                }
            }
        }

        g2.dispose();

    // Hints that don't seem to offer any extra performance on OSX
    //g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    //g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);


//        PDF READER
//        get the first page
//        PDFPage page = pdffile.getPage(0);
//        PDFRenderer renderer = new PDFRenderer(page, g2, new Rectangle(0, 0, w, h), null, Color.RED);
//        try {
//            page.waitForFinish();
//            renderer.run();
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }

    // Paint the flattened image if available


    // Buffer flatImage drawn over everything else temporarily
//        if (displayBufferImage) {
//            if (bufferImage != null) {
//                g2.drawImage(bufferImage, 0, 0, null);
//            }
//        }



    }

    //////////////////////////////////////////////////////////////
    // CANVAS FUNCTIONALITY
    //////////////////////////////////////////////////////////////
    public void redraw() {
        redraw(false);
    }

    /** Redraw the canvas */
    public void redraw(boolean vector) {
        applyAffects();
        if (redraw) {
            if (vector) {
                flatImage = getVolatileImage(true);
            }
            this.repaint();

            // Something has happened on the canvas and the user is still active
            canvasChanged = true;
        }
    }

    /** Force the canvas to redraw regardless of the current redraw setting */
    public void forceRedraw() {
        this.setRedraw(true);
        this.redraw();
        this.setRedraw(false);
        canvasChanged = true;
    }

    /** Set the canvas redraw state */
    public void setRedraw(boolean redraw) {
        this.redraw = redraw;
    }

    /** Get the canvas redraw state */
    public boolean isRedraw() {
        return redraw;
    }

    /** Get the Background Colour */
    public Color getBgColour() {
        return bgColour;
    }

    /** Set the Background Colour */
    public void setBgColour(Color bgColour) {
        this.bgColour = bgColour;
    }

    /** Set Antialiasing */
    public void setSmoothing(boolean b) {
        if (b) { // ON
            if (!smoothing) {
                smoothing = true;
            }

        } else { // OFF
            if (smoothing) {
                smoothing = false;
            }
        }
    }

    /** Get Antialiasing */
    public boolean getSmoothing() {
        return smoothing;
    }

    public Dimension getCanvasSize() {
        return this.getSize();
    }

    /** Return if there has been activity on the canvas since the last time the timer checked */
    boolean canvasChange() {
        return canvasChanged;
    }

    /** Reset the activity flag - called by the timer */
    void resetCanvasChange() {
        canvasChanged = false;
    }

    /** Turn on/off mouseEvents being sent to modules */
    public void setMouseEvents(boolean b) {
        mouseEvents = b;
    }

    /** Turn on/off mouseEvents being sent to create modules */
    public void setCreateMouseEvents(boolean b) {
        createMouseEvents = b;
    }

    /** Turn on/off mouseEvents being sent to affect modules */
    public void setAffectMouseEvents(boolean b) {
        affectMouseEvents = b;
    }

    /** Resize the canvas - called when the window is resized */
    public void resizeCanvas(Dimension windowSize) {
        this.setBounds(0, 0, windowSize.width, windowSize.height);
    }

    /** Clear the canvas */
    public void clear() {
        shapes.clear();
        createShapes.clear();
        affectShapes.clear();
        guideShapes.clear();

        this.flatImage = null;

        if (redraw) {
            this.redraw();
        // Redraw to clear the screen even if redrawing is off
        } else {
            forceRedraw();
        }
        // Pass this on to the currently selected modules
        Alchemy.plugins.creates[Alchemy.plugins.currentCreate].cleared();

        if (Alchemy.plugins.hasCurrentAffects()) {
            for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                if (Alchemy.plugins.currentAffects[i]) {
                    Alchemy.plugins.affects[i].cleared();
                }
            }
        }
        // Now is a good time to clean up memory
        System.gc();
    }

    /** Apply affects to the current shape and redraw the canvas */
    private void applyAffects() {
        if (Alchemy.plugins.hasCurrentAffects()) {
            for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                if (Alchemy.plugins.currentAffects[i]) {
                    Alchemy.plugins.affects[i].affect();
                }
            }
        }
    }

    /** Commit all shapes to the main shapes array */
    public void commitShapes() {
        flatImage = getVolatileImage(false);

        for (int i = 0; i < createShapes.size(); i++) {
            shapes.add(createShapes.get(i));
        }
        createShapes.clear();
        for (int i = 0; i < affectShapes.size(); i++) {
            shapes.add(affectShapes.get(i));
        }
        affectShapes.clear();

        // Tell the modules the shapes have been commited
        if (Alchemy.plugins.currentCreate >= 0) {
            Alchemy.plugins.creates[Alchemy.plugins.currentCreate].commited();
        }
        if (Alchemy.plugins.hasCurrentAffects()) {
            for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                if (Alchemy.plugins.currentAffects[i]) {
                    Alchemy.plugins.affects[i].commited();
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////
    // SHAPES
    //////////////////////////////////////////////////////////////
    /** Returns the most recently added shape
     * @return The current shape
     */
    public AlcShape getCurrentShape() {
        if (shapes.size() > 0) {
            return (AlcShape) shapes.get(shapes.size() - 1);
        } else {
            return null;
        }
    }

    /** Sets the most recently added shape
     * @param shape Shape to become the current shape
     */
    public void setCurrentShape(AlcShape shape) {
        if (shapes.size() > 0) {
            shapes.set(shapes.size() - 1, shape);
        }
    }

    /** Removes the most recently added shape */
    public void removeCurrentShape() {
        if (shapes.size() > 0) {
            shapes.remove(shapes.size() - 1);
        }
    }

    //////////////////////////////////////////////////////////////
    // CREATE SHAPES
    //////////////////////////////////////////////////////////////
    /** Returns the most recently added create shape
     * @return The current create shape
     */
    public AlcShape getCurrentCreateShape() {
        if (createShapes.size() > 0) {
            return (AlcShape) createShapes.get(createShapes.size() - 1);
        } else {
            return null;
        }
    }

    /** Sets the most recently added create shape
     * @param shape     Shape to become the current create shape
     */
    public void setCurrentCreateShape(AlcShape shape) {
        if (createShapes.size() > 0) {
            createShapes.set(createShapes.size() - 1, shape);
        }
    }

    /** Removes the most recently added create shape */
    public void removeCurrentCreateShape() {
        if (createShapes.size() > 0) {
            createShapes.remove(createShapes.size() - 1);
        }
    }

    /** Commit all create shapes to the main shapes array */
    public void commitCreateShapes() {
        flatImage = getVolatileImage(false);
        for (int i = 0; i < createShapes.size(); i++) {
            shapes.add(createShapes.get(i));
        }
        createShapes.clear();
    }

    //////////////////////////////////////////////////////////////
    // AFFECT SHAPES
    //////////////////////////////////////////////////////////////
    /** Returns the most recently added affect shape
     * @return The current create shape
     */
    public AlcShape getCurrentAffectShape() {
        if (affectShapes.size() > 0) {
            return (AlcShape) affectShapes.get(affectShapes.size() - 1);
        } else {
            return null;
        }
    }

    /** Sets the most recently added affect shape
     * @param shape     Shape to become the current affect shape
     */
    public void setCurrentAffectShape(AlcShape shape) {
        if (affectShapes.size() > 0) {
            affectShapes.set(affectShapes.size() - 1, shape);
        }
    }

    /** Removes the most recently added affect shape */
    public void removeCurrentAffectShape() {
        if (affectShapes.size() > 0) {
            affectShapes.remove(affectShapes.size() - 1);
        }
    }

    /** Commit all affect shapes to the main shapes array */
    public void commitAffectShapes() {
        flatImage = getVolatileImage(false);

        for (int i = 0; i < affectShapes.size(); i++) {
            shapes.add(affectShapes.get(i));
        }
        affectShapes.clear();
    }

    //////////////////////////////////////////////////////////////
    // GUIDE SHAPES
    //////////////////////////////////////////////////////////////
    /** Returns the most recently added guide shape
     * @return The current guide shape
     */
    public AlcShape getCurrentGuideShape() {
        if (guideShapes.size() > 0) {
            return (AlcShape) guideShapes.get(guideShapes.size() - 1);
        } else {
            return null;
        }
    }

    /** Sets the most recently added guide shape
     * @param shape     Shape to become the current guide shape
     */
    public void setCurrentGuideShape(AlcShape shape) {
        if (guideShapes.size() > 0) {
            guideShapes.set(guideShapes.size() - 1, shape);
        }
    }

    /** Removes the most recently added guide shape */
    public void removeCurrentGuideShape() {
        if (guideShapes.size() > 0) {
            guideShapes.remove(guideShapes.size() - 1);
        }
    }

    //////////////////////////////////////////////////////////////
    // GLOBAL SHAPE SETTINGS
    //////////////////////////////////////////////////////////////
    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
    }

    /** Toggle the colour between black and white */
    public void toggleBlackWhite() {
        if (this.colour == Color.BLACK) {
            this.colour = Color.WHITE;
        } else {
            this.colour = Color.BLACK;
        }
    }

    /** Get the current alpha value */
    public int getAlpha() {
        return alpha;
    }

    /** Set the current alpha value */
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        setColour(this.colour);
    }

    /** Get the current style */
    public int getStyle() {
        return style;
    }

    /** Set the current style */
    public void setStyle(int style) {
        this.style = style;
    }

    /** Toggle the style between line and solid */
    public void toggleStyle() {
        if (style == LINE) {
            style = SOLID;
        } else {
            style = LINE;
        }
    }

    /** Get the current line width */
    public float getLineWidth() {
        return lineWidth;
    }

    /** Set the current line width */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    //////////////////////////////////////////////////////////////
    // IMAGE
    //////////////////////////////////////////////////////////////
    /** Set the Image to be drawn on the canvas
     * 
     * @param buffImage Image to be drawn
     */
    public void setImage(Image image) {
        this.image = image;
        flatImage = getVolatileImage(true);
    }

    /** Clear the buffImage from the canvas */
    public void clearImage() {
        this.image = null;
    }

    /** Check if the flatImage display is on
     * 
     * @return Image display on or off
     */
    public boolean isDisplayImage() {
        return displayImage;
    }

    /** Set flatImage display to on or off
     * 
     * @param displayFlatImage Image display on or off
     */
    public void setDisplayImage(boolean displayImage) {
        this.displayImage = displayImage;
        flatImage = getVolatileImage(true);
    }

    /** Set the buffer flatImage 
     *  A temporary snap shot of the canvas used instead of the canvas.
     *  This is used to prevent the area under the canvas redrawing
     *  when the redraw is set to off ie Blindness module
     * 
     */
    public void assignBufferImage() {
        this.bufferImage = getBufferedImage();
        this.displayBufferImage = false;
    }

    /** Set the buffer flatImage to be displayed or not */
    public void setDisplayBufferImage(boolean displayBufferImage) {
        this.displayBufferImage = displayBufferImage;
    }

    /** Create a VolatileImage from the canvas */
    Image getVolatileImage(boolean vectorMode) {
        // Get the canvas size with out the frame/decorations
        java.awt.Rectangle visibleRect = this.getVisibleRect();
//        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
//        Image volatileImage = gc.createCompatibleVolatileImage(visibleRect.width, visibleRect.height);
        BufferedImage volatileImage = new BufferedImage(visibleRect.width, visibleRect.height, BufferedImage.TYPE_INT_ARGB);
        // Check this image is valid
//        int valid = volatileImage.validate(gc);
//        if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
//            System.out.println("Volatile Image Incompatible");
//            volatileImage = this.getVolatileImage(true);
//        }
        // Paint the image with the canvas
        Graphics2D g2 = volatileImage.createGraphics();
        if (vectorMode) {
            vectorCanvas.paintComponent(g2);
        } else {
            this.paintComponent(g2);
        }
        g2.dispose();
        return volatileImage;
    }

    /** Create a BufferedImage from the canvas */
    BufferedImage getBufferedImage() {
        // Get the canvas size with out the frame/decorations
        java.awt.Rectangle visibleRect = this.getVisibleRect();
        BufferedImage buffImage = new BufferedImage(visibleRect.width, visibleRect.height, BufferedImage.TYPE_INT_ARGB);
        //BufferedImage buffImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffImage.createGraphics();
        //g.fillRect(0, 0, buffImage.getWidth(), buffImage.getHeight());
        //this.print(g);
        this.paintComponent(g2);
        //System.out.println(this.getVisibleRect());
        g2.dispose();
        return buffImage;
    }

    //////////////////////////////////////////////////////////////
    // SAVE PNG
    //////////////////////////////////////////////////////////////
    /** Save the canvas to a PNG file
     * 
     * @param file  The file object to save the PNG to
     * @return      True if save worked, otherwise false
     */
    boolean savePng(File file) {
        try {
            //File file = new File("saveToThisFile.jpg");
            BufferedImage buffImage = getBufferedImage();
            ImageIO.write(buffImage, "png", file);
            return true;
        } catch (IOException ex) {
            System.err.println(ex);
            return false;
        }
    }

    //////////////////////////////////////////////////////////////
    // PDF STUFF
    //////////////////////////////////////////////////////////////
    /** Save the canvas to a single paged PDF file
     * 
     * @param file  The file object to save the pdf to
     * @return      True if save worked, otherwise false
     */
    boolean saveSinglePdf(File file) {
        int singlePdfWidth = Alchemy.window.getWindowSize().width;
        int singlePdfHeight = Alchemy.window.getWindowSize().height;
        Document singleDocument = new Document(new com.lowagie.text.Rectangle(singlePdfWidth, singlePdfHeight), 0, 0, 0, 0);
        singleDocument.addTitle("Alchemy");
        singleDocument.addAuthor(USER_NAME);
        //document.addSubject("This example explains how to add metadata.");
        //document.addKeywords("iText, Hello World, step 3, metadata");
        singleDocument.addCreator("al.chemy.org");

        System.out.println("Save Single Pdf Called: " + file.toString());

        try {

            PdfWriter singleWriter = PdfWriter.getInstance(singleDocument, new FileOutputStream(file));
            singleDocument.open();
            PdfContentByte singleContent = singleWriter.getDirectContent();

            // Turn off the buffer flatImage if present
            boolean bufferOff = false;
            if (displayBufferImage) {
                displayBufferImage = false;
                bufferOff = true;
            }

            Graphics2D g2pdf = singleContent.createGraphics(singlePdfWidth, singlePdfHeight);
            this.paint(g2pdf);
            g2pdf.dispose();

            // Turn the buffer back on again
            if (bufferOff) {
                displayBufferImage = true;
            }

            singleDocument.close();
            return true;

        } catch (DocumentException ex) {
            System.err.println(ex);
            return false;
        } catch (IOException ex) {
            System.err.println(ex);
            return false;
        }
    }

    /** Adds a page to an existing pdf file
     * 
     * @param mainPdf   The main pdf with multiple pages.
     *                  Also used as the destination file.
     * @param tempPdf   The 'new' pdf with one page to be added to the main pdf
     * @return
     */
    boolean addPageToPdf(File mainPdf, File tempPdf) {
        try {
            // Destination file created in the temp dir then we will move it
            File dest = new File(TEMP_DIR, "Alchemy.pdf");

            PdfReader reader = new PdfReader(mainPdf.getPath());
            PdfReader newPdf = new PdfReader(tempPdf.getPath());
            int n = reader.getNumberOfPages();

            //reader.consolidateNamedDestinations();

            Document mainDocument = new Document(reader.getPageSizeWithRotation(1));
            PdfCopy copy = new PdfCopy(mainDocument, new FileOutputStream(dest));
            mainDocument.open();

            for (int i = 0; i < n;) {
                ++i;
                PdfImportedPage page = copy.getImportedPage(reader, i);
                copy.addPage(page);
            }
            // Add the last (new) page
            PdfImportedPage lastPage = copy.getImportedPage(newPdf, 1);
            copy.addPage(lastPage);

            mainDocument.close();

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
    // PRINT STUFF
    //////////////////////////////////////////////////////////////
    /**
     * This is the method defined by the Printable interface.  It prints the
     * canvas to the specified Graphics object, respecting the paper size
     * and margins specified by the PageFormat.  If the specified page number
     * is not page 0, it returns a code saying that printing is complete.  The
     * method must be prepared to be called multiple times per printing request
     * 
     * This code is from the book Java Examples in a Nutshell, 2nd Edition. Copyright (c) 2000 David Flanagan. 
     * 
     **/
    public int print(Graphics g, PageFormat format, int pageIndex) throws PrinterException {
        // We are only one page long; reject any other page numbers
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }

        // The Java 1.2 printing API passes us a Graphics object, but we
        // can always cast it to a Graphics2D object
        Graphics2D g2p = (Graphics2D) g;

        // Translate to accomodate the requested top and left margins.
        g2p.translate(format.getImageableX(), format.getImageableY());


        // Figure out how big the drawing is, and how big the page (excluding margins) is
        Dimension size = this.getSize();                  // Canvas size
        double pageWidth = format.getImageableWidth();    // Page width
        double pageHeight = format.getImageableHeight();  // Page height

        // If the canvas is too wide or tall for the page, scale it down
        if (size.width > pageWidth) {
            double factor = pageWidth / size.width;  // How much to scale
            System.out.println("Width Scale: " + factor);
            g2p.scale(factor, factor);              // Adjust coordinate system
            pageWidth /= factor;                   // Adjust page size up
            pageHeight /= factor;
        }

        if (size.height > pageHeight) {   // Do the same thing for height
            double factor = pageHeight / size.height;
            System.out.println("Height Scale: " + factor);
            g2p.scale(factor, factor);
            pageWidth /= factor;
            pageHeight /= factor;

        }

        // Now we know the canvas will fit on the page.  Center it by translating as necessary.
        g2p.translate((pageWidth - size.width) / 2, (pageHeight - size.height) / 2);

        // Draw a line around the outside of the drawing area
        //g2.drawRect(-1, -1, size.width + 2, size.height + 2);

        // Set a clipping region so the canvas doesn't go out of bounds
        g2p.setClip(0, 0, size.width, size.height);

        // Finally, print the component by calling the paintComponent() method.
        // Or, call paint() to paint the component, its background, border, and
        // children, including the Print JButton
        this.paintComponent(g);

        // Tell the PrinterJob that the page number was valid
        return Printable.PAGE_EXISTS;

    }

    //////////////////////////////////////////////////////////////
    // MOUSE EVENTS
    //////////////////////////////////////////////////////////////
    public void mouseMoved(MouseEvent event) {
        if (!Alchemy.preferences.paletteAttached) {
            Alchemy.toolBar.toggleToolBar(event.getY());
        }
        if (mouseEvents) {
            // Pass to the current create module
            if (createMouseEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseMoved(event);
            }
            // Pass to all active affect modules
            if (affectMouseEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mouseMoved(event);
                        }
                    }
                }
            }
        }
    }

    public void mousePressed(MouseEvent event) {
        // Turn off the toolbar on canvas click
        if (Alchemy.toolBar.toolBarTimer != null) {
            Alchemy.toolBar.setToolBarVisible(false);
        }

        if (mouseEvents) {
            // Pass to the current create module
            if (createMouseEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mousePressed(event);
            }
            // Pass to all active affect modules
            if (affectMouseEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mousePressed(event);
                        }
                    }
                }
            }
        }
    }

    public void mouseClicked(MouseEvent event) {
        if (mouseEvents) {
            // Pass to the current create module
            if (createMouseEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseClicked(event);
            }
            // Pass to all active affect modules
            if (affectMouseEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mouseClicked(event);
                        }
                    }
                }
            }
        }
    }

    public void mouseEntered(MouseEvent event) {
        if (mouseEvents) {
            // Pass to the current create module
            if (createMouseEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseEntered(event);
            }
            // Pass to all active affect modules
            if (affectMouseEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mouseEntered(event);
                        }
                    }
                }
            }
        }
    }

    public void mouseExited(MouseEvent event) {
        if (mouseEvents) {
            // Pass to the current create module
            if (createMouseEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseExited(event);
            }
            // Pass to all active affect modules
            if (affectMouseEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mouseExited(event);
                        }
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent event) {
        if (mouseEvents) {
            // Pass to the current create module
            if (createMouseEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseReleased(event);
            }
            // Pass to all active affect modules
            if (affectMouseEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mouseReleased(event);
                        }
                    }
                }
            }
        }
    }

    public void mouseDragged(MouseEvent event) {
        if (mouseEvents) {
            // Pass to the current create module
            if (createMouseEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseDragged(event);
            }
            // Pass to all active affect modules
            if (affectMouseEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mouseDragged(event);
                        }
                    }
                }
            }
        }
    }
}

/** Vector Canvas
 *  Draws the canvas is full, including all shapes,
 *  the background and image if any.
 */
class VectorCanvas extends JPanel implements AlcConstants {

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        int w = Alchemy.canvas.getWidth();
        int h = Alchemy.canvas.getHeight();

        Graphics2D g2 = (Graphics2D) g;

        if (Alchemy.canvas.smoothing) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        // Paint background.
        g2.setColor(Alchemy.canvas.bgColour);
        g2.fillRect(0, 0, w, h);

        // Draw image
        if (Alchemy.canvas.displayImage && Alchemy.canvas.image != null) {
            g2.drawImage(Alchemy.canvas.image, 0, 0, null);
        }

        // Draw the create, affect, and shapes lists
        for (int j = 0; j < Alchemy.canvas.fullShapeList.length; j++) {
            for (int i = 0; i < Alchemy.canvas.fullShapeList[j].size(); i++) {
                AlcShape currentShape = (AlcShape) Alchemy.canvas.fullShapeList[j].get(i);
                // LINE
                if (currentShape.style == LINE) {
                    //g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                    g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                    g2.setColor(currentShape.colour);
                    g2.draw(currentShape.path);
                // SOLID
                } else {
                    g2.setColor(currentShape.colour);
                    g2.fill(currentShape.path);
                }
            }
        }
        if (Alchemy.canvas.guides) {
            for (int i = 0; i < Alchemy.canvas.guideShapes.size(); i++) {
                AlcShape currentShape = (AlcShape) Alchemy.canvas.guideShapes.get(i);
                // LINE
                if (currentShape.style == LINE) {
                    //g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                    g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                    g2.setColor(currentShape.colour);
                    g2.draw(currentShape.path);
                // SOLID
                } else {
                    g2.setColor(currentShape.colour);
                    g2.fill(currentShape.path);
                }
            }
        }

        g2.dispose();
    }
}
