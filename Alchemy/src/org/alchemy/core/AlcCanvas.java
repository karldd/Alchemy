/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
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

// ITEXT
import com.lowagie.text.pdf.*;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.*;

// PDF READER
import com.sun.pdfview.*;

// JPEN
import jpen.*;
import jpen.event.PenListener;

/** 
 * The Alchemy canvas <br>
 * Stores all shapes created and handles all graphics related stuff<br>
 * Think saving pdfs, printing, and of course displaying! 
 */
public class AlcCanvas extends JPanel implements AlcConstants, MouseListener, MouseMotionListener, PenListener, Printable {
    //////////////////////////////////////////////////////////////
    // GLOBAL SHAPE SETTINGS
    //////////////////////////////////////////////////////////////
//  /** Global Shape Foreground color */
//    private Color color;
    /** Global Shape Foreground Alpha */
    private int alpha = 255;
    private boolean alphaLocked = false;
    
    // The current color is stored here.  An array is used so that
    // mulitple "states" can be stored (Stylus/Eraser, etc)
    private ArrayList<Color> currentColorSet;
    private int[] currentAlphaSet = {255,255};
    private int currentColorIndex;
    
    //  This is not currently used, but may be implemented for
    //  recent color switching a la mypaint.
    public Color previousColor;
    
    // 0-Disabled, 1-Single, 2-Unlimited
    private int undoDepth;
    // remember the size of the shapes array
    private int lastShapesSize;
    
    // Swatch stored here
    public ArrayList<Color> swatch;
    public int activeSwatchIndex;
    
    /** Global Shape Style - (1) LINE or (2) SOLID FILL */
    private int style = STYLE_STROKE;
    /** Globl Shape Line Weight (if the style is line) */
    private float lineWidth = 1F;
    //////////////////////////////////////////////////////////////
    // GLOBAL SETTINGS
    ////////////////////////////////////////////////////////////// 
    /** Background color */
    private Color bgColor;
    /** 'Redraw' on or off **/
    private boolean redraw = true;
    /** Smoothing on or off */
    boolean smoothing;
    /** Boolean used by the timer to determine if there has been canvas activity */
    private boolean canvasChanged = false;
    /** Draw under the other shapes on the canvas */
    private boolean drawUnder = false;
    /** Boolean used to indicate if the user is picking a zoom location with the mouse */
    private boolean zoomMousing = false;
    /** Zoom data */
    private double zoomAmount = 4.0;
    private double currentZoom = 1/zoomAmount;
    private double lastZoomX = 0.0;
    private double lastZoomY = 0.0;
    
    //////////////////////////////////////////////////////////////
    // PEN SETTINGS
    //////////////////////////////////////////////////////////////
    /** Events on or off - stop mouse/pen events to the modules when inside the UI */
    private boolean events = true;
    private boolean createEvents = true;
    private boolean affectEvents = true;
    /** The Pen manager used by JPen*/
    private PenManager pm;
    /** Pen down or up */
    private boolean penDown = false;
    /** The type of pen - PEN_STYLUS / PEN_ERASER / PEN_CURSOR */
    private int penType = PEN_CURSOR;
    /** Pen Pressure if available */
    private float penPressure = 0F;
    /** Pen Tilt if available */
    private Point2D.Float penTilt = new Point2D.Float();
    /** Pen Location - if a pen is available this will be a float otherwise int */
    private Point2D.Float penLocation = new Point2D.Float();
    /** Pen location has changed or not */
    private boolean penLocationChanged = true;
    //////////////////////////////////////////////////////////////
    // SHAPES
    //////////////////////////////////////////////////////////////
    /** Array list containing shapes that have been commited.
     *  Shapes in this list are generally rendered to the image buffer
     *  to improve performance */
    public ArrayList<AlcShape> shapes;
    /** Array list containing shapes currently in use by create modules */
    public ArrayList<AlcShape> createShapes;
    /** Array list containing shapes currently in use by affect modules */
    public ArrayList<AlcShape> affectShapes;
    /** Array list containing shapes used as visual guides - not actual geometry */
    public ArrayList<AlcShape> guideShapes;
    /** Keeps track of the shape number of the first shape in a group for undo
        new numbers are entered on mouse presses. Correspondes to "shapes" array */
    public ArrayList<Integer> shapeGroups;
    public ArrayList<Integer> shapeGroupsSize;
    /** Full shape array of each array list */
    ArrayList[] fullShapeList = new ArrayList[3];
    /** Active shape list plus guides */
    ArrayList[] activeShapeList = new ArrayList[2];
    //////////////////////////////////////////////////////////////
    // IMAGE
    //////////////////////////////////////////////////////////////
    /** An image of the canvas drawn behind active shapes */
    private Image canvasImage;
    /** Image than can be drawn on the canvas */
    private BufferedImage image;
    private BufferedImage zoomedImage;
    /** Display the Image or not */
    private boolean imageDisplay = false;
    /** Position to display the image */
    private Point imageLocation = new Point(0, 0);
     /** Position to display the zoomed image */
    private Point zoomedImageLocation = new Point(0, 0);
    /** An image used to fake transparency in fullscreen mode */
    private Image transparentImage;
    //////////////////////////////////////////////////////////////
    // DISPLAY
    //////////////////////////////////////////////////////////////
    /** Record indicator on/off */
    private boolean recordIndicator = false;
    /** Draw guides */
    private boolean guides = true;
    /** Graphics Envrionment - updated everytime the volatile buffImage is refreshed */
    private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    /** Graphics Configuration - updated everytime the volatile buffImage is refreshed */
    private GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    /** A Vector based canvas for full redrawing */
    VectorCanvas vectorCanvas;
    /** Previous cursor */
    Cursor oldCursor;
    /** Automatic toggling of the toolbar */
    private boolean autoToggleToolBar;
    
