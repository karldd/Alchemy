/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2009 Karl D.D. Willis
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

import java.awt.Point;
import java.awt.Color;

import java.awt.Rectangle;
import java.awt.geom.*;
import java.io.Serializable;

/**
 * A shape class used by Alchemy<br>
 * The main shape is stored as a {@link GeneralPath} object
 * with other variables defining the colour, style, alpha etc...
 */
public class AlcShape implements AlcConstants, Cloneable, Serializable {

    /** The main path stored as a GeneralPath */
    protected GeneralPath path;
    /** Colour of this shape */
    protected Color colour;
    /** Alpha of this shape */
    private int alpha;
    /** Style of this shape - (1) LINE or (2) SOLID FILL */
    protected int style;
    /** Line Weight if the style is line */
    protected float lineWidth;
    /** Line smoothing global setting */
    static boolean lineSmoothing = true;
    /** Store the last point */
    private Point2D.Float lastPt;
    /** For drawing smaller marks - draw lines until x points have been made */
    private final int startPoints = 5;
    /** Minimum distance until points are added */
    private final int minMovement = 5;
    /** Keep track of the number of points added */
    private int totalPoints = 0;

    /**
     * Creates a new instance of AlcShape with the default values
     * @param p Initial point to set the GeneralPath moveto
     */
    public AlcShape(Point p) {
        setupPoint(p);
        setupDefaultAttributes();
    }

    /**
     * Creates a new instance of AlcShape with the default values
     * @param p Initial point to set the GeneralPath moveto
     */
    public AlcShape(Point2D.Float p) {
        setupPoint(p);
        setupDefaultAttributes();
    }

    /**
     * Creates a new instance of AlcShape with defined values
     * @param p         Initial point to set the GeneralPath moveto
     * @param colour    Colour of the shape
     * @param alpha     Alpha value of the shape
     * @param style     Style of the shape - (1) LINE or (2) SOLID FILL 
     * @param lineWidth Line width of the shape
     */
    public AlcShape(Point p, Color colour, int alpha, int style, float lineWidth) {
        setupPoint(p);
        setupAttributes(colour, alpha, style, lineWidth);
    }

    /**
     * Creates a new instance of AlcShape with defined values
     * @param p         Initial point to set the GeneralPath moveto
     * @param colour    Colour of the shape
     * @param alpha     Alpha value of the shape
     * @param style     Style of the shape - (1) LINE or (2) SOLID FILL 
     * @param lineWidth Line width of the shape
     */
    public AlcShape(Point2D.Float p, Color colour, int alpha, int style, float lineWidth) {
        setupPoint(p);
        setupAttributes(colour, alpha, style, lineWidth);
    }

    /** 
     * Creates a blank AlcShape object
     */
    public AlcShape() {
        setupBlank();
        setupDefaultAttributes();
    }

    /**
     * Creates a new instance of AlcShape with the default values
     * @param path    GeneralPath path
     */
    public AlcShape(GeneralPath path) {
        setupShape(path);
        setupDefaultAttributes();
    }

    /**
     * Creates a new instance of AlcShape with defined values
     * @param path        GeneralPath path
     * @param colour    Colour of the shape
     * @param alpha     Alpha value of the shape
     * @param style     Style of the shape - (1) LINE or (2) SOLID FILL 
     * @param lineWidth Line width of the shape
     */
    public AlcShape(GeneralPath path, Color colour, int alpha, int style, float lineWidth) {
        setupShape(path);
        setupAttributes(colour, alpha, style, lineWidth);
    }

    private void setupBlank() {
        // Create an empty shape
        path = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
    }

    private void setupPoint(Point p) {
        setupPoint(new Point2D.Float(p.x, p.y));
    }

    private void setupPoint(Point2D.Float p) {
        // Create the path and move to the first point
        path = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
        path.moveTo(p.x, p.y);
        totalPoints++;
        savePoints(p);
    }

    private void setupShape(GeneralPath path) {
        // Add the path
        this.path = path;
        path.setWindingRule(GeneralPath.WIND_NON_ZERO);
    }

    /** Setup the attributes of a shape
     * 
     * @param colour    Colour of the shape
     * @param alpha     Alpha value of the shape
     * @param style     Style of the shape - (1) LINE or (2) SOLID FILL 
     * @param lineWidth Line width of the shape
     */
    public void setupAttributes(Color colour, int alpha, int style, float lineWidth) {
        this.alpha = alpha;
        setColour(colour);
        this.style = style;
        this.lineWidth = lineWidth;
    }

    /** Set the attributes of this shape (Alpha/Colour/Style/LineWidth)
     *  to the current values of the canvas
     */
    public void setupDefaultAttributes() {
        this.alpha = Alchemy.canvas.getAlpha();
        this.colour = Alchemy.canvas.getColour();
        this.style = Alchemy.canvas.getStyle();
        this.lineWidth = Alchemy.canvas.getLineWidth();
    }

