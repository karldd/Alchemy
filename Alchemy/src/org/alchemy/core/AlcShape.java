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
import java.awt.geom.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A shape class used by Alchemy<br>
 * The main shape is stored as a {@link GeneralPath} object
 * with other variables defining the color, style, alpha etc...
 */
public class AlcShape implements AlcConstants, Cloneable, Serializable {

    //////////////////////////////////////////////////////////////
    // SHAPE ATTRIBUTES
    //////////////////////////////////////////////////////////////
    /** The main path stored as a GeneralPath */
    GeneralPath path;
    /** Color of this shape */
    Color color;
    /** Alpha of this shape */
    int alpha = 255;
    /** Style of this shape - (1) LINE or (2) SOLID FILL */
    int style;
    /** The Gradient of this shape (if available) */
    private GradientPaint gradientPaint;
    /** Line Weight if the style is line */
    float lineWidth;
    /** Line smoothing global setting */
    private static boolean lineSmoothing = true;
    /** The last point */
    private Point2D.Float lastPoint;
    /** If the path has been closed or not */
    private boolean pathClosed = false;
    /** If this shape has been created with pen strokes or not */
    private boolean penShape = false;
    /** Keep track of the number of points added */
    private int totalPoints = 0;
    /** For shapes drawn as a line with a variable width, this is the spine of the shape */
    private ArrayList<Point2D.Float> spine;
    /** For shapes drawn as a line with a variable width, this is the width of the shape */
    private ArrayList<Float> spineWidth;
    /** Utility variable used for storing the sort index of an array of AlcShapes */
    private int sortIndex = 0;
    //////////////////////////////////////////////////////////////
    // SHAPE PREFERENCES
    //////////////////////////////////////////////////////////////
    /** For drawing smaller marks - draw lines until x points have been made */
    private final int startPoints = 5;
    /** Minimum distance until points are added */
    private final int minDistance = 5;
    /** Minimum distance until spine points are added */
    private final int minDistanceSpine = 3;

    //////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    //////////////////////////////////////////////////////////////
    /**
     * Creates a new instance of AlcShape with the default values
     * @param p Initial point to set the GeneralPath moveto
     */
    public AlcShape(Point p) {
        setup(p);
        setup();
    }

    /**
     * Creates a new instance of AlcShape with the default values
     * @param p Initial point to set the GeneralPath moveto
     */
    public AlcShape(Point2D.Float p) {
        setup(p);
        setup();
    }
    
    /**
     * Creates a new instance of AlcShape with defined values
     * @param p         Initial point to set the GeneralPath moveto
     * @param color     Color of the shape
     * @param alpha     Alpha value of the shape
     * @param style     Style of the shape - (1) LINE or (2) SOLID FILL 
     * @param lineWidth Line width of the shape
     */
    public AlcShape(Point p, Color color, int alpha, int style, float lineWidth) {
        setup(p);
        setup(color, alpha, style, lineWidth);
    }

    /**
     * Creates a new instance of AlcShape with defined values
     * @param p         Initial point to set the GeneralPath moveto
     * @param color    color of the shape
     * @param alpha     Alpha value of the shape
     * @param style     Style of the shape - (1) LINE or (2) SOLID FILL 
     * @param lineWidth Line width of the shape
     */
    public AlcShape(Point2D.Float p, Color color, int alpha, int style, float lineWidth) {
        setup(p);
        setup(color, alpha, style, lineWidth);
    }

    /** 
     * Creates a blank AlcShape object
     */
    public AlcShape() {
        // Create an empty shape
        path = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
        setup();
    }

    /**
     * Creates a new instance of AlcShape with the default values
     * @param path    GeneralPath path
     */
    public AlcShape(GeneralPath path) {
        setup(path);
        setup();
    }

    /**
     * Creates a new instance of AlcShape with defined values
     * @param path      GeneralPath path
     * @param color     Color of the shape
     * @param alpha     Alpha value of the shape
     * @param style     Style of the shape - (1) LINE or (2) SOLID FILL 
     * @param lineWidth Line width of the shape
     */
    public AlcShape(GeneralPath path, Color color, int alpha, int style, float lineWidth) {
        setup(path);
        setup(color, alpha, style, lineWidth);
    }

    //////////////////////////////////////////////////////////////
    // SHAPE INITILISATION
    //////////////////////////////////////////////////////////////
    private void setup(Point p) {
        setup(new Point2D.Float(p.x, p.y));
    }

    private void setup(Point2D.Float p) {
        // Create the path and move to the first point
        path = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
        path.moveTo(p.x, p.y);
        totalPoints++;
        savePoints(p);
    }