    /** Creates a new instance of AlcCanvas*/
    AlcCanvas() {
        currentColorSet = new ArrayList<Color>(2);
        currentColorSet.add(Color.WHITE);
        currentColorSet.add(Color.WHITE);
        currentColorIndex = 0;
        
        this.smoothing = Alchemy.preferences.smoothing;
        this.bgColor = new Color(Alchemy.preferences.bgColor);
        currentColorSet.set(currentColorIndex, new Color(Alchemy.preferences.color));

        this.autoToggleToolBar = !Alchemy.preferences.paletteAttached;
        this.undoDepth = Alchemy.preferences.undoDepth;
        
        /** Holds saved swatch colors */
        swatch = new ArrayList<Color>();
        /** Keeps track of which swatch color is active */
        activeSwatchIndex = -1;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

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
        
        /** Keeps track of which shapes in "shapes" array were laid down
         *  in a single mouse/pen click                                   */
        shapeGroups = new ArrayList<Integer>();
        shapeGroupsSize = new ArrayList<Integer>();
        
        activeShapeList[0] = createShapes;
        activeShapeList[1] = affectShapes;

        vectorCanvas = new VectorCanvas();

        pm = new PenManager(this);
        pm.pen.addListener(this);
        pm.pen.setFrequencyLater(200);

        this.setCursor(CURSOR_CROSS);
    }

    /** Bitmap Canvas
     *  Draws all current shapes on top of the buffered image
     * @param g Graphics Object to draw on
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        java.awt.Rectangle visibleRect = this.getVisibleRect();
        int w = visibleRect.width;
        int h = visibleRect.height;

        // Draw the 'fake' Transparent Image
        if (Alchemy.window.isTransparent() && transparentImage != null) {
            g2.drawImage(transparentImage, 0, 0, null);

            // Draw the image if present
            if (imageDisplay && image != null) {
                Point p = getImageLocation();
               g2.drawImage(getImage(), (int)p.getX(), (int)p.getY(), null);
            }
        } else {
            // Draw the image if present
            if (imageDisplay && image != null) {
                Point p = getImageLocation();
                g2.drawImage(getImage(), (int)p.getX(), (int)p.getY(), null);
            }
            // Paint background.
            g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue()));
            g2.fillRect(0, 0, w, h);
        }


        if (smoothing) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        // Draw the flattened buffImage
        if (canvasImage != null && !drawUnder) {
            g2.drawImage(canvasImage, 0, 0, null);
        }
        if (redraw) {
            // Draw the create, affect, and guide lists
            for (int j = 0; j < activeShapeList.length; j++) {
                for (int i = 0; i < activeShapeList[j].size(); i++) {
                    AlcShape currentShape = (AlcShape) activeShapeList[j].get(i);
                    // LINE
                    if (currentShape.style == STYLE_STROKE) {
                        //g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                        g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                        g2.setPaint(currentShape.getPaint());
                        g2.draw(currentShape.path);
                    // SOLID
                    } else {
                        g2.setPaint(currentShape.getPaint());
                        g2.fill(currentShape.path);
                    }
                }
            }
        }

        // Draw the image on top of the current shapes
        if (drawUnder) {
            g2.drawImage(canvasImage, 0, 0, null);
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
                AlcShape currentShape = guideShapes.get(i);
                // LINE
                if (currentShape.style == STYLE_STROKE) {
                    //g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                    g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                    g2.setColor(currentShape.color);
                    g2.draw(currentShape.path);
                // SOLID
                } else {
                    g2.setColor(currentShape.color);
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
    /** Redraw the canvas */
    public void redraw() {
        redraw(false);
    }

