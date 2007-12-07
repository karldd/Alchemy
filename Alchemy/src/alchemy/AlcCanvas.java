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
import javax.swing.*;
import java.awt.event.*;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Rectangle;


import java.util.ArrayList;

public class AlcCanvas extends JComponent implements AlcConstants, MouseMotionListener, MouseListener {

    /** Reference to the root **/
    private AlcMain root;
    //////////////////////////////////////////////////////////////
    // GLOBAL SETTINGS
    ////////////////////////////////////////////////////////////// 
    /** Background colour */
    private Color bgColour = Color.WHITE;
    /** 'Redraw' on or off **/
    private boolean redraw = true;
    /** Drawing on or off - stop mark making when inside the UI */
    private boolean draw = true;
    /** Smoothing on or off */
    private boolean smoothing = true;
    /** Automatic shape creation on or off */
    private boolean shapeCreation = true;
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
    /** Array list to contain the shapes created */
    private ArrayList<AlcShape> shapes;
    /** Temporary Shape */
    private AlcShape tempShape;
    /** Graphics */
    Graphics2D g2;
    //////////////////////////////////////////////////////////////
    // PDF
    //////////////////////////////////////////////////////////////
    Document document;
    PdfWriter writer;
    PdfContentByte content;
    int pdfWidth, pdfHeight;

    /** Creates a new instance of AlcCanvas */
    public AlcCanvas(AlcMain root) {
        this.root = root;

        addMouseListener(this);
        addMouseMotionListener(this);
        this.setBounds(0, 0, root.getWindowSize().width, root.getWindowSize().height);

        shapes = new ArrayList<AlcShape>(100);
        shapes.ensureCapacity(100);

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

        //... Paint background.
        g2.setPaint(bgColour);
        g2.fillRect(0, 0, w, h);


        // Draw all the shapes
        if (shapes != null) {
            for (int i = 0; i < shapes.size(); i++) {

                AlcShape currentShape = shapes.get(i);

                // LINE
                if (currentShape.getStyle() == LINE) {

                    float strokeWidth = (float) currentShape.getLineWidth();
                    g2.setStroke(new BasicStroke(strokeWidth));
                    g2.setPaint(currentShape.getColour());
                    g2.draw(currentShape.getShape());

                // SOLID
                } else {

                    g2.setPaint(currentShape.getColour());
                    g2.fill(currentShape.getShape());

                }

            }
        }



        // Draw the tempShape if present
        if (tempShape != null) {

            // LINE
            if (tempShape.getStyle() == LINE) {

                float strokeWidth = (float) tempShape.getLineWidth();
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.setPaint(tempShape.getColour());
                g2.draw(tempShape.getShape());

            // SOLID
            } else {

                g2.setPaint(tempShape.getColour());
                g2.fill(tempShape.getShape());

            }

        }

    }

    //////////////////////////////////////////////////////////////
    // CANVAS FUNCTIONALITY
    //////////////////////////////////////////////////////////////
    /** Redraw the canvas */
    public void redraw() {
        if (redraw) {
            this.repaint();
        }
    }

    /** Resize the canvas - called when the window is resized */
    public void resizeCanvas(Dimension windowSize) {
        this.setBounds(0, 0, windowSize.width, windowSize.height);
    }

    /** Clear the canvas */
    public void clear() {
        tempShape = null;
        shapes.clear();

        if (root.hasCurrentAffects()) {
            for (int i = 0; i < root.currentAffects.length; i++) {
                if (root.currentAffects[i]) {
                    root.affects[i].cleared();
                }
            }
        }

        redraw();
    }

    /** Apply affects to the current shape and redraw the canvas */
    public void applyAffects() {
        if (root.hasCurrentAffects()) {
            for (int i = 0; i < root.currentAffects.length; i++) {
                if (root.currentAffects[i]) {
                    root.affects[i].incrementShape(getCurrentShape());
                }
            }
        }
        redraw();
    }

    /** A temporary shape stored seperately.
     *  Used as a buffer before it is added to the shapes array.
     *  Stops the shapes from constantly adding to themselves while marks are being made.
     */
    public void addTempShape(AlcShape tempShape) {
        this.tempShape = tempShape;
    }

    /** Commit the temporary shape to the main shapes array */
    public void commitTempShape() {
        if (tempShape != null) {
            shapes.add(tempShape);
            tempShape = null;
        }
    }

    /** Returns the most recently added shape */
    public AlcShape getCurrentShape() {
        if (shapes.size() > 0) {
            return shapes.get(shapes.size() - 1);
        } else {
            return null;
        }
    }

    /** Returns the shape at index */
    public AlcShape getShape(int index) {
        // Check that the index is not out of bounds
        if (index < 0 || index > (shapes.size() - 1)) {
            return null;
        } else {
            return shapes.get(index);
        }
    }