    private void setup(GeneralPath path) {
        // Add the path
        this.path = path;
        path.setWindingRule(GeneralPath.WIND_NON_ZERO);
        recalculateTotalPoints();
    }

    /** Setup the attributes of a shape
     * 
     * @param color     Color of the shape
     * @param alpha     Alpha value of the shape
     * @param style     Style of the shape - (1) LINE or (2) SOLID FILL 
     * @param lineWidth Line width of the shape
     */
    public void setup(Color color, int alpha, int style, float lineWidth) {
        this.alpha = alpha;
        setColor(color);
        this.style = style;
        if (lineWidth < 0) {
            lineWidth = 0;
        }
        this.lineWidth = lineWidth;
    }

    /** Set the attributes of this shape (Alpha/Color/Style/LineWidth)
     *  to the current values of the canvas
     */
    public void setup() {
        this.alpha = Alchemy.canvas.getAlpha();
        setColor(Alchemy.canvas.getColor());
        this.style = Alchemy.canvas.getStyle();
        if (Alchemy.canvas.getLineWidth() < 0) {
            this.lineWidth = 0;
        } else {
            this.lineWidth = Alchemy.canvas.getLineWidth();
        }
    }

    /** 
     * Add a curve point to the shape 
     * This method uses a simple smoothing algorithm to get rid of hard edges
     * @param p Point to curve to
     */
    public void curveTo(Point p) {
        curveTo(new Point2D.Float(p.x, p.y));
    }

    /** 
     * Add a curve point to the shape 
     * This method uses a simple smoothing algorithm to get rid of hard edges
     * @param p Point to curve to
     */
    public void curveTo(Point2D.Float p) {
        if (lineSmoothing) {

            // Filter out repeats
            if (!p.equals(lastPoint)) {

                // At the start just draw lines so smaller marks can be made
                if (totalPoints < startPoints) {

                    path.lineTo(p.x, p.y);
                    savePoints(p);

                } else {

                    // Movement since the last point was drawn
                    double movement = p.distance(lastPoint);
                    //System.out.println(p.x + " " + lastPt.x);

                    // Test to see if this point has moved far enough
                    if (movement > minDistance) {

                        // New control point value
                        Point2D.Float pt = new Point2D.Float();

                        // Average the points
                        pt.x = (lastPoint.x + p.x) / 2F;
                        pt.y = (lastPoint.y + p.y) / 2F;

                        // Add the Quadratic curve - control point x1, y1 and actual point x2, y2
                        path.quadTo(lastPoint.x, lastPoint.y, pt.x, pt.y);
                        savePoints(p);

                    }
                }
            }
        } else {
            lineTo(p);
        }
    }

    /**
     * Add a straight line point to the shape
     * @param p Point to draw a line to
     */
    public void lineTo(Point p) {
        lineTo(new Point2D.Float(p.x, p.y));
    }

    /**
     * Add a straight line point to the shape
     * @param p Point to draw a line to
     */
    public void lineTo(Point2D.Float p) {
        // Filter out repeats
        if (!p.equals(lastPoint)) {
            // At the start just draw lines so smaller marks can be made
            if (totalPoints < startPoints) {

                path.lineTo(p.x, p.y);
                savePoints(p);

            } else {

                // Movement since the last point was drawn
                double movement = p.distance(lastPoint);

                // Test to see if this point has moved far enough
                if (movement > minDistance) {
                    path.lineTo(p.x, p.y);
                    savePoints(p);
                }
            }
        }
    }

    /** 
     * Add a spine point for variable width lines
     * @param p     The point to add
     * @param width The width of the line
     */
    public void spineTo(Point2D.Float p, float width) {
        this.penShape = true;
        if (spine == null) {
            spine = new ArrayList<Point2D.Float>(1000);
        }
        if (spineWidth == null) {
            spineWidth = new ArrayList<Float>(1000);
        }

        // Check that the pen location has changed
        if (Alchemy.canvas.isPenLocationChanged()) {
            // If this is the first point then add it
            if (spine.size() == 0) {
                spine.add(p);
                spineWidth.add(width);

            // If this is the second point onwards
            // Then check there has been enough movement    
            } else {
                Point2D.Float lastPt = spine.get(spine.size() - 1);
                double distance = p.distance(lastPt);
                if (distance > minDistanceSpine) {
                    spine.add(p);
                    spineWidth.add(width);
                    createSpine();
                }
            }
        }
    }