    /** 
     * Add a curve point to the shape 
     * This method uses a simple smoothing algorithm to get rid of hard edges
     * @param p Point to curve to
     */
    public void addCurvePoint(Point p) {
        addCurvePoint(new Point2D.Float(p.x, p.y));
    }

    /** 
     * Add a curve point to the shape 
     * This method uses a simple smoothing algorithm to get rid of hard edges
     * @param p Point to curve to
     */
    public void addCurvePoint(Point2D.Float p) {
        if (lineSmoothing) {

            // Filter out repeats
            if (!p.equals(lastPt)) {

                // At the start just draw lines so smaller marks can be made
                if (totalPoints < startPoints) {

                    path.lineTo(p.x, p.y);
                    savePoints(p);

                } else {

                    // Movement since the last point was drawn
                    float movement = Math.abs(p.x - lastPt.x) + Math.abs(p.y - lastPt.y);
                    //System.out.println(p.x + " " + lastPt.x);

                    // Test to see if this point has moved far enough
                    if (movement > minMovement) {

                        // New control point value
                        Point2D.Float pt = new Point2D.Float();

                        // Average the points
                        pt.x = (lastPt.x + p.x) / 2F;
                        pt.y = (lastPt.y + p.y) / 2F;

                        // Add the Quadratic curve - control point x1, y1 and actual point x2, y2
                        path.quadTo(lastPt.x, lastPt.y, pt.x, pt.y);
                        savePoints(p);

                    }
                }
            }
        } else {
            addLinePoint(p);
        }
    }

    /**
     * Add a straight line point to the shape
     * @param p Point to draw a line to
     */
    public void addLinePoint(Point p) {
        addLinePoint(new Point2D.Float(p.x, p.y));
    }

    /**
     * Add a straight line point to the shape
     * @param p Point to draw a line to
     */
    public void addLinePoint(Point2D.Float p) {
        // Filter out repeats
        if (!p.equals(lastPt)) {
            // At the start just draw lines so smaller marks can be made
            if (totalPoints < startPoints) {

                path.lineTo(p.x, p.y);
                savePoints(p);

            } else {

                // Movement since the last point was drawn
                float movement = Math.abs(p.x - lastPt.x) + Math.abs(p.y - lastPt.y);

                // Test to see if this point has moved far enough
                if (movement > minMovement) {
                    path.lineTo(p.x, p.y);
                    savePoints(p);
                }
            }
        }
    }

    /** Append a GeneralPath to this shape
     * 
     * @param newPath   The path to be appended
     * @param connect   Connect the two paths together or not
     */
    public void append(GeneralPath newPath, boolean connect) {
        this.path.append(newPath, connect);
    }

    /** 
     * Save the points to keep track of the total number of points
     * along with setting the last point
     * @param p     Point to be saved
     */
    private void savePoints(Point2D.Float p) {
        // Increment the total number of points
        totalPoints++;
        // Set the current point to the (original) last point value - not the altered pt value
        lastPt = new Point2D.Float(p.x, p.y);
    }

    /** Add the first point
     * @param p Point to draw a line to
     */
    public void addFirstPoint(Point p) {
        path.moveTo(p.x, p.y);
    }

    /** Add the first point
     * @param p Point to draw a line to
     */
    public void addFirstPoint(Point2D.Float p) {
        path.moveTo(p.x, p.y);
    }

    /** Add the last point as a straight line 
     * @param p Point to draw a line to
     */
    public void addLastPoint(Point p) {
        path.lineTo(p.x, p.y);
    }

    /** Add the last point as a straight line 
     * @param p Point to draw a line to
     */
    public void addLastPoint(Point2D.Float p) {
        path.lineTo(p.x, p.y);
    }

    /** Move the shape by the specified distance.
     *  Consecutive calls will accumulate the overall distance
     * @param x     x distance
     * @param y     y distance
     */
    public void move(double x, double y) {
        AffineTransform move = AffineTransform.getTranslateInstance(x, y);
        GeneralPath transformedPath = (GeneralPath) path.createTransformedShape(move);
        this.path = transformedPath;
    }

    /** Scale the shape by a certain factor
     * 
     * @param sx    X scale factor
     * @param sy    Y scale factor
     */
    public void scale(double sx, double sy) {
        AffineTransform scale = AffineTransform.getScaleInstance(sx, sy);
        GeneralPath transformedPath = (GeneralPath) path.createTransformedShape(scale);
        this.path = transformedPath;
    }