    /** Redraw the canvas
     *  @param fullRedraw   Specify if the full set of vector shapes should be redrawn
     *                  or just add the new shape to the existing buffer image
     */
    public void redraw(boolean fullRedraw) {
        applyAffects();
        if (redraw) {
            if (fullRedraw) {
                // If the window is transparent
                if (Alchemy.window.isTransparent()) {
                    canvasImage = renderCanvas(true, true);
                } else {
                    canvasImage = renderCanvas(true);
                }
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
    }

    /** Set the canvas redraw state
     * @param redraw    Redraw state 
     */
    public void setRedraw(boolean redraw) {
        this.redraw = redraw;
    }

    /** Get the canvas redraw state
     * @return  Redraw state on or off
     */
    public boolean isRedraw() {
        return redraw;
    }

    /** Set the draw under state to draw under existing shapes 
     * @param drawUnder
     */
    void setDrawUnder(boolean drawUnder) {
        this.drawUnder = drawUnder;
        updateCanvasImage(true);
    }

    /** Get the draw under state
     * @return 
     */
    boolean getDrawUnder() {
        return this.drawUnder;
    }

    /** Update the canvasImage with transparency if required 
     * 
     * @param transparency
     */
    void updateCanvasImage(boolean transparency) {
        canvasImage = renderCanvas(true, true);
    }

    /** Set Smoothing (AntiAliasing) on or off
     * @param smoothing     Smoothing on or off
     */
    void setSmoothing(boolean smoothing) {
        this.smoothing = smoothing;
        if (redraw) {
            this.redraw(true);
        // If redraw is off, just update the canvas image
        } else {
            canvasImage = renderCanvas(true);
        }
    }

    /** Get Antialiasing
     * @return 
     */
    boolean isSmoothing() {
        return this.smoothing;
    }

    /** Return if there has been activity on the canvas since the last time the timer checked */
    boolean canvasChanged() {
        return this.canvasChanged;
    }

    /** Reset the activity flag - called by the timer */
    void resetCanvasChanged() {
        this.canvasChanged = false;
    }
    //////////////////////////////////////////////////////////////
    // PEN EVENTS
    //////////////////////////////////////////////////////////////
    /** Turn on/off mouse/pen events being sent to modules
     * @param events 
     */
    public void setEvents(boolean events) {
        this.events = events;
    }

    /** Turn on/off mouse/pen events  being sent to create modules
     * @param createEvents 
     */
    public void setCreateEvents(boolean createEvents) {
        this.createEvents = createEvents;
    }

    /** Turn on/off mouse/pen events  being sent to affect modules
     * @param affectEvents 
     */
    public void setAffectEvents(boolean affectEvents) {
        this.affectEvents = affectEvents;
    }

    /** Pen (or mouse) down or up
     * @return  The state of the pen or mouse
     */
    public boolean isPenDown() {
        return penDown;
    }

    /** Pen (or mouse) down or up
     * @return  The state of the pen or mouse
     */
    public boolean isMouseDown() {
        return penDown;
    }

    /** Get the pen pressure (if available)
     * @return
     */
    public float getPenPressure() {
        return penPressure;
    }

    /** Pen Tilt  if available 
     * @return  Point2D.Float with tilt information
     */
    public Point2D.Float getPenTilt() {
        return penTilt;
    }

    /** Pen Location as a new Point2D.Float object. <br>
     *  If a pen tablet is available, this method will return more accurate
     *  information on the pen location than the standard {@link MouseEvent}
     * @return  Point2D.Float with pen location information
     */
    public Point2D.Float getPenLocation() {
        return new Point2D.Float(penLocation.x, penLocation.y);
    }

    /** Set the pen location - set internally by mouse events */
    private void setPenLocation(MouseEvent event) {
        if (penType == PEN_CURSOR) {
            penLocation.x = event.getX();
            penLocation.y = event.getY();
        //System.out.println("Mouse: " + penLocation + " " + penLocationChanged);
        }
    }

    /** Set the pen location - set internally by pen events */
    private void setPenLocation(PLevelEvent ev) {
        for (PLevel level : ev.levels) {
            PLevel.Type levelType = level.getType();
            switch (levelType) {
                case X:
                    penLocation.x = level.value;
                    break;
                case Y:
                    penLocation.y = level.value;
                    break;
            }
        }
    }

    /** Has the pen location changed - useful for filtering out repeats
     * @return Boolean indicating if the pen location has changed or not
     */
    public boolean isPenLocationChanged() {
        return penLocationChanged;
    }

    /** The type of pen being used
     * @return  {@link AlcConstants#PEN_STYLUS}, {@link AlcConstants#PEN_ERASER}, {@link AlcConstants#PEN_CURSOR} or ZERO for unknown
     */
    public int getPenType() {
        return penType;
    }

    /** Set the pen type - Default is PEN_CURSOR 
     * @param ev PenEvent from JPen pen tablet library
     */
    private void setPenType() {
        PKind.Type kindType = pm.pen.getKind().getType();
        switch (kindType) {
            case CUSTOM:
                penType = 0;
                break;
            case STYLUS:
                currentColorIndex = 0;
                if(!alphaLocked){
                    setAlpha(currentAlphaSet[currentColorIndex]);
                }
                setColor(currentColorSet.get(currentColorIndex));                    
                Alchemy.toolBar.refreshColorButton();
                penType = PEN_STYLUS;
                break;
            case ERASER:
                currentColorIndex = 1;
                if(!alphaLocked){
                    setAlpha(currentAlphaSet[currentColorIndex]);
                }
                setColor(currentColorSet.get(currentColorIndex));
                Alchemy.toolBar.refreshColorButton();
                penType = PEN_ERASER;
                break;
            case CURSOR:
                penType = PEN_CURSOR;
        }
    }

    /** Resize the canvas - called when the window is resized
     * @param windowSize    The new window size
     */
    public void resizeCanvas(Dimension windowSize) {
        // Allow for the left hand toolbar if in 'simple' mode
        int x = 0;
        if (Alchemy.preferences.simpleToolBar) {
            x = Alchemy.toolBar.toolBarWidth;
            windowSize.width -= Alchemy.toolBar.toolBarWidth;
        }
        this.setBounds(x, 0, windowSize.width, windowSize.height);
    }

    /** Clear all shapes and then redraws the canvas */
    public void clear() {
        shapes.clear();
        createShapes.clear();
        affectShapes.clear();
        guideShapes.clear();

        this.canvasImage = null;
        if (imageDisplay && image != null) {
            canvasImage = renderCanvas(true);
        }

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

    /** Set the cursor temporarily - can be restored with {@link #restoreCursor()}
     * @param cursor    New temp cursor
     */
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

    /** Commit all shapes to the main {@link #shapes} array and render the image buffer */
    public void commitShapes() {
        // Add the createShapes and affectShapes to the main array
        // Add to the bottom if drawUnder is on
        if (drawUnder) {
            shapes.addAll(0, createShapes);
            shapes.addAll(0, affectShapes);
            // Refresh the canvasImage after the shapes have been added
            // to keep the ordering correct
            createShapes.clear();
            affectShapes.clear();
            canvasImage = renderCanvas(true, true);

        // Otherwise add to the top
        } else {
            // If the window is transparent
            if (Alchemy.window.isTransparent()) {
                canvasImage = renderCanvas(true, true);
            } else {
                canvasImage = renderCanvas(false);
            }
            shapes.addAll(createShapes);
            shapes.addAll(affectShapes);
            createShapes.clear();
            affectShapes.clear();
        }

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

    /** Get a normalized array of shapes with the top-left corner set to 0,0
     *  and the size set to the given value
     *
     * @param inputShapes   The shapes to normalize
     * @param size          The size to scale the shapes to
     * @return              The normailzed array list of shapes
     */
    public ArrayList<AlcShape> normailzeShapes(ArrayList<AlcShape> inputShapes, int size){
        ArrayList<AlcShape> outputShapes = new ArrayList<AlcShape>(inputShapes.size());

        for (AlcShape shape : inputShapes) {

            // Scale to size
            Rectangle bounds = shape.getBounds();
            // Figure out the longest side
            int longestSize = (bounds.width > bounds.height) ? bounds.width : bounds.height;
            // Create the scaling factor
            double scale = (float) size / longestSize;
            AffineTransform scaleTransform = new AffineTransform();
            scaleTransform.scale(scale, scale);
            GeneralPath gp = (GeneralPath) shape.getPath().createTransformedShape(scaleTransform);
            bounds = gp.getBounds();

            // Reset to 0, 0
            AffineTransform transform = new AffineTransform();
            transform.translate(0 - bounds.x, 0 - bounds.y);
            GeneralPath transformedPath = (GeneralPath) gp.createTransformedShape(transform);
            outputShapes.add(shape.customClone(transformedPath));
        }


        return outputShapes;
    }

    //////////////////////////////////////////////////////////////
    // SHAPES
    //////////////////////////////////////////////////////////////
    /** Check if there are {@link #shapes} available
     * @return True if there are shapes available, else false
     */
    public boolean hasShapes(){
        return shapes.size() > 0;
    }

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
     * @param shape (@link AlcShape} to become the current shape
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
    /** Removes a shape */
    public void removeShape(int s) {
        if (shapes.size() > 0 && s<shapes.size()) {
            shapes.remove(s);
        }
    }
    
    /** Removes the most recently added group of shapes which were
     *  laid down in a single mouse/pen event                      */
    public boolean removeShapeGroup(){
        
        if (!shapes.isEmpty()){
           int i = 0;
           while(i<shapeGroupsSize.get(shapeGroupsSize.size()-1)){
              removeShape(shapeGroups.get(shapeGroups.size()-1)); 
              i++;
           }
           i = 0;
           while(i<shapeGroups.size()){
               if(shapeGroups.get(i)>shapeGroups.get(shapeGroups.size()-1)){
               shapeGroups.set(i, shapeGroups.get(i)-shapeGroupsSize.get(shapeGroupsSize.size()-1));
               }
               i++;
           }
           redraw(true);
        
           shapeGroups.remove(shapeGroups.size()-1);
           shapeGroupsSize.remove(shapeGroupsSize.size()-1);        
        }
        if(shapes.isEmpty()){
            return(true);
        }else{
            return(false);
        }
            
    }
    
    /** Adds the current color, to the swatch array, sets it active */
    public void addCurrentColorToSwatch(){
        swatch.add(activeSwatchIndex+1, new Color(currentColorSet.get(currentColorIndex).getRed(),
                                                  currentColorSet.get(currentColorIndex).getGreen(),
                                                  currentColorSet.get(currentColorIndex).getBlue(),
                                                  currentAlphaSet[currentColorIndex]));
        activeSwatchIndex+=1;
    }

    //////////////////////////////////////////////////////////////
    // CREATE SHAPES
    //////////////////////////////////////////////////////////////
    /** Check if there are {@link #createShapes} available
     * @return True if there are create shapes available, else false
     */
    public boolean hasCreateShapes(){
        return createShapes.size() > 0;
    }

    /** Returns the most recently added create shape
     * @return The current create shape
     */
    public AlcShape getCurrentCreateShape() {
        if (createShapes.size() > 0) {
            return createShapes.get(createShapes.size() - 1);
        }
        return null;
    }

    /** Sets the most recently added create shape
     * @param shape     (@link AlcShape} to become the current create shape
     */
    public void setCurrentCreateShape(AlcShape shape) {
        if (createShapes.size() > 0) {
            createShapes.set(createShapes.size() - 1, shape);
        } else {
            createShapes.add(shape);
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
    /** Check if there are {@link #affectShapes} available
     * @return True if there are affect shapes available, else false
     */
    public boolean hasAffectShapes(){
        return affectShapes.size() > 0;
    }

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
     * @param shape     (@link AlcShape} to become the current affect shape
     */
    public void setCurrentAffectShape(AlcShape shape) {
        if (affectShapes.size() > 0) {
            affectShapes.set(affectShapes.size() - 1, shape);
        } else {
            affectShapes.add(shape);
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
            return guideShapes.get(guideShapes.size() - 1);
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
    // SHAPE/COLOR SETTINGS
    //////////////////////////////////////////////////////////////
    /** Get the current color
     * @return      The current color
     */
    public Color getColor() {
            return currentColorSet.get(currentColorIndex);
    }

    /** Set the current color
     * @param color
     */
    public void setColor(Color color) {
        try {
            previousColor = currentColorSet.get(currentColorIndex);
            currentColorSet.set(currentColorIndex, new Color(color.getRed(), color.getGreen(), color.getBlue()));
            if(!alphaLocked){
                currentAlphaSet[currentColorIndex] = alpha;
            }

            if (Alchemy.preferences.paletteAttached || Alchemy.preferences.simpleToolBar) {
                Alchemy.toolBar.refreshColorButton();
            }
        } catch (IllegalArgumentException ex) {
            // Ignore the color out of range exception caused by out of bounds slider settings
        }
    }
    
    // relay method to allow modules access to the toolbar color button
    public void refreshColorButtonRelay(){
        Alchemy.toolBar.refreshColorButton();
    }

    /** Get the background color
     * @return Color object of the background color
     */
    public Color getBackgroundColor() {
        return bgColor;
    }

    /** Set the background Color
     * @param color
     */
    public void setBackgroundColor(Color color) {
        this.bgColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
        redraw(true);
    }

    /** Get the current alpha value
     * @return 
     */
    public int getAlpha() {
            return alpha;
    }

    /** Set the current alpha value
     * @param alpha 
     */
    public void setAlpha(int alpha) {
            this.alpha = alpha;
            setColor(this.getColor());
            Alchemy.toolBar.refreshTransparencySlider();
    }

    /** Returns the state of alpha lock */
    public boolean isAlphaLocked(){
        return alphaLocked;
    }
    
    /** Toggles the state of alpha lock */
    public void toggleAlphaLocked(){
        if(alphaLocked){
            alphaLocked=false;
            setAlpha(currentAlphaSet[currentColorIndex]);
        }else{
            alphaLocked=true;
        }
    }
    
    public void setUndoDepth(int i){
        undoDepth = i;
    }
    
    public int getUndoDepth(){
        return undoDepth;
    }

    /** Get the current style
     * @return 
     */
    public int getStyle() {
        return style;
    }

    /** Set the current style
     * @param style 
     */
    public void setStyle(int style) {
        this.style = style;
    }

    /** Toggle the style between line and solid */
    public void toggleStyle() {
        if (style == STYLE_STROKE) {
            style = STYLE_FILL;
        } else {
            style = STYLE_STROKE;
        }
    }

    /** Get the current line width
     * @return 
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /** Set the current line width
     * @param lineWidth 
     */
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

    /** Returns if the record indicator (used with session auto-saving) is enabled 
     * @return
     */
    public boolean isRecordIndicatorEnabled() {
        return recordIndicator;
    }

    /** Set the display of the record indicator (used with session auto-saving)
     * @param recordIndicator   On or off
     */
    public void setRecordIndicator(boolean recordIndicator) {
        this.recordIndicator = recordIndicator;
    }

    /** Manage the automatic toggling of the toolbar */
    void setAutoToggleToolBar(boolean manageToolBar) {
        if (!Alchemy.preferences.paletteAttached) {
            this.autoToggleToolBar = manageToolBar;
        }
    }

    /** If the toolbar is being automatically toggled on/off or not */
    boolean isAutoToggleToolBar() {
        if (Alchemy.preferences.paletteAttached) {
            return false;
        } else {
            return this.autoToggleToolBar;
        }
    }

    //////////////////////////////////////////////////////////////
    // TRANSFORM
    //////////////////////////////////////////////////////////////
    /** Flip the shapes on the canvas horizontally */
    void flipHorizontal(){
        AffineTransform horizontalReflection = new AffineTransform();
        // Move the reflection into place
        horizontalReflection.translate(this.getWidth(), 0);
        // Reflect it using a negative scale
        horizontalReflection.scale(-1, 1);
        // Apply to every shape
        for(AlcShape shape : shapes){
            GeneralPath reflectedPath = (GeneralPath) shape.getPath().createTransformedShape(horizontalReflection);
            shape.setPath(reflectedPath);
            shape.setGradientPaint(makeHorizontalReflectedGradientPaint(shape.getGradientPaint()));
        }
        redraw(true);
    }
    
    /** Make a GradientPaint reflected through the horizontal axis */
    private GradientPaint makeHorizontalReflectedGradientPaint(GradientPaint gp) {
        if(gp == null){
            return null;
        }
        float horizontalAxis = this.getWidth() / 2;
        float x1 = horizontalAxis - ((float) gp.getPoint1().getX() - horizontalAxis);
        float x2 = horizontalAxis - ((float) gp.getPoint2().getX() - horizontalAxis);
        GradientPaint newGp = new GradientPaint(
                x1,
                (float) gp.getPoint1().getY(),
                gp.getColor1(),
                x2,
                (float) gp.getPoint2().getY(),
                gp.getColor2());
        return newGp;
    }


    /** Flip the shapes on the canvas Vertically */
    void flipVertical(){
        AffineTransform verticalReflection = new AffineTransform();
        // Move the reflection into place
        verticalReflection.translate(0, this.getHeight());
        // Reflect it using a negative scale
        verticalReflection.scale(1, -1);
        // Apply to every shape
        for(AlcShape shape : shapes){
            GeneralPath reflectedPath = (GeneralPath) shape.getPath().createTransformedShape(verticalReflection);
            shape.setPath(reflectedPath);
            shape.setGradientPaint(makeVerticalReflectedGradientPaint(shape.getGradientPaint()));
        }
        redraw(true);
    }

    /** Make a GradientPaint reflected through the vertical axis */
    private GradientPaint makeVerticalReflectedGradientPaint(GradientPaint gp) {
        if(gp == null){
            return null;
        }
        float verticalAxis = this.getHeight() / 2;
        float y1 = verticalAxis - ((float) gp.getPoint1().getY() - verticalAxis);
        float y2 = verticalAxis - ((float) gp.getPoint2().getY() - verticalAxis);
        GradientPaint newGp = new GradientPaint(
                (float) gp.getPoint1().getX(),
                y1,
                gp.getColor1(),
                (float) gp.getPoint2().getX(),
                y2,
                gp.getColor2());
        return newGp;
    }
    
    /** Zoom the Canvas 4x - keep location under mouse, under the mouse */
    public boolean zoomCanvas(boolean keyLaunch){
        
        //things get wacky if you quick-key zoom while over the toolbar...       
        if(this.getMousePosition()!=null||!keyLaunch){

            double x;
            double y;
            double imageZoomX;
            double imageZoomY;
            AffineTransform zoom = new AffineTransform();
            
            // Not Zoomed, lets set data for zooming/
            if(currentZoom==1/zoomAmount){
                currentZoom = zoomAmount;
                Point location = this.getMousePosition();
                // set the zoom coordinate so that location under mouse remains under the mouse
                lastZoomX = location.getX()-((this.getWidth()/zoomAmount)*(location.getX()/this.getWidth()));
                lastZoomY = location.getY()-((this.getHeight()/zoomAmount)*(location.getY()/this.getHeight()));
                imageZoomX = (location.getX()-imageLocation.getX())-
                   ( (this.getWidth()/zoomAmount) * ( (location.getX()-imageLocation.getX()) / this.getWidth() ) );
                imageZoomY = (location.getY()-imageLocation.getY())-
                   ( (this.getWidth()/zoomAmount) * ( (location.getY()-imageLocation.getY()) / this.getWidth() ) );
                x = 0 - ((zoomAmount * lastZoomX));
                y = 0 - ((zoomAmount * lastZoomY));
                
                zoomedImageLocation.x = (int)(imageLocation.getX() - (zoomAmount*imageZoomX));
                zoomedImageLocation.y = (int)(imageLocation.getY() - (zoomAmount*imageZoomY));
                
            // Zoomed, lets set data for unzooming
            }else{
                currentZoom = 1/zoomAmount;
                x = lastZoomX;
                y = lastZoomY;
            }       
            zoom = AffineTransform.getTranslateInstance ( x,y );
            zoom.scale(currentZoom,currentZoom);
            for(AlcShape shape : shapes){
                GeneralPath zoomPath = (GeneralPath) shape.getPath().createTransformedShape(zoom);
                shape.setPath(zoomPath);
                shape.setGradientPaint(makeHorizontalReflectedGradientPaint(shape.getGradientPaint()));
            }
            redraw(true);
            
            // success
            return(true);
        
        }else{
            // fail
            return(false);
        }       
    }
    
    public void startZoomMousing(){
        if(currentZoom<1){
            zoomMousing = true;
            Alchemy.toolBar.setToolBarVisible(false);
            setTempCursor(CURSOR_ZOOM);

        }else{
            zoomCanvas(false);
        }
    }
    public void stopZoomMousing(){
        zoomMousing = false;
        restoreCursor();
    }
    public boolean isCanvasZoomed(){
        if (currentZoom>1){
            return true;
        }else{
            return false;
        }
    }
    public Point2D.Double getZoomLocation(){
        Point2D.Double p = new Point2D.Double();
        if(isCanvasZoomed()){
            p.setLocation(lastZoomX,lastZoomY);
        }else{
            p.setLocation(0.0,0.0);
        }
        return p;
    }
    public double calculateZoomedX(double c){
        if(isCanvasZoomed()){
           c = (0 - (4*(int)lastZoomX))+(c*4);
           return c;
        }else{
           return c;
        }
    }
    public double calculateZoomedY(double c){
        if(isCanvasZoomed()){
           c = (0 - (4*(int)lastZoomY))+(c*4);
           return c;
        }else{
           return c;
        }
    }

    //////////////////////////////////////////////////////////////
    // IMAGE
    //////////////////////////////////////////////////////////////
    /** Set the Image to be drawn on the canvas
     * 
     * @param image Image to be drawn
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        if(this.image!=null){
           this.zoomedImage = getZoomedImage(this.image);
        }else{
            this.zoomedImage =null;
        }
        canvasImage = renderCanvas(true);
        if (image != null) {
            Alchemy.menuBar.unloadBackgroundImageItem.setEnabled(true);
        } else {
            Alchemy.menuBar.unloadBackgroundImageItem.setEnabled(false);
        }
    }
    
    private BufferedImage getZoomedImage(BufferedImage image){
        
        // Create new (blank) image of required (scaled) size
        BufferedImage scaledImage = new BufferedImage((int)(image.getWidth()*zoomAmount), 
                                                      (int)(image.getHeight()*zoomAmount), 
                                                      BufferedImage.TYPE_INT_ARGB);

        // Paint scaled version of image to new image
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, (int)(image.getWidth()*zoomAmount), (int)(image.getHeight()*zoomAmount), null);

        // clean up
        graphics2D.dispose();
        
        return scaledImage;
    }

    /** Get the current image
     * 
     * @return  The current image
     */
    public Image getImage() {
        if(isCanvasZoomed()){
            return this.zoomedImage;
        }else{
            return this.image;
        }
    }

    /** Check if an Image is defined or not
     * 
     * @return Image display on or off
     */
    public boolean isImageSet() {
        return image == null ? false : true;
    }

    /** Set image display to on or off
     * 
     * @param imageDisplay Image display on or off
     */
    public void setImageDisplay(boolean imageDisplay) {
        this.imageDisplay = imageDisplay;
        canvasImage = renderCanvas(true);
    }

    /** Check if image display is enabled
     * @return 
     */
    public boolean isImageDisplayEnabled() {
        return imageDisplay;
    }

    /** Set the location for the image to be displayed on the canvas
     * 
     * @param p
     */
    public void setImageLocation(Point p) {
        this.imageLocation = p;
    }

    /** Set the location for the image to be displayed on the canvas
     * 
     * @param x
     * @param y
     */
    public void setImageLocation(int x, int y) {
        this.imageLocation.x = x;
        this.imageLocation.y = y;
    }
    
    private void setZoomedImageLocation(){
        
    }
    /** Get the location where the image is displayed on the canvas
     * 
     * @return  Point - x & y location
     */
    public Point getImageLocation() {
        if(isCanvasZoomed()){
         return zoomedImageLocation;   
        }else{
        return imageLocation;
        }
    }

    /** Reset the image location back to zero */
    public void resetImageLocation() {
        this.imageLocation.x = 0;
        this.imageLocation.y = 0;
    }

    /** Set the transparent image to be drawn behind the canvas
     * 
     * @param transparentImage
     */
    void setTransparentImage(Image transparentImage) {
        this.transparentImage = transparentImage;
    }

    /** Create an image from the canvas
     * 
     * @param vectorMode    In vector mode all shapes are rendered from scratch.
     *                      Otherwise the active shapes are rendered on top of the current canvas image
     * @return
     */
    Image renderCanvas(boolean vectorMode) {
        return renderCanvas(vectorMode, false, 1, -1, -1);
    }

    /** Create an image from the canvas
     * 
     * @param vectorMode    In vector mode all shapes are rendered from scratch.
     *                      Otherwise the active shapes are rendered on top of the current canvas image
     * @param transparent   Ignore the background and create a transparent image with only shapes
     * @return
     */
    Image renderCanvas(boolean vectorMode, boolean transparent) {
        return renderCanvas(vectorMode, transparent, 1, -1, -1);
    }

    /** Create an image from the canvas
     * 
     * @param vectorMode    In vector mode all shapes are rendered from scratch.
     *                      Otherwise the active shapes are rendered on top of the current canvas image
     * @param scale         Scale setting to scale the canvas up or down
     * @return
     */
    Image renderCanvas(boolean vectorMode, double scale) {
        return renderCanvas(vectorMode, false, scale, -1, -1);
    }

    /** Create an image from the canvas
     * 
     * @param vectorMode    In vector mode all shapes are rendered from scratch.
     *                      Otherwise the active shapes are rendered on top of the current canvas image
     * @param transparent   Ignore the background and create a transparent image with only shapes
     * @param scale         Scale setting to scale the canvas up or down
     * @return
     */
    Image renderCanvas(boolean vectorMode, boolean transparent, double scale, int width, int height) {
        if (width == -1 || height == -1) {
            // Get the canvas size with out the frame/decorations
            java.awt.Rectangle visibleRect = this.getVisibleRect();
            width = visibleRect.width;
            height = visibleRect.height;
        }
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage buffImage;
        if (transparent) {
            buffImage = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        } else {
            buffImage = gc.createCompatibleImage(width, height, Transparency.OPAQUE);
        }
        // Paint the buffImage with the canvas
        Graphics2D g2 = buffImage.createGraphics();
        // Make sure the record indicator is off
        recordIndicator = false;

        if (scale != 1) {
            g2.scale(scale, scale);
        }

        if (transparent) {
            vectorCanvas.transparent = true;
            vectorCanvas.paintComponent(g2);
            vectorCanvas.transparent = false;
        } else {
//            vectorCanvas.transparent = false;
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
    // SAVE BITMAP
    //////////////////////////////////////////////////////////////
    /** Save the canvas to a bitmap file
     * 
     * @param file  The file object to save the bitmap to
     * @return      True if save worked, otherwise false
     */
    boolean saveBitmap(File file) {
        return saveBitmap(file, "png", false);
    }

    /** Save the canvas to a bitmap file
     * 
     * @param file          The file object to save the bitmap to
     * @param transparent   An image with transparency or not
     * @return              True if save worked, otherwise false
     */
    boolean saveBitmap(File file, boolean transparent) {
        return saveBitmap(file, "png", transparent);
    }

    /** Save the canvas to a bitmap file
     * 
     * @param file          The file object to save the bitmap to
     * @param format        The file format to save in
     * @return              True if save worked, otherwise false
     */
    boolean saveBitmap(File file, String format) {
        return saveBitmap(file, format, false);
    }

    // TODO - Scaleable image export
    /** Save the canvas to a bitmap file
     * 
     * @param file          The file object to save the bitmap to
     * @param format        The file format to save in
     * @param transparent   An image with transparency or not
     * @return              True if save worked, otherwise false
     */
    boolean saveBitmap(File file, String format, boolean transparent) {
        try {
            setGuide(false);
            Image bitmapImage = renderCanvas(true, transparent);
            setGuide(true);
            ImageIO.write((BufferedImage) bitmapImage, format, file);
            return true;
        } catch (IOException ex) {
            System.err.println(ex);
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
     * This code is based on code from the book Java Examples in a Nutshell, 2nd Edition. Copyright (c) 2000 David Flanagan. 
     *
     * @param g
     * @param format
     * @param pageIndex
     * @return
     * @throws PrinterException 
     */
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
        setPenLocation(event);
        if (isAutoToggleToolBar()) {
            Alchemy.toolBar.toggleToolBar(event.getY());
        }
        if (events) {
            // Pass to the current create module
            if (createEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseMoved(event);
            }
            // Pass to all active affect modules
            if (affectEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mouseMoved(event);
                        }
                    }
                }
            }
        }
        if (penType != PEN_CURSOR) {
            penLocationChanged = false;
        }
    }

    public void mousePressed(MouseEvent event) {
        penDown = true;
        // Hide the toolbar when clicking on the canvas
        if (!Alchemy.preferences.paletteAttached && Alchemy.toolBar.isToolBarVisible() &&
                !Alchemy.preferences.simpleToolBar && event.getY() >= Alchemy.toolBar.getTotalHeight()) {
            Alchemy.toolBar.setToolBarVisible(false);
        }
        
        if(event.getButton()!=MouseEvent.BUTTON1&&zoomMousing){
            stopZoomMousing();
        }

        if (events) {          
            if(zoomMousing){             
                zoomMousing = false;
                zoomCanvas(false);
                Alchemy.toolBar.setZoomButtonSelected();
                restoreCursor();
                
            }else{
            
                // Pass to the current create module
                if (createEvents) {
                    Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mousePressed(event);
                }
                // Pass to all active affect modules
                if (affectEvents) {
                    if (Alchemy.plugins.hasCurrentAffects()) {
                        for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                            if (Alchemy.plugins.currentAffects[i]) {
                                Alchemy.plugins.affects[i].mousePressed(event);
                            }
                        }
                    }
                }
                startUndoGroup();       
            }      
        }
    }
    
    public void startUndoGroup(){
        lastShapesSize = shapes.size();
        if(drawUnder){//drawUnder){
            shapeGroups.add(0);
        }else{
            shapeGroups.add(shapes.size());         
        } 
    }
    
    public void finishUndoGroup(){
        if(undoDepth>0){Alchemy.toolBar.enableUndo();}

        int groupSize = shapes.size() - lastShapesSize;
        shapeGroupsSize.add(groupSize);
         
        //drawunder was enabled - or it was the first shape and doesnt matter
        if(shapeGroups.get(shapeGroups.size()-1)==0){
            int i = shapeGroups.size()-2;
            int test;
            while(i>=0){
                test = shapeGroups.get(i)+groupSize;
                shapeGroups.set(i, shapeGroups.get(i)+groupSize);
                i--;
            }
        }     
    }

    public void mouseClicked(MouseEvent event) {
        if (events) {
            // Pass to the current create module
            if (createEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseClicked(event);
            }
            // Pass to all active affect modules
            if (affectEvents) {
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
        if (events) {
            // Pass to the current create module
            if (createEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseEntered(event);
            }
            // Pass to all active affect modules
            if (affectEvents) {
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
        if (events) {
            // Pass to the current create module
            if (createEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseExited(event);
            }
            // Pass to all active affect modules
            if (affectEvents) {
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
        penDown = false;
        if (events) {
            // Pass to the current create module
            if (createEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseReleased(event);
            }
            // Pass to all active affect modules
            if (affectEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mouseReleased(event);
                        }
                    }
                }
            }
            finishUndoGroup();
        }
    }

    public void mouseDragged(MouseEvent event) {
        setPenLocation(event);
        if (events) {
            // Pass to the current create module
            if (createEvents) {
                Alchemy.plugins.creates[Alchemy.plugins.currentCreate].mouseDragged(event);
            }
            // Pass to all active affect modules
            if (affectEvents) {
                if (Alchemy.plugins.hasCurrentAffects()) {
                    for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                        if (Alchemy.plugins.currentAffects[i]) {
                            Alchemy.plugins.affects[i].mouseDragged(event);
                        }
                    }
                }
            }
        }
        if (penType != PEN_CURSOR) {
            penLocationChanged = false;
        }
    }
    //////////////////////////////////////////////////////////////
    // PEN EVENTS
    //////////////////////////////////////////////////////////////
    public void penKindEvent(PKindEvent ev) {
        setPenType();
    }

    public void penLevelEvent(PLevelEvent ev) {
        //setPenType();
        // Register the pen pressure, tilt and location 
        // Do this only if this is an actual pen
        // Otherwise register pen location using the mouse
        if (penType != PEN_CURSOR) {
            // Pressure and tilt is only good when the pen is down
            if (penDown) {
                penPressure = pm.pen.getLevelValue(PLevel.Type.PRESSURE);
                // parabolic sensitivity
                penPressure *= penPressure;
                penTilt.x = pm.pen.getLevelValue(PLevel.Type.TILT_X);
                penTilt.y = pm.pen.getLevelValue(PLevel.Type.TILT_Y);
            }
            // If this event is a movement
            if (ev.isMovement()) {
                // Register the pen location even when the mouse is up
                setPenLocation(ev);
                penLocationChanged = true;
            }
        }
    }

    public void penButtonEvent(PButtonEvent arg0) {
    }

    public void penScrollEvent(PScrollEvent arg0) {
    }

    public void penTock(long arg0) {
    }

    /** Vector Canvas
     *  Draws the canvas in full, including all shapes,
     *  the background and buffImage if any.
     */
    class VectorCanvas extends JPanel implements AlcConstants {

        boolean transparent = false;
        private int width,  height;

        @Override
        public void paintComponent(Graphics g) {

            super.paintComponent(g);

            width = Alchemy.canvas.getWidth();
            height = Alchemy.canvas.getHeight();

            Graphics2D g2 = (Graphics2D) g;

            // Get the PDF Content Byte
            PdfContentByte cb = null;

            if (g2 instanceof PdfGraphics2D) {
                PdfGraphics2D g2pdf = (PdfGraphics2D) g2;
                cb = g2pdf.getContent();
            }

            if (Alchemy.canvas.smoothing) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            } else {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }

            // Do not draw the background when creating a transparent image
            if (!transparent) {
                // Paint background without transparency
                Color bgColor = Alchemy.canvas.getBackgroundColor();
                g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue()));
                g2.fillRect(0, 0, width, height);
            }

            // PDF READER
            if (Alchemy.session.pdfReadPage != null) {

                // Remember the old transform settings
                AffineTransform at = g2.getTransform();

                int pageWidth = (int) Alchemy.session.pdfReadPage.getWidth();
                int pageHeight = (int) Alchemy.session.pdfReadPage.getHeight();
                PDFRenderer renderer = new PDFRenderer(Alchemy.session.pdfReadPage, g2, new Rectangle(0, 0, pageWidth, pageHeight), null, Alchemy.canvas.getBackgroundColor());
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


            // Draw the shapes, create, and affect lists
            for (int j = 0; j < Alchemy.canvas.fullShapeList.length; j++) {
                for (int i = 0; i < Alchemy.canvas.fullShapeList[j].size(); i++) {
                    AlcShape currentShape = (AlcShape) Alchemy.canvas.fullShapeList[j].get(i);
                    Paint paint = currentShape.getPaint();
                    
                    // LINE
                    if (currentShape.style == STYLE_STROKE) {
                        //g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                        g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

                        // If this shape is a gradient and we are making a PDF
                        if (paint instanceof GradientPaint && cb != null) {
                            drawTransparentGradient(cb, g2, (GradientPaint) paint, currentShape.path, false);
                        } else {
                            g2.setPaint(paint);
                            g2.draw(currentShape.path);
                        }

                    // SOLID
                    } else {

                        // If this shape is a gradient and we are making a PDF
                        if (paint instanceof GradientPaint && cb != null) {
                            drawTransparentGradient(cb, g2, (GradientPaint) paint, currentShape.path, true);
                        } else {
                            g2.setPaint(paint);
                            g2.fill(currentShape.path);
                        }


                    }
                }
            }
            if (Alchemy.canvas.isGuideEnabled()) {
                for (int i = 0; i < Alchemy.canvas.guideShapes.size(); i++) {
                    AlcShape currentShape = Alchemy.canvas.guideShapes.get(i);
                    // LINE
                    if (currentShape.style == STYLE_STROKE) {
                        //g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
                        g2.setStroke(new BasicStroke(currentShape.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                        g2.setColor(currentShape.color);
                        g2.draw(currentShape.path);
                    // SOLID
                    } else {
                        g2.setColor(currentShape.color);
                        g2.fill(currentShape.path);
                    }
                }
            }

            g2.dispose();
        }

        /** Draw a transparent gradient to the PDF */
        private void drawTransparentGradient(PdfContentByte cb, Graphics2D g2, GradientPaint gp, GeneralPath path, boolean fill) {

            //Create template
            PdfTemplate template = cb.createTemplate(width, height);

            //Prepare transparent group
            PdfTransparencyGroup transGroup = new PdfTransparencyGroup();
            transGroup.put(PdfName.CS, PdfName.DEVICERGB);
            transGroup.setIsolated(true);
            transGroup.setKnockout(false);
            template.setGroup(transGroup);

            //Prepare graphic state
            PdfGState gState = new PdfGState();
            PdfDictionary maskDict = new PdfDictionary();
            maskDict.put(PdfName.TYPE, PdfName.MASK);
            maskDict.put(PdfName.S, new PdfName("Luminosity"));
            maskDict.put(new PdfName("G"), template.getIndirectReference());
            gState.put(PdfName.SMASK, maskDict);
            cb.setGState(gState);

            // Create a gradient to use as the mask
            // Also flip the Y location
            PdfShading shading = PdfShading.simpleAxial(
                    cb.getPdfWriter(),
                    (float) gp.getPoint1().getX(),
                    (float) (height - gp.getPoint1().getY()),
                    (float) gp.getPoint2().getX(),
                    (float) (height - gp.getPoint2().getY()),
                    Color.WHITE,
                    Color.BLACK,
                    true,
                    true);
            template.paintShading(shading);

            // Draw the actual color under the mask
            g2.setColor(gp.getColor1());
            // SOLID
            if (fill) {
                g2.fill(path);
            // LINE    
            } else {
                g2.draw(path);
            }

        }
    }
}
