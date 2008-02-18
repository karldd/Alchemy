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

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import javax.swing.*;
import java.awt.event.*;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Rectangle;

import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/** 
 * The Alchemy canvas
 * Stores all shapes created and handles all graphics related stuff
 * Think saving pdfs, printing, and of course displaying! 
 */
public class AlcCanvas extends JComponent implements AlcConstants, MouseMotionListener, MouseListener, Printable {

    /** Reference to the root **/
    private final AlcMain root;
    //////////////////////////////////////////////////////////////
    // GLOBAL SETTINGS
    ////////////////////////////////////////////////////////////// 
    /** Background colour */
    private Color bgColour = Color.WHITE;
    /** 'Redraw' on or off **/
    private boolean redraw = true;
    /** MouseEvents on or off - stop mouse events to the modules when inside the UI */
    private boolean mouseEvents = true;
    private boolean createMouseEvents = true;
    private boolean affectMouseEvents = true;
    /** Mouse down */
    private boolean mouseDown;
    /** Smoothing on or off */
    private boolean smoothing;
    /** Boolean used by the timer to determine if there has been canvas activity */
    private boolean canvasChanged = false;
    //////////////////////////////////////////////////////////////
    // GLOBAL SHAPE SETTINGS
    //////////////////////////////////////////////////////////////
    /** Colour of this shape */
    private Color colour = Color.BLACK;
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
    /** Graphics */
    private Graphics2D g2;
    /** Image to draw on the canvas */
    private Image image;
    /** Display the image or not */
    private boolean displayImage = true;
    /** Record indicator */
    private Ellipse2D.Double recordCircle;
    /** Record indicator on/off */
    boolean recordIndicator = false;

    /** Creates a new instance of AlcCanvas
     * @param root Reference to the root
     */
    public AlcCanvas(AlcMain root) {
        this.root = root;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        this.smoothing = root.prefs.getSmoothing();
        this.bgColour = new Color(root.prefs.getBgColour());

        addMouseListener(this);
        addMouseMotionListener(this);
        this.setBounds(0, 0, root.getWindowSize().width, root.getWindowSize().height);

        shapes = new ArrayList(100);
        shapes.ensureCapacity(100);
        createShapes = new ArrayList(25);
        createShapes.ensureCapacity(25);
        affectShapes = new ArrayList(25);
        affectShapes.ensureCapacity(25);
        guideShapes = new ArrayList(25);
        guideShapes.ensureCapacity(25);

    }

    /** Paint Component that draws all shapes to the canvas */
    public void paintComponent(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();

        g2 = (Graphics2D) g;
        if (smoothing) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        // Hints that don't seem to offer any extra performance on OSX
        //g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        //g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

        //... Paint background.
        g2.setColor(bgColour);
        g2.fillRect(0, 0, w, h);

        // Paint the buffImage is available
        if (displayImage) {
            if (image != null) {
                g2.drawImage(image, 0, 0, null);
            }
        }

        // Draw both lots of shapes
        ArrayList[] theShapes = {shapes, createShapes, affectShapes, guideShapes};

        for (int j = 0; j < theShapes.length; j++) {

            // Draw all the shapes
            if (theShapes[j] != null) {
                for (int i = 0; i < theShapes[j].size(); i++) {

                    AlcShape currentShape = (AlcShape) theShapes[j].get(i);

                    // LINE
                    if (currentShape.getStyle() == LINE) {

                        //g2.setStroke(new BasicStroke(currentShape.getLineWidth(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                        g2.setStroke(new BasicStroke(currentShape.getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                        g2.setColor(currentShape.getColour());
                        g2.draw(currentShape.getPath());

                    // SOLID
                    } else {

                        g2.setColor(currentShape.getColour());
                        g2.fill(currentShape.getPath());

                    }

                }
            }
        }

        if (recordIndicator) {
            recordCircle = new Ellipse2D.Double(5, h - 35, 7, 7);
            g2.setColor(Color.RED);
            g2.fill(recordCircle);
        }

    }

    //////////////////////////////////////////////////////////////
    // CANVAS FUNCTIONALITY
    //////////////////////////////////////////////////////////////
    /** Redraw the canvas */
    public void redraw() {
        applyAffects();
        if (redraw) {

            // Something has happened on the canvas and the user is still active
            canvasChanged = true;
            this.repaint();
        }
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

        // Pass this on to the currently selected modules
        // Does this need to be passed to all of the modules? Even if not selected?
        if (root.currentCreate >= 0) {
            root.creates[root.currentCreate].cleared();
        }
        if (root.hasCurrentAffects()) {
            for (int i = 0; i < root.currentAffects.length; i++) {
                if (root.currentAffects[i]) {
                    root.affects[i].cleared();
                }
            }
        }

        // Redraw to clear the screen even if redrawing is off
        if (redraw) {
            redraw();
        } else {
            redraw = true;
            redraw();
            redraw = false;
        }
    }

    /** Apply affects to the current shape and redraw the canvas */
    public void applyAffects() {
        if (root.hasCurrentAffects()) {
            for (int i = 0; i < root.currentAffects.length; i++) {
                if (root.currentAffects[i]) {
                    root.affects[i].affectShape();
                }
            }
        }
    }

