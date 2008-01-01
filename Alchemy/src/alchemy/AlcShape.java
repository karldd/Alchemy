/**
 * AlcShape.java - General shape container
 *
 * Created on November 15, 2007, 5:48 PM
 *
 */
package alchemy;

import java.awt.Point;
import java.awt.Color;

import java.awt.geom.GeneralPath;

public class AlcShape implements AlcConstants {

    private GeneralPath shape;
    // SHAPE DEFAULTS
    /** Colour of this shape */
    private Color colour = Color.BLACK;
    /** Alpha of this shape */
    private int alpha = 255;
    /** Style of this shape - (1) LINE or (2) SOLID FILL */
    private int style = LINE;
    /** Line Weight if the style is line */
    private int lineWidth = 1;
    /** Store the last point */
    private Point lastPt;
    /** For drawing smaller marks - draw lines until x points have been made */
    private int startPoints = 5;
    /** Minimum distance until points are added */
    private int minMovement = 5;
    /** Keep track of the number of points added */
    private int totalPoints = 0;

    public AlcShape(Point p) {
        setupPoint(p);
        setupAttributes(colour, alpha, style, lineWidth);
    }

    public AlcShape(Point p, Color colour, int alpha, int style, int lineWidth) {
        setupPoint(p);
        setupAttributes(colour, alpha, style, lineWidth);
    }

    public AlcShape() {
        setupBlank();
        setupAttributes(colour, alpha, style, lineWidth);
    }

    public AlcShape(GeneralPath gp) {
        setupShape(gp);
        setupAttributes(colour, alpha, style, lineWidth);
    }

    public AlcShape(GeneralPath gp, int style) {
        setupShape(gp);
        setupAttributes(colour, alpha, style, lineWidth);
    }

    public AlcShape(GeneralPath gp, Color colour, int alpha, int style, int lineWidth) {
        setupShape(gp);
        setupAttributes(colour, alpha, style, lineWidth);
    }

    public void setupBlank() {
        // Create an empty shape
        shape = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
        //shape.setWindingRule(GeneralPath.WIND_NON_ZERO);
    }

    public void setupPoint(Point p) {
        // Create the shape and move to the first point
        shape = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
        // TODO - get Jacobs opinion on this winding rule
        //shape.setWindingRule(GeneralPath.WIND_NON_ZERO);
        //shape.setWindingRule(GeneralPath.WIND_EVEN_ODD);

        shape.moveTo(p.x, p.y);
        totalPoints++;
    }

    public void setupShape(GeneralPath gp) {
        // Add the shape
        this.shape = gp;
        shape.setWindingRule(GeneralPath.WIND_NON_ZERO);
    }

    public void setupAttributes(Color colour, int alpha, int style, int lineWidth) {
        this.alpha = alpha;
        setColour(colour);
        this.style = style;
        this.lineWidth = lineWidth;
    }

    public void addCurvePoint(Point p) {

        // At the start just draw lines so smaller marks can be made
        if (totalPoints < startPoints) {

            shape.lineTo(p.x, p.y);
            savePoints(p);

        } else {

            // Movement since the last point was drawn
            int movement = Math.abs(p.x - lastPt.x) + Math.abs(p.y - lastPt.y);

            // Test to see if this point has moved far enough
            if (movement > minMovement) {

                // New control point value
                Point pt = new Point();

                // Average the points
                pt.x = (lastPt.x + p.x) >> 1;
                pt.y = (lastPt.y + p.y) >> 1;

                //System.out.println("Last Point: " + lastPt.x + " " + lastPt.y + " - Edited Point: " + pt.x + " " + pt.y + " - Original Point: " + p.x + " " + p.y);

                // Add the Quadratic curve - control point x1, y1 and actual point x2, y2
                shape.quadTo(lastPt.x, lastPt.y, pt.x, pt.y);
                savePoints(p);

            }
        }
    }

    public void addLinePoint(Point p) {

        // At the start just draw lines so smaller marks can be made
        if (totalPoints < startPoints) {

            shape.lineTo(p.x, p.y);
            savePoints(p);

        } else {

            // Movement since the last point was drawn
            int movement = Math.abs(p.x - lastPt.x) + Math.abs(p.y - lastPt.y);

            // Test to see if this point has moved far enough
            if (movement > minMovement) {

                shape.lineTo(p.x, p.y);
                savePoints(p);

            }
        }
    }

    public void savePoints(Point p) {

        // Increment the total number of points
        totalPoints++;

        // Set the current point to the (original) last point value - not the altered pt value
        lastPt = p;

    }

    public void addLastPoint(Point p) {
        shape.lineTo(p.x, p.y);
    }

    // ALCSHAPE Interfaces
    public GeneralPath getShape() {
        return shape;
    }

    public void setShape(GeneralPath shape) {
        this.shape = shape;
    }

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

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }
}
