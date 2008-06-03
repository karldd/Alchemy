/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2008 Karl D.D. Willis
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
import java.awt.print.*;
import javax.swing.*;
import java.awt.event.*;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;

// ITEXT
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.*;
import com.lowagie.text.xml.xmp.*;

import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

// PDF READER
import com.sun.pdfview.*;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

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
    private Color bgColour;
    /** Old colour set when the colours are swapped */
    private Color oldColour;
    /** Swap state - true if the background is currently swapped in */
    private boolean backgroundActive = false;
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
    // SHAPES
    //////////////////////////////////////////////////////////////
    /** Array list containing shapes that have been archived */
    public ArrayList<AlcShape> shapes;
    /** Array list containing shapes made by create modules */
    public ArrayList<AlcShape> createShapes;
    /** Array list containing shapes made by affect modules */
    public ArrayList<AlcShape> affectShapes;
    /** Array list containing shapes used as visual guides - not actual geometry */
    public ArrayList<AlcShape> guideShapes;
    /** Full shape array of each array list */
    ArrayList[] fullShapeList = new ArrayList[3];
    /** Active shape list plus guides */
    ArrayList[] activeShapeList = new ArrayList[2];
    //////////////////////////////////////////////////////////////
    // IMAGE
    //////////////////////////////////////////////////////////////
    /** An image of the canvas drawn behind active shapes */
    private BufferedImage canvasImage;
    /** Image than can be drawn on the canvas */
    private BufferedImage image;
    /** Display the Image or not */
    private boolean imageDisplay = false;
    /** Position to display the image */
    private int imageX = 0;
    private int imageY = 0;
    //////////////////////////////////////////////////////////////
    // DISPLAY
    //////////////////////////////////////////////////////////////
    /** Record indicator on/off */
    private boolean recordIndicator = false;
    /** Draw guides */
    private boolean guides = true;
    /** Graphics Envrionment - updated everytime the volatile buffImage is refreshed */
    private GraphicsEnvironment ge;
    /** Graphics Configuration - updated everytime the volatile buffImage is refreshed */
    private GraphicsConfiguration gc;
    /** A Vector based canvas for full redrawing */
    private static VectorCanvas vectorCanvas;
    /** Previous cursor */
    Cursor oldCursor;

    /** Creates a new instance of AlcCanvas*/
    AlcCanvas() {



        this.smoothing = Alchemy.preferences.smoothing;
        this.bgColour = new Color(Alchemy.preferences.bgColour);
        this.colour = new Color(Alchemy.preferences.colour);

        addMouseListener(this);
        addMouseMotionListener(this);

////        Dimension windowSize = Alchemy.window.getWindowSize();
////        int x = 0;
////        if (Alchemy.preferences.simpleToolBar) {
////            x = 150;
////            windowSize.width -= 150;
////        }
////        this.setBounds(x, 0, windowSize.width, windowSize.height);

        shapes = new ArrayList<AlcShape>(100);
        shapes.ensureCapacity(100);
        createShapes = new ArrayList<AlcShape>(25);
        createShapes.ensureCapacity(25);
        affectShapes = new ArrayList<AlcShape>(25);
        affectShapes.ensureCapacity(25);
        guideShapes = new ArrayList<AlcShape>(25);
        guideShapes.ensureCapacity(25);

        fullShapeList[0] = shapes;
        fullShapeList[1] = createShapes;
        fullShapeList[2] = affectShapes;

        activeShapeList[0] = createShapes;
        activeShapeList[1] = affectShapes;


        vectorCanvas = new VectorCanvas();

        this.setCursor(CROSS);
    }

    /** Bitmap Canvas
     *  Draws all current shapes on top of the buffered image
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        java.awt.Rectangle visibleRect = this.getVisibleRect();
        int w = visibleRect.width;
        int h = visibleRect.height;

        if (imageDisplay && image != null) {
            g2.drawImage(image, imageX, imageY, null);
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

        // Draw the flattened buffImage
        if (canvasImage != null) {
            g2.drawImage(canvasImage, 0, 0, null);
        }
        if (redraw) {
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
        }

        // Draw a red circle when saving a frame
        if (recordIndicator) {
            Ellipse2D.Double recordCircle = new Ellipse2D.Double(5, h - 12, 7, 7);
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
                canvasImage = renderCanvas(true);
            }
            this.repaint();

            // Something has happened on the canvas and the user is still active
            canvasChanged = true;
        }
    }

    /** Force the canvas to redraw regardless of the current redraw setting */
    public void forceRedraw() {
        this.setRedraw(true);
        this.redraw(true);
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

    /** Set Smoothing (AntiAliasing) on or off */
    public void setSmoothing(boolean b) {
        this.smoothing = b;
        if (redraw) {
            this.redraw(true);
        // If redraw is off, just update the canvas image
        } else {
            canvasImage = renderCanvas(true);
        }
    }

    /** Get Antialiasing */
    public boolean getSmoothing() {
        return smoothing;
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
        // Allow for the left hand toolbar if in 'simple' mode
        int x = 0;
        if (Alchemy.preferences.simpleToolBar) {
            x = Alchemy.toolBar.toolBarWidth;
            windowSize.width -= Alchemy.toolBar.toolBarWidth;
        }
        this.setBounds(x, 0, windowSize.width, windowSize.height);
    }

    /** Clear the canvas */
    public void clear() {
        shapes.clear();
        createShapes.clear();
        affectShapes.clear();
        guideShapes.clear();

        this.canvasImage = null;

        if (redraw) {
            // If a session is loaded then make sure to redraw it below
            if (Alchemy.session.pdfReadPage == null) {
                this.redraw(false);
            } else {
                this.redraw(true);
            }
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

    /** Set the cursor */
    public void setTempCursor(Cursor cursor) {
        if (oldCursor == null) {
            oldCursor = this.getCursor();
            this.setCursor(cursor);
        }
    }

    /** Restore the cursor */
    public void restoreCursor() {
        if (oldCursor != null) {
            this.setCursor(oldCursor);
            oldCursor = null;
        }
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
        canvasImage = renderCanvas(false);

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
            return createShapes.get(createShapes.size() - 1);
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
        canvasImage = renderCanvas(false);
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
        canvasImage = renderCanvas(false);

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
    // SHAPE/COLOUR SETTINGS
    //////////////////////////////////////////////////////////////
    /** Get the current colour
     * @return      The current colour
     */
    public Color getColour() {
        return colour;
    }

    /** Set the current colour */
    public void setColour(Color colour) {
        // Control how the Foreground/Background button is updated
        if (backgroundActive) {
            bgColour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue());
            this.colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue());
            redraw(true);
        } else {
            this.colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
        }

        if (Alchemy.preferences.paletteAttached || Alchemy.preferences.simpleToolBar) {
            Alchemy.toolBar.refreshColourButton();
        } else {
            Alchemy.toolBar.queueColourButtonRefresh();
        }
    }

    /** Get the old colour */
    Color getOldColour() {
        return oldColour;
    }

    /** Set the old colour when backgroundActive state is true */
    void setOldColour(Color colour) {
        this.oldColour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
    }

    /** Toggle the colour between black and white */
    void toggleColour() {
        if (backgroundActive) {
            colour = new Color(oldColour.getRed(), oldColour.getGreen(), oldColour.getBlue(), alpha);
            backgroundActive = false;
        } else {
            oldColour = colour;
            colour = new Color(bgColour.getRGB());
            backgroundActive = true;
        }
    }

    /** Get the Background Colour */
    public Color getBgColour() {
        return bgColour;
    }

    /** Set the Background Colour */
    public void setBgColour(Color bgColour) {
        this.bgColour = bgColour;
        if (backgroundActive) {
            colour = new Color(bgColour.getRGB());
        }
    }

    /** Set the foreground colour
     *  This method sets the foreground colour 
     *  even if it is not currently active.
     *  E.g. The active colour is the background colour
     */
    public void setForegroundColour() {
        if (backgroundActive) {
            this.oldColour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
        } else {
            this.colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
        }
    }

    /** Get the current forground colour
     *  This method returns the foreground colour 
     *  even if it is not currently active.
     *  E.g. The active colour is the background colour
     * @return
     */
    public Color getForegroundColour() {
        if (backgroundActive) {
            return oldColour;
        } else {
            return colour;
        }
    }

    /** Get the current alpha value */
    public int getAlpha() {
        return alpha;
    }

    /** Set the current alpha value */
    void setAlpha(int alpha) {
        this.alpha = alpha;
        if (backgroundActive) {
            setOldColour(this.oldColour);
        } else {
            setColour(this.colour);
        }
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
    // DISPLAY
    //////////////////////////////////////////////////////////////
    boolean isGuideEnabled() {
        return guides;
    }

    void setGuide(boolean guides) {
        this.guides = guides;
    }

    public boolean isRecordIndicatorEnabled() {
        return recordIndicator;
    }

    public void setRecordIndicator(boolean recordIndicator) {
        this.recordIndicator = recordIndicator;
    }

    //////////////////////////////////////////////////////////////
    // IMAGE
    //////////////////////////////////////////////////////////////
    /** Set the Image to be drawn on the canvas
     * 
     * @param buffImage Image to be drawn
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        canvasImage = renderCanvas(true);
    }

    /** Get the current image
     * 
     * @return  The current image
     */
    public Image getImage() {
        return this.image;
    }

    /** Check if an mage is defined or not
     * 
     * @return Image display on or off
     */
    public boolean isImageSet() {
        return image == null ? false : true;
    }

    /** Set image display to on or off
     * 
     * @param displayFlatImage Image display on or off
     */
    public void setImageDisplay(boolean imageDisplay) {
        this.imageDisplay = imageDisplay;
        canvasImage = renderCanvas(true);
    }

    /** Check if image display is enabled */
    public boolean isImageDisplayEnabled() {
        return imageDisplay;
    }

    /** Set the location for the image to be displayed on the canvas
     * 
     * @param x
     * @param y
     */
    public void setImageLocation(int x, int y) {
        this.imageX = x;
        this.imageY = y;
    }

    /** Get the location where the image is displayed on the canvas
     * 
     * @return  Point - x & y location
     */
    public Point getImageLocation() {
        return new Point(imageX, imageY);
    }

    /** Reset the image location back to zero */
    public void resetImageLocation() {
        this.imageX = 0;
        this.imageY = 0;
    }

    /** Create an image from the canvas
     * 
     * @param vectorMode    In vector mode all shapes are rendered from scratch.
     *                      Otherwise the active shapes are rendered on top of the current canvas image
     * @return
     */
    BufferedImage renderCanvas(boolean vectorMode) {
        return renderCanvas(vectorMode, false);
    }

    /** Create an image from the canvas
     * 
     * @param vectorMode    In vector mode all shapes are rendered from scratch.
     *                      Otherwise the active shapes are rendered on top of the current canvas image
     * @param transparent   Ignore the background and create a transparent image with only shapes
     * @return
     */
    BufferedImage renderCanvas(boolean vectorMode, boolean transparent) {
        // Get the canvas size with out the frame/decorations
        java.awt.Rectangle visibleRect = this.getVisibleRect();
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage buffImage;
        if (transparent) {
            buffImage = gc.createCompatibleImage(visibleRect.width, visibleRect.height, Transparency.TRANSLUCENT);
        } else {
            buffImage = gc.createCompatibleImage(visibleRect.width, visibleRect.height);
        }
        // Paint the buffImage with the canvas
        Graphics2D g2 = buffImage.createGraphics();
        // Make sure the record indicator is off
        recordIndicator = false;

        if (transparent) {
            vectorCanvas.transparent = true;
            vectorCanvas.paintComponent(g2);
            vectorCanvas.transparent = false;
        } else {
            if (vectorMode) {
                vectorCanvas.paintComponent(g2);
            } else {
                this.paintComponent(g2);
            }
        }
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
        return savePng(file, false);
    }

    /** Save the canvas to a PNG file
     * 
     * @param file          The file object to save the PNG to
     * @param transparent   An image with transparency or not
     * @return              True if save worked, otherwise false
     */
    boolean savePng(File file, boolean transparent) {
        try {
            //File file = new File("saveToThisFile.jpg");
            setGuide(false);
            BufferedImage buffImage;
            if (transparent) {
                buffImage = renderCanvas(true, true);
            } else {
                buffImage = renderCanvas(true);
            }
            setGuide(true);
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
        java.awt.Rectangle visibleRect = this.getVisibleRect();
        //int singlePdfWidth = Alchemy.window.getWindowSize().width;
        //int singlePdfHeight = Alchemy.window.getWindowSize().height;
        Document singleDocument = new Document(new com.lowagie.text.Rectangle(visibleRect.width, visibleRect.height), 0, 0, 0, 0);
        System.out.println("Save Single Pdf Called: " + file.toString());

        try {

            PdfWriter singleWriter = PdfWriter.getInstance(singleDocument, new FileOutputStream(file));
            singleDocument.addTitle("Alchemy Session");
            singleDocument.addAuthor(USER_NAME);
            singleDocument.addCreator("Alchemy <http://al.chemy.org>");

            // Add metadata and open the document
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            XmpWriter xmp = new XmpWriter(os);
            PdfSchema pdf = new PdfSchema();
            pdf.setProperty(PdfSchema.KEYWORDS, "Alchemy <http://al.chemy.org>");
            pdf.setProperty(PdfSchema.VERSION, "1.4");
            xmp.addRdfDescription(pdf);
            xmp.close();
            singleWriter.setXmpMetadata(os.toByteArray());

            singleDocument.open();
            PdfContentByte singleContent = singleWriter.getDirectContent();

            Graphics2D g2pdf = singleContent.createGraphics(visibleRect.width, visibleRect.height);

            setGuide(false);
            vectorCanvas.paintComponent(g2pdf);
            setGuide(true);

            g2pdf.dispose();

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
            File dest = new File(TEMP_DIR, "Alchemy.pdf");
            OutputStream output = new FileOutputStream(dest);

            PdfReader reader = new PdfReader(mainPdf.getPath());
            PdfReader newPdf = new PdfReader(tempPdf.getPath());
            Document mainDocument = new Document(reader.getPageSizeWithRotation(1));
            PdfWriter mainWriter = PdfWriter.getInstance(mainDocument, output);

            // Copy the meta data
            mainDocument.addTitle("Alchemy Session");
            mainDocument.addAuthor(USER_NAME);
            mainDocument.addCreator("Alchemy <http://al.chemy.org>");
            mainWriter.setXmpMetadata(reader.getMetadata());
            mainDocument.open();

            // Holds the PDF
            PdfContentByte mainContent = mainWriter.getDirectContent();

            // Add each page from the main PDF
            for (int i = 0; i < reader.getNumberOfPages();) {
                ++i;
                mainDocument.newPage();
                PdfImportedPage page = mainWriter.getImportedPage(reader, i);
                mainContent.addTemplate(page, 0, 0);
            }
            // Add the last (new) page
            mainDocument.newPage();
            PdfImportedPage lastPage = mainWriter.getImportedPage(newPdf, 1);
            mainContent.addTemplate(lastPage, 0, 0);

            output.flush();
            mainDocument.close();
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
    // PRINT STUFF
    //////////////////////////////////////////////////////////////
    /**
     * This is the method defined by the Printable interface.  It prints the
     * canvas to the specified Graphics object, respecting the paper size
     * and margins specified by the PageFormat.  If the specified pdfReadPage number
     * is not pdfReadPage 0, it returns a code saying that printing is complete.  The
     * method must be prepared to be called multiple times per printing request
     * 
     * This code is from the book Java Examples in a Nutshell, 2nd Edition. Copyright (c) 2000 David Flanagan. 
     * 
     **/
    public int print(Graphics g, PageFormat format, int pageIndex) throws PrinterException {
        // We are only one pdfReadPage long; reject any other pdfReadPage numbers
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }

        // The Java 1.2 printing API passes us a Graphics object, but we
        // can always cast it to a Graphics2D object
        Graphics2D g2p = (Graphics2D) g;

        // Translate to accomodate the requested top and left margins.
        g2p.translate(format.getImageableX(), format.getImageableY());


        // Figure out how big the drawing is, and how big the pdfReadPage (excluding margins) is
        Dimension size = this.getSize();                  // Canvas size
        double pageWidth = format.getImageableWidth();    // Page width
        double pageHeight = format.getImageableHeight();  // Page height

        // If the canvas is too wide or tall for the pdfReadPage, scale it down
        if (size.width > pageWidth) {
            double factor = pageWidth / size.width;  // How much to scale
            System.out.println("Width Scale: " + factor);
            g2p.scale(factor, factor);              // Adjust coordinate system
            pageWidth /= factor;                   // Adjust pdfReadPage size up
            pageHeight /= factor;
        }

        if (size.height > pageHeight) {   // Do the same thing for height
            double factor = pageHeight / size.height;
            System.out.println("Height Scale: " + factor);
            g2p.scale(factor, factor);
            pageWidth /= factor;
            pageHeight /= factor;

        }

        // Now we know the canvas will fit on the pdfReadPage.  Center it by translating as necessary.
        g2p.translate((pageWidth - size.width) / 2, (pageHeight - size.height) / 2);

        // Draw a line around the outside of the drawing area
        //g2.drawRect(-1, -1, size.width + 2, size.height + 2);

        // Set a clipping region so the canvas doesn't go out of bounds
        g2p.setClip(0, 0, size.width, size.height);

        // Finally, print the component by calling the paintComponent() method.
        // Or, call paint() to paint the component, its background, border, and
        // children, including the Print JButton
        vectorCanvas.paintComponent(g);

        // Tell the PrinterJob that the pdfReadPage number was valid
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
        // Hide the toolbar when clicking on the canvas
        if (!Alchemy.preferences.paletteAttached && Alchemy.toolBar.isToolBarVisible() && !Alchemy.preferences.simpleToolBar && event.getY() >= Alchemy.toolBar.getTotalHeight()) {
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
 *  the background and buffImage if any.
 */
class VectorCanvas extends JPanel implements AlcConstants {

    boolean transparent = false;

    @Override
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

        // Do not draw the background when saving a transparent image
        if (!transparent) {
            // Paint background.
            g2.setColor(Alchemy.canvas.getBgColour());
            g2.fillRect(0, 0, w, h);
        }

        // PDF READER
        if (Alchemy.session.pdfReadPage != null) {

            // Remember the old transform settings
            AffineTransform at = g2.getTransform();

            int pageWidth = (int) Alchemy.session.pdfReadPage.getWidth();
            int pageHeight = (int) Alchemy.session.pdfReadPage.getHeight();
            PDFRenderer renderer = new PDFRenderer(Alchemy.session.pdfReadPage, g2, new Rectangle(0, 0, pageWidth, pageHeight), null, Alchemy.canvas.getBgColour());
            try {
                Alchemy.session.pdfReadPage.waitForFinish();
                renderer.run();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            // Revert to the old transform settings
            g2.setTransform(at);

        }

        // Draw Image
        if (Alchemy.canvas.isImageDisplayEnabled() && Alchemy.canvas.isImageSet()) {
            Point p = Alchemy.canvas.getImageLocation();
            g2.drawImage(Alchemy.canvas.getImage(), p.x, p.y, null);
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
        if (Alchemy.canvas.isGuideEnabled()) {
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