    /** Create the spine - redraws the variable width line based on the spine points */
    public void createSpine() {
        if (spine.size() > 0) {
            // Reset the shape and create the first point
            setPoint(spine.get(0));
            // Draw the outer points
            for (int i = 1; i < spine.size(); i++) {
                Point2D.Float p2 = spine.get(i - 1);
                Point2D.Float p1 = spine.get(i);
                float level = (spineWidth.get(i)).floatValue();
                Point2D.Float pOut = rightAngle(p1, p2, level);
                curveTo(pOut);
            }
            // Draw the inner points
            for (int j = 1; j < spine.size(); j++) {
                int index = (spine.size() - j);
                Point2D.Float p2 = spine.get(index);
                Point2D.Float p1 = spine.get(index - 1);
                float level = (spineWidth.get(index)).floatValue();
                Point2D.Float pIn = rightAngle(p1, p2, level);
                curveTo(pIn);
            }
            // Close the shape
            closePath();
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
        lastPoint = new Point2D.Float(p.x, p.y);
        penShape = true;
    }

    /** Move to the given Point
     * @param p Point to draw a line to
     */
    public void moveTo(Point p) {
        path.moveTo(p.x, p.y);
    }

    /** Move to the given Point
     * @param p Point to draw a line to
     */
    public void moveTo(Point2D.Float p) {
        path.moveTo(p.x, p.y);
    }

    /**
     *  Closes the current subpath by drawing a straight line back to the coordinates of the last moveTo
     */
    public void closePath() {
        path.closePath();
        pathClosed = true;
    }

    //////////////////////////////////////////////////////////////
    // TRANSFORM FUNCTIONS
    //////////////////////////////////////////////////////////////
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
        return lastPoint;
    }

    /** 
     * Set the last point for this shape
     * Useful when creating a duplicate object
     * @param lastPt   The last point
     */
    public void setLastPoint(Point2D.Float lastPt) {
        this.lastPoint = lastPt;
    }