    /** Rotate the shape using the shapes centre as the anchor point
     * 
     * @param theta     The angle of rotation in radians
     */
    public void rotate(double theta) {
        Rectangle bounds = this.path.getBounds();
        int x = bounds.x + bounds.width / 2;
        int y = bounds.y + bounds.height / 2;
        rotate(theta, x, y);
    }

    /** Rotate the shape
     * 
     * @param theta     The angle of rotation in radians
     * @param x         The coordinates of the x anchor point
     * @param y         The coordinates of the y anchor point
     */
    public void rotate(double theta, double x, double y) {
        AffineTransform rotate = AffineTransform.getRotateInstance(theta, x, y);
        GeneralPath transformedPath = (GeneralPath) path.createTransformedShape(rotate);
        this.path = transformedPath;
    }

    //////////////////////////////////////////////////////////////
    // ALCSHAPE GETTERS/SETTERS
    //////////////////////////////////////////////////////////////
    /** 
     * Return the GeneralPath path
     * @return GeneralPath path
     */
    public GeneralPath getPath() {
        return path;
    }

    /**
     * Set the path using a GeneralPath
     * @param path
     */
    public void setPath(GeneralPath path) {
        this.path = path;
    }

    /**
     * Set (or reset perhaps) the shape with a single Point
     * @param p
     */
    public void setPoint(Point p) {
        setPoint(new Point2D.Float(p.x, p.y));
    }

    /**
     * Set (or reset perhaps) the shape with a single Point
     * @param p
     */
    public void setPoint(Point2D.Float p) {
        path = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
        path.moveTo(p.x, p.y);
        totalPoints = 1;
    }

    /** 
     * Return the total number of points in this shape
     * @return Total number of points
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /** 
     * Set the total number of points for this shape
     * Useful when creating a duplicate object
     * @param totalPoints   Total number of points
     */
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    /** 
     * Recalculates the number of points for this shape
     * Useful when shapes have been merged together
     */
    public void recalculateTotalPoints() {
        PathIterator count = path.getPathIterator(null);
        int numberOfPoints = 0;
        while (!count.isDone()) {
            numberOfPoints++;
            count.next();
        }
        this.totalPoints = numberOfPoints;
    }

    /** 
     * Return the last point
     * @return The last point
     */
    public Point2D.Float getLastPoint() {
        return lastPt;
    }

    /** 
     * Set the last point for this shape
     * Useful when creating a duplicate object
     * @param lastPt   The last point
     */
    public void setLastPoint(Point2D.Float lastPt) {
        this.lastPt = lastPt;
    }

    /**
     * Get the colour of this shape
     * @return The colour
     */
    public Color getColour() {
        return colour;
    }

    /**
     * Set the colour of this shape
     * @param colour The colour
     */
    public void setColour(Color colour) {
        this.colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
    }

    /**
     * Get the alpha (transparency) value of this shape
     * @return Alpha value
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Set the alpha (transparency) value of this shape
     * @param alpha Alpha value
     */
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        setColour(this.colour);
    }

    /**
     * Set a colour with alpha directly 
     * @param colour
     */
    public void setAlphaColour(Color colour) {
        this.colour = colour;
    }

    /**
     * Get the style of this shape
     * @return  The style of this shape - (1) LINE or (2) SOLID FILL
     */
    public int getStyle() {
        return style;
    }

    /**
     * Set the style of this shape
     * @param style  The style of this shape - (1) LINE or (2) SOLID FILL 
     */
    public void setStyle(int style) {
        this.style = style;
    }

    /**
     * Get the line width of this shape
     * @return  The line width
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /** Set the line width of this shape
     * 
     * @param lineWidth The line width
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    /** Get the bounds of this shape 
     * 
     * @return Rectangle representing the shapes bounds
     */
    public Rectangle getBounds() {
        return this.path.getBounds();
    }

    /**
     * 'Deep' Clone this object using the existing style/colour etc.. values
     * @return An new cloned object of this shape
     */
    @Override
    public Object clone() {
        //Deep copy
        AlcShape tempShape = new AlcShape(this.path, this.colour, this.alpha, this.style, this.lineWidth);
        tempShape.setTotalPoints(this.totalPoints);
        tempShape.setLastPoint(this.lastPt);
        return tempShape;
    }

    /** 
     * A custom clone that adds a new GeneralPath to the shape
     * while keeping all of the style infomation
     * 
     * @param tempPath  A GeneralPath to be added to the AlcShape
     * @return          The cloned shape
     */
    public AlcShape customClone(GeneralPath tempPath) {
        //Deep copy
        AlcShape tempShape = new AlcShape(tempPath, this.colour, this.alpha, this.style, this.lineWidth);
        tempShape.setTotalPoints(this.totalPoints);
        tempShape.setLastPoint(this.lastPt);
        return tempShape;
    }
}
