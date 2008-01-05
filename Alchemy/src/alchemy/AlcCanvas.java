/**
 * AlcCanvas.java
 *
 * Created on November 16, 2007, 4:09 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
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

import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.imageio.ImageIO;

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
    /** Mouse down */
    private boolean mouseDown;
    /** Smoothing on or off */
    private boolean smoothing = true;
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
    private int lineWidth = 1;
    //////////////////////////////////////////////////////////////
    // DRAWING
    //////////////////////////////////////////////////////////////
    /** Array list containing shapes that have been archived */
    public ArrayList<AlcShape> shapes;
    /** Array list containing shapes made by create modules */
    public ArrayList<AlcShape> createShapes;
    /** Array list containing shapes made by affect modules */
    public ArrayList<AlcShape> affectShapes;
    /** Graphics */
    private Graphics2D g2;
    //////////////////////////////////////////////////////////////
    // PDF
    //////////////////////////////////////////////////////////////
    Document document;
    PdfWriter writer;
    PdfContentByte content;
    int pdfWidth, pdfHeight;

    /** Creates a new instance of AlcCanvas
     * @param root Reference to the root
     */
    public AlcCanvas(AlcMain root) {
        this.root = root;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        addMouseListener(this);
        addMouseMotionListener(this);
        this.setBounds(0, 0, root.getWindowSize().width, root.getWindowSize().height);

        shapes = new ArrayList<AlcShape>(100);
        shapes.ensureCapacity(100);
        createShapes = new ArrayList<AlcShape>(25);
        createShapes.ensureCapacity(25);
        affectShapes = new ArrayList<AlcShape>(25);
        affectShapes.ensureCapacity(25);

    }

    @Override
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
        g2.setPaint(bgColour);
        g2.fillRect(0, 0, w, h);


        // Draw both lots of shapes
        ArrayList[] theShapes = {shapes, createShapes, affectShapes};

        for (int j = 0; j < theShapes.length; j++) {

            // Draw all the shapes
            if (theShapes[j] != null) {
                for (int i = 0; i < theShapes[j].size(); i++) {

                    AlcShape currentShape = (AlcShape) theShapes[j].get(i);

                    // LINE
                    if (currentShape.getStyle() == LINE) {

                        g2.setStroke(new BasicStroke((float) currentShape.getLineWidth(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                        g2.setPaint(currentShape.getColour());
                        g2.draw(currentShape.getShape());

                    // SOLID
                    } else {

                        g2.setPaint(currentShape.getColour());
                        g2.fill(currentShape.getShape());

                    }

                }
            }
        }

    }

    //////////////////////////////////////////////////////////////
    // CANVAS FUNCTIONALITY
    //////////////////////////////////////////////////////////////
    /** Redraw the canvas */
    public void redraw() {
        if (redraw) {
            applyAffects();
            // Something has happened on the canvas and the user is still active
            canvasChanged = true;
            this.repaint();
        }
    }

    /** Turn on/off mouseEvents being sent to modules */
    public void setMouseEvents(boolean b) {
        mouseEvents = b;
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
                    root.affects[i].incrementShape();
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
            return shapes.get(shapes.size() - 1);
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
            return affectShapes.get(affectShapes.size() - 1);
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

    /** Function to control the display of the Ui toolbar */
    private void toggleToolBar(MouseEvent event) {
        int y = event.getY();
        if (y < 10) {
            if (!root.toolBar.getToolBarVisible()) {
                root.toolBar.setToolBarVisible(true);
                // Turn drawing off while in the toolbar
                mouseEvents = false;
            }
        } else if (y > root.toolBar.getTotalHeight()) {
            if (root.toolBar.getToolBarVisible()) {
                root.toolBar.setToolBarVisible(false);
                // Turn drawing on once out of the UI
                mouseEvents = true;
            }
        }
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

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
        setColour(this.colour);
    }

    public int getStyle() {
        return style;
    }

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

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }


    //////////////////////////////////////////////////////////////
    // PDF STUFF
    //////////////////////////////////////////////////////////////
    /** Start PDF record */
    public void startPdf(File file) {
        pdfWidth = root.getWindowSize().width;
        pdfHeight = root.getWindowSize().height;
        document = new Document(new Rectangle(pdfWidth, pdfHeight), 0, 0, 0, 0);
        document.addTitle("Alchemy");
        document.addAuthor(USER_NAME);
        //document.addSubject("This example explains how to add metadata.");
        //document.addKeywords("iText, Hello World, step 3, metadata");
        document.addCreator("al.chemy.org");

        //String path = "test.pdf";
        System.out.println("Start PDF Called: " + file.toString());

        try {

            writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            content = writer.getDirectContent();
            if (shapes.size() > 0) {
                // Drawing on the first frame 
                Graphics2D g2pdf = content.createGraphics(pdfWidth, pdfHeight);
                this.paint(g2pdf);
                g2pdf.dispose();
            }


        } catch (DocumentException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public boolean saveSinglePdf(File file) {
        int singlePdfWidth = root.getWindowSize().width;
        int singlePdfHeight = root.getWindowSize().height;
        Document singleDocument = new Document(new Rectangle(singlePdfWidth, singlePdfHeight), 0, 0, 0, 0);
        singleDocument.addTitle("Alchemy");
        singleDocument.addAuthor(USER_NAME);
        //document.addSubject("This example explains how to add metadata.");
        //document.addKeywords("iText, Hello World, step 3, metadata");
        singleDocument.addCreator("al.chemy.org");

        //String path = "test.pdf";
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

    /** Save frame of Pdf */
    public void savePdfPage() {
        System.out.println("Save PDF Page Called");
        try {
            document.newPage();
            Graphics2D g2pdf = content.createGraphics(pdfWidth, pdfHeight);
            this.paint(g2pdf);
            g2pdf.dispose();
        } catch (Exception event) {
            System.err.println(event);
        }

    }

    /** End PDF Record */
    public void endPdf() {
        System.out.println("End Pdf Called");
        if (document != null) {

            // Check if the document has pages
            if (root.session.getPageCount() > 0) {
                document.close();
                document = null;

            } else {

                // If the document has shapes but no pages, make a page and close it
                if (shapes.size() > 0) {
                    savePdfPage();
                    document.close();
                    document = null;

                // If there are no pages and no shapes then delete the empty file created
                } else {
                    File f = root.session.getCurrentPdfPath();
                    if (f.exists()) {
                        f.delete();
                    }
                    document = null;
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////
    // SAVE BITMAP STUFF
    //////////////////////////////////////////////////////////////
    public boolean savePng(File file) {
        try {
            //File file = new File("saveToThisFile.jpg");
            BufferedImage image = generatedBufferedImage();
            ImageIO.write(image, "png", file);
            return true;
        } catch (IOException ex) {
            System.err.println(ex);
            return false;
        }
    }

    public BufferedImage generatedBufferedImage() {
        BufferedImage image = new BufferedImage(this.getVisibleRect().width, this.getVisibleRect().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        //g.fillRect(0, 0, image.getWidth(), image.getHeight());
        this.print(g);
        //System.out.println(this.getVisibleRect());
        g.dispose();
        return image;
    }


    //////////////////////////////////////////////////////////////
    // MOUSE EVENTS
    //////////////////////////////////////////////////////////////
    public void mouseMoved(MouseEvent event) {
        // Toogle visibility of the Toolbar
        toggleToolBar(event);
        passMouseEvent(event, "mouseMoved");
    }

    public void mousePressed(MouseEvent event) {
        mouseDown = true;
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
        if (mouseEvents) {
            try {
                // Pass to the current create module
                if (root.currentCreate >= 0) {
                    Method method = root.creates[root.currentCreate].getClass().getMethod(eventType, new Class[]{MouseEvent.class});
                    method.invoke(root.creates[root.currentCreate], new Object[]{event});
                }
                // Pass to all active affect modules
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if (root.currentAffects[i]) {
                        Method method = root.affects[i].getClass().getMethod(eventType, new Class[]{MouseEvent.class});
                        method.invoke(root.affects[i], new Object[]{event});
                    }
                }
            } catch (Throwable e) {
                System.err.println("passMouseEvent: " + e + " " + eventType);
            }
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
            g2.scale(factor, factor);              // Adjust coordinate system
            pageWidth /=
                    factor;                   // Adjust page size up
            pageHeight /=
                    factor;
        }

        if (size.height > pageHeight) {   // Do the same thing for height
            double factor = pageHeight / size.height;
            g2.scale(factor, factor);
            pageWidth /=
                    factor;
            pageHeight /=
                    factor;
        }

        // Now we know the canvas will fit on the page.  Center it by translating as necessary.
        g2.translate((pageWidth - size.width) / 2, (pageHeight - size.height) / 2);

        // Draw a line around the outside of the drawing area
        g2.drawRect(-1, -1, size.width + 2, size.height + 2);

        // Set a clipping region so the canvas doesn't go out of bounds
        g2.setClip(0, 0, size.width, size.height);

        // Finally, print the component by calling the paintComponent() method.
        // Or, call paint() to paint the component, its background, border, and
        // children, including the Print JButton
        this.paintComponent(g);

        // Tell the PrinterJob that the page number was valid
        return Printable.PAGE_EXISTS;

    }
}