    /**
     * Get the color of this shape
     * @return The color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the color of this shape
     * @param color The color
     */
    public void setColor(Color color) {
        this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    /**
     * Get the Paint to draw this shape
     * @return  GradientPaint if available else Color
     */
    Paint getPaint() {
        if (this.gradientPaint != null) {
            return this.gradientPaint;
        } else {
            return this.color;
        }
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
        setColor(this.color);
    }

    /**
     * Set a color with alpha directly
     * @param color
     */
    public void setAlphaColor(Color color) {
        this.color = color;
    }

    /**
     * Get the style of this shape
     * @return  The style of this shape as {@link AlcConstants#STYLE_STROKE} or {@link AlcConstants#STYLE_FILL}
     */
    public int getStyle() {
        return style;
    }

    /**
     * Set the style of this shape
     * @param style  The style of this shape either {@link AlcConstants#STYLE_STROKE} or {@link AlcConstants#STYLE_FILL}
     */
    public void setStyle(int style) {
        this.style = style;
    }

    /** 
     * Get this shapes Gradient 
     * @return  The gradient
     */
    public GradientPaint getGradientPaint() {
        return gradientPaint;
    }

    /** 
     * Set this shapes gradient
     * @param gradientPaint The gradient
     */
    public void setGradientPaint(GradientPaint gradientPaint) {
        this.gradientPaint = gradientPaint;
    }

    /**
     * Get the line width of this shape
     * @return  The line width
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /** 
     * Set the line width of this shape
     * @param lineWidth The line width
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    /** 
     * Check if line smoothing is on or off 
     * @return boolean for line smoothing
     */
    public static boolean isLineSmoothing() {
        return lineSmoothing;
    }

    /** 
     * Set the line smoothing variable for AlcShape
     * @param lineSmoothing boolean to set line smoothing on or off
     */
    public static void setLineSmoothing(boolean lineSmoothing) {
        AlcShape.lineSmoothing = lineSmoothing;
    }

    /** 
     * Get the bounds of this shape 
     * @return Rectangle representing the shapes bounds
     */
    public Rectangle getBounds() {
        return this.path.getBounds();
    }

    /**
     * Return if the path has been closed or not
     * @return  True if the path has been closed else false
     */
    public boolean isPathClosed() {
        return pathClosed;
    }

    void setPathClosed(boolean pathClosed) {
        this.pathClosed = pathClosed;
    }

    /** 
     * Return if this shape has been created with pen strokes or not
     * @return True if this shape has been created with pen strokes or else false
     */
    public boolean isPenShape() {
        return penShape;
    }

    void setPenShape(boolean penShape) {
        this.penShape = penShape;
    }

    /** 
     * Return if this shape uses a spine or not
     * @return True if the path uses a spine else false
     */
    public boolean hasSpine() {
        return spine != null;
    }

    /** 
     * Get the spine (used for variable width lines) of this shape
     * @return  An arraylist containing the spine
     */
    public ArrayList<Point2D.Float> getSpine() {
        return spine;
    }

    /** 
     * Set the spine (used for variable width lines) of this shape
     * @param spine  An arraylist containing the new spine
     */
    public void setSpine(ArrayList<Point2D.Float> spine) {
        this.spine = spine;
    }

    /** 
     * Get the spine width (used for variable width lines) of this shape
     * @return  An arraylist containing the spine width
     */
    public ArrayList<Float> getSpineWidth() {
        return spineWidth;
    }

    /** 
     * Set the spine width (used for variable width lines) of this shape
     * @param spineWidth  An arraylist containing the new spine width
     */
    public void setSpineWidth(ArrayList<Float> spineWidth) {
        this.spineWidth = spineWidth;
    }

    /**
     * Return a simple list of x,y points from this AlcShape object
     * @return  ArrayList<Point2D.Float> containing x,y points
     */
    public ArrayList<Point2D.Float> getPoints() {
        PathIterator pathIterator = path.getPathIterator(null);
        float[] points = new float[6];
        int allocate = (totalPoints > 0) ? totalPoints : 1000;
        ArrayList<Point2D.Float> list = new ArrayList<Point2D.Float>(allocate);

        while (!pathIterator.isDone()) {

            switch (pathIterator.currentSegment(points)) {
                case PathIterator.SEG_MOVETO:
                    list.add(new Point2D.Float(points[0], points[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    list.add(new Point2D.Float(points[0], points[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    list.add(new Point2D.Float(points[2], points[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    list.add(new Point2D.Float(points[4], points[5]));
                    break;
            }
            pathIterator.next();
        }
        return list;
    }

    //////////////////////////////////////////////////////////////
    // UTILITY
    //////////////////////////////////////////////////////////////
    /** Calculate a right angle perpendicular to the two given points at the given distance
     * 
     * @param p1        The first point
     * @param p2        The second point
     * @param distance  The distance away from the two points
     * @return          The point at a perpendicular right angle
     */
    public static Point2D.Float rightAngle(Point2D.Float p1, Point2D.Float p2, double distance) {
        // Calculate the angle between the last point and the new point
        double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x) - MATH_HALF_PI;
        // Convert the polar coordinates to cartesian
        double x = p1.x + (distance * Math.cos(angle));
        double y = p1.y + (distance * Math.sin(angle));
        return new Point2D.Float((float) x, (float) y);
    }

    /** Get the sort index */
    public int getSortIndex() {
        return sortIndex;
    }

    /** Set the sort index */
    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }



    //////////////////////////////////////////////////////////////
    // CLONE STUFF
    //////////////////////////////////////////////////////////////
    /**
     * 'Deep' Clone this object using the existing style/color etc.. values
     * @return An new cloned object of this shape
     */
    @Override
    public Object clone() {
        //Deep copy
        AlcShape tempShape = new AlcShape(this.path, this.color, this.alpha, this.style, this.lineWidth);
        cloneAttributes(tempShape);
        return tempShape;
    }

    /** 
     * A custom clone that adds a new GeneralPath to the shape
     * while keeping all of the style infomation.
     * Note that this function does not clone the spine.
     * 
     * @param tempPath  A GeneralPath to be added to the AlcShape
     * @return          The cloned shape
     */
    public AlcShape customClone(GeneralPath tempPath) {
        //Deep copy
        AlcShape tempShape = new AlcShape(tempPath);
        cloneAttributes(tempShape);
        return tempShape;
    }

    /** 
     * A custom clone that adds a new spine (variable width line) to the shape the creates the path 
     * while keeping all of the style infomation.
     * 
     * @param spine         The spine of the shape
     * @param spineWidth    The width of the spine
     * @return              The cloned shape
     */
    public AlcShape customClone(ArrayList<Point2D.Float> spine, ArrayList<Float> spineWidth) {
        //Deep copy
        AlcShape tempShape = new AlcShape();
        tempShape.setSpine(spine);
        tempShape.setSpineWidth(spineWidth);
        tempShape.createSpine();
        cloneAttributes(tempShape);
        return tempShape;
    }

    /** Clone other attributes of this shape */
    private void cloneAttributes(AlcShape tempShape) {
        tempShape.setAlpha(this.alpha);
        tempShape.setStyle(this.style);
        tempShape.setLineWidth(this.lineWidth);
        tempShape.setTotalPoints(this.totalPoints);
        tempShape.setLastPoint(this.lastPoint);
        tempShape.setPathClosed(this.pathClosed);
        tempShape.setPenShape(this.penShape);
        if (this.gradientPaint != null) {
            GradientPaint gp = new GradientPaint(this.gradientPaint.getPoint1(), this.gradientPaint.getColor1(), this.gradientPaint.getPoint2(), this.gradientPaint.getColor2());
            tempShape.setGradientPaint(gp);
        }
    }
}