    /** Returns the size of the shapes arraylist*/
    public int getShapesSize() {
        return shapes.size();
    }

    /** Adds a shape into the shape array */
    public void addShape(AlcShape newShape) {
        shapes.add(newShape);
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

    /** Set automatic shape creation - when on shapes are automatically draw and then passed to the affects */
    public void setShapeCreation(boolean b) {
        if (b) { // ON
            if (!shapeCreation) {
                shapeCreation = true;
            }

        } else { // OFF
            if (shapeCreation) {
                shapeCreation = false;
            }
        }
    }

    /** Get Shape Creation */
    public boolean getShapeCreation() {
        return shapeCreation;
    }

    /** Function to control the display of the Ui toolbar */
    private void toggleToolBar(MouseEvent e) {
        int y = e.getY();
        if (y < 10) {
            if (!root.toolBar.getToolBarVisible()) {
                root.toolBar.setToolBarVisible(true);
                // Turn drawing off while in the toolbar
                draw = false;
            }
        } else if (y > root.toolBar.getTotalHeight()) {
            if (root.toolBar.getToolBarVisible()) {
                root.toolBar.setToolBarVisible(false);
                // Turn drawing on once out of the UI
                draw = true;
            }
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
    public void startPdf() {
        pdfWidth = root.getWindowSize().width;
        pdfHeight = root.getWindowSize().height;
        document = new Document(new Rectangle(pdfWidth, pdfHeight), 0, 0, 0, 0);
        document.addTitle("al.chemy");
        document.addAuthor(System.getProperty("user.name"));
        //document.addSubject("This example explains how to add metadata.");
        //document.addKeywords("iText, Hello World, step 3, metadata");
        document.addCreator("al.chemy.org");
        System.out.println(System.getProperty("user.home"));
        System.out.println(System.getProperty("user.dir"));

        String path = "test.pdf";
        System.out.println("startPDF Called");

        try {

            writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            content = writer.getDirectContent();
            Graphics2D g2pdf = content.createGraphics(pdfWidth, pdfHeight);

            this.paint(g2pdf);
            g2pdf.dispose();

        } catch (DocumentException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /** Save frame of Pdf */
    public void savePdfFrame() {
        System.out.println("Save PDF Frame Called");
        try {
            document.newPage();
            Graphics2D g2pdf = content.createGraphics(pdfWidth, pdfHeight);
            this.paint(g2pdf);
            g2pdf.dispose();
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    /** End PDF Record */
    public void endPdf() {
        System.out.println("End Pdf Called");
        if (document != null) {
            document.close();
            document = null;
        }
    }

    //////////////////////////////////////////////////////////////
    // MOUSE EVENTS
    //////////////////////////////////////////////////////////////
    public void mouseMoved(MouseEvent e) {
        // Toogle visibility of the Toolbar
        toggleToolBar(e);

        if (draw) {
            if (root.currentCreate >= 0) {
                root.creates[root.currentCreate].mouseMoved(e);
            }

            if (root.hasCurrentAffects()) {
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if (root.currentAffects[i]) {
                        root.affects[i].mouseMoved(e);
                    }
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        if (draw) {
            if (root.currentCreate >= 0) {
                root.creates[root.currentCreate].mousePressed(e);
            }

            if (root.hasCurrentAffects()) {
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if (root.currentAffects[i]) {
                        root.affects[i].mousePressed(e);
                    }
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (draw) {
            if (root.currentCreate >= 0) {
                root.creates[root.currentCreate].mouseClicked(e);
            }

            if (root.hasCurrentAffects()) {
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if (root.currentAffects[i]) {
                        root.affects[i].mouseClicked(e);
                    }
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
        if (draw) {
            if (root.currentCreate >= 0) {
                root.creates[root.currentCreate].mouseEntered(e);
            }

            if (root.hasCurrentAffects()) {
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if (root.currentAffects[i]) {
                        root.affects[i].mouseEntered(e);
                    }
                }
            }
        }
    }

    public void mouseExited(MouseEvent e) {
        if (draw) {
            if (root.currentCreate >= 0) {
                root.creates[root.currentCreate].mouseExited(e);
            }

            if (root.hasCurrentAffects()) {
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if (root.currentAffects[i]) {
                        root.affects[i].mouseExited(e);
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (draw) {
            if (root.currentCreate >= 0) {
                root.creates[root.currentCreate].mouseReleased(e);
            }

            if (root.hasCurrentAffects()) {
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if (root.currentAffects[i]) {
                        root.affects[i].mouseReleased(e);
                    }
                }
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (draw) {
            if (root.currentCreate >= 0) {
                root.creates[root.currentCreate].mouseDragged(e);
            }

            if (root.hasCurrentAffects()) {
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if (root.currentAffects[i]) {
                        root.affects[i].mouseDragged(e);
                    }
                }
            }
        }

    }
}
