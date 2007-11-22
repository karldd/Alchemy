/**
 * AlcShape.java - General shape container
 *
 * Created on November 15, 2007, 5:48 PM
 *
 */

package alchemy;

import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import java.awt.geom.GeneralPath;

public class AlcShape {
    
    GeneralPath shape;
    
    /** Colour of this shape */
    Color colour = Color.BLACK;
    
    /** Alpha of this shape */
    int alpha = 255;
    
    /** Style of this shape - (1) LINE or (2) SOLID FILL */
    int style = 1;
    
    /** Line Weight if the style is line */
    int lineWidth = 1;
    
    /** Store the last point */
    Point lastPt;
    
    /** For drawing smaller marks - draw lines until x points have been made */
    int startPoints = 5;
    
    /** Minimum distance until points are added */
    int minMovement = 5;
    
    /** Keep track of the number of points added */
    int totalPoints = 0;
    
    public AlcShape(Point p){
        setup(p, colour, alpha, style, lineWidth);
    }
    
    public AlcShape(Point p, Color aColour, int aAlpha, int aStyle, int aLineWidth){
        setup(p, aColour, aAlpha, aStyle, aLineWidth);
    }
    
    public void setup(Point p, Color aColour, int aAlpha, int aStyle, int aLineWidth){
        
        //System.out.println("Setup");
        
        /** Create the shape and move to the first point */
        shape = new GeneralPath();
        shape.moveTo(p.x, p.y);
        totalPoints++;
        lastPt = p;
        
        //System.out.println(p.x + " " + p.y);
        
        alpha = aAlpha;
        setColour(aColour);
        style = aStyle;
        lineWidth = aLineWidth;
        
    }
    
    public void drag(Point p) {
        
        // At the start just draw lines so smaller marks can be made
        if(totalPoints < startPoints) {
            
            shape.lineTo(p.x,  p.y);
            savePoints(p);
            
        } else {
            
            // Movement since the last point was drawn
            int movement =  Math.abs(p.x - lastPt.x) + Math.abs(p.y - lastPt.y);
            
            // Test to see if this point has moved far enough
            if(movement > minMovement){
                
                // New control point value
                Point pt = new Point();
                
                // Average the points
                pt.x = (lastPt.x + p.x) >> 1;
                pt.y = (lastPt.y + p.y) >> 1;
                
                //System.out.println("Last Point: " + lastPt.x + " " + lastPt.y + " - Edited Point: " + pt.x + " " + pt.y + " - Original Point: " + p.x + " " + p.y);
                
                // Add the Quadratic curve - control point x1, y1 and actual point x2, y2
                shape.quadTo(lastPt.x, lastPt.y, pt.x,  pt.y);
                savePoints(p);
                
            }
        }
    }
    
    public void savePoints(Point p){
        
        // Increment the total number of points
        totalPoints++;
        
        // Set the current point to the (original) last point value - not the altered pt value
        lastPt = p;
        
    }
    
    public void release(Point xy) {
        shape.lineTo(xy.x, xy.y);
    }
    
    
    // ALCSHAPE Interfaces
    public GeneralPath getShape(){
        return shape;
    }
    
    public void setShape(GeneralPath aShape){
        shape = aShape;
    }
    
    public Color getColour(){
        return colour;
    }
    
    public void setColour(Color c){
        colour = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
    
    public int getAlpha(){
        return alpha;
    }
    
    public void setAlpha(int a){
        alpha = a;
    }
    
    public int getStyle(){
        return style;
    }
    
    public void setStyle(int s){
        style = s;
    }
    
    public int getLineWidth(){
        return lineWidth;
    }
    
    public void setLineWidth(int l){
        lineWidth = l;
    }
    
}