    /** Commit all shapes to the main shapes array */
    public void commitShapes() {
        commitCreateShapes();
        commitAffectShapes();

        // Tell the modules the shapes have been commited
        if (root.currentCreate >= 0) {
            root.creates[root.currentCreate].commited();
        }
        if (root.hasCurrentAffects()) {
            for (int i = 0; i < root.currentAffects.length; i++) {
                if (root.currentAffects[i]) {
                    root.affects[i].commited();
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
    private void commitCreateShapes() {
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
    private void commitAffectShapes() {
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
    // CANVAS SETTINGS
    //////////////////////////////////////////////////////////////
    /** Set the canvas redraw state */
    public void setRedraw(boolean redraw) {
        this.redraw = redraw;
    }

    /** Get the canvas redraw state */
    public boolean getRedraw() {
        return redraw;
    }

    /** Return if the mouse is down - this does not take into account left/right buttons */
    public boolean getMouseDown() {
        return mouseDown;
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

    /** Return if there has been activity on the canvas since the last time the timer checked */
    boolean canvasChange() {
        return canvasChanged;
    }

    /** Reset the activity flag - called by the timer */
    void resetCanvasChange() {
        canvasChanged = false;
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
    }

    /** Clear the buffImage from the canvas */
    public void clearImage() {
        this.image = null;
    }

    /** Check if the image display is on
     * 
     * @return Image display on or off
     */
    public boolean isDisplayImage() {
        return displayImage;
    }

    /** Set image display to on or off
     * 
     * @param displayImage Image display on or off
     */
    public void setDisplayImage(boolean displayImage) {
        this.displayImage = displayImage;
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
    // PDF STUFF
    //////////////////////////////////////////////////////////////
    /** Save the canvas to a single paged PDF file
     * 
     * @param file  The file object to save the pdf to
     * @return      True if save worked, otherwise false
     */
    public boolean saveSinglePdf(File file) {
        int singlePdfWidth = root.getWindowSize().width;
        int singlePdfHeight = root.getWindowSize().height;
        Document singleDocument = new Document(new Rectangle(singlePdfWidth, singlePdfHeight), 0, 0, 0, 0);
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

            Graphics2D g2pdf = singleContent.createGraphics(singlePdfWidth, singlePdfHeight);
            this.paint(g2pdf);
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

    /** Adds a page to an existing pdf file
     * 
     * @param mainPdf   The main pdf with multiple pages.
     *                  Also used as the destination file.
     * @param tempPdf   The 'new' pdf with one page to be added to the main pdf
     * @return
     */
    public boolean addPageToPdf(File mainPdf, File tempPdf) {
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
    // SAVE BITMAP STUFF
    //////////////////////////////////////////////////////////////
    /** Save the canvas to a PNG file
     * 
     * @param file  The file object to save the PNG to
     * @return      True if save worked, otherwise false
     */
    public boolean savePng(File file) {
        try {
            //File file = new File("saveToThisFile.jpg");
            BufferedImage buffImage = generatedBufferedImage();
            ImageIO.write(buffImage, "png", file);
            return true;
        } catch (IOException ex) {
            System.err.println(ex);
            return false;
        }
    }

    /** Create a bufferedImage from the canvas */
    private BufferedImage generatedBufferedImage() {
        BufferedImage buffImage = new BufferedImage(this.getVisibleRect().width, this.getVisibleRect().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = buffImage.getGraphics();
        //g.fillRect(0, 0, buffImage.getWidth(), buffImage.getHeight());
        this.print(g);
        //System.out.println(this.getVisibleRect());
        g.dispose();
        return buffImage;
    }


    //////////////////////////////////////////////////////////////
    // MOUSE EVENTS
    //////////////////////////////////////////////////////////////
    public void mouseMoved(MouseEvent event) {
        // Toogle visibility of the Toolbar
        if (!root.prefs.getPaletteAttached()) {
            root.toolBar.toggleToolBar(event.getY());
        }
        passMouseEvent(event, "mouseMoved");
    }

    public void mousePressed(MouseEvent event) {
        mouseDown = true;
        // Turn off the toolbar on canvas click
        if (root.toolBar.toolBarTimer != null) {
            root.toolBar.setToolBarVisible(false);
        }
        passMouseEvent(event, "mousePressed");
    }

    public void mouseClicked(MouseEvent event) {
        passMouseEvent(event, "mouseClicked");
    }

    public void mouseEntered(MouseEvent event) {
        passMouseEvent(event, "mouseEntered");
    }

    public void mouseExited(MouseEvent event) {
        passMouseEvent(event, "mouseExited");
    }

    public void mouseReleased(MouseEvent event) {
        mouseDown = false;
        passMouseEvent(event, "mouseReleased");
    }

    public void mouseDragged(MouseEvent event) {
        passMouseEvent(event, "mouseDragged");
    }

    /** Calls a mouse event in each active module */
    private void passMouseEvent(MouseEvent event, String eventType) {
        // Reflection is used here to simplify passing events to each module

        try {
            if (mouseEvents) {
                // Pass to the current create module
                if (createMouseEvents) {
                    if (root.currentCreate >= 0) {
                        Method method = root.creates[root.currentCreate].getClass().getMethod(eventType, new Class[]{MouseEvent.class});
                        method.invoke(root.creates[root.currentCreate], new Object[]{event});
                    }
                }
                // Pass to all active affect modules
                if (affectMouseEvents) {
                    if (root.hasCurrentAffects()) {
                        for (int i = 0; i < root.currentAffects.length; i++) {
                            if (root.currentAffects[i]) {
                                Method method = root.affects[i].getClass().getMethod(eventType, new Class[]{MouseEvent.class});
                                method.invoke(root.affects[i], new Object[]{event});
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("passMouseEvent: " + eventType);
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
}
