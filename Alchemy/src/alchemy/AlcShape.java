/**
 * AlcShape.java - General shape container
 *
 * Created on November 15, 2007, 5:48 PM
 *
 */

package alchemy;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class AlcShape {
    
    // Minimum space between points
    int space = 5;
    PApplet root;
    PGraphics canvas;
    
    ArrayList<Point> points;
    int colour;
    
    boolean endLine = false;
    Point end = new Point();
    
    
    public AlcShape(PApplet root, PGraphics canvas, Point pt){
        init(root, canvas, pt, 0x000000);
    }
    
    public AlcShape(PApplet root, PGraphics canvas, Point pt, int colour){
        init(root, canvas, pt, colour);
    }
    
    public void init(PApplet root,  PGraphics canvas, Point pt, int c){
        this.root = root;
        this.canvas = canvas;
        points = new ArrayList<Point>(1000);
        points.ensureCapacity(1000);
        points.add(pt);
        this.colour = c;
    }
    
    public void draw(){
        
        //root.fill(colour);
        
        
        Point pt1 = (Point)points.get(0);
        
        //root.beginShape();
        //root.vertex(pt1.x, pt1.y);
        
        for(int i = 1; i < points.size(); i++) {
            //for(int i = 1; i < points.size(); i++) {
            //Point ppt = (Point)points.get(i-1);
            Point ppt = (Point)points.get(i-1);
            Point pt = (Point)points.get(i);
            
            //int movement = root.abs(pt.x - ppt.x) + root.abs(pt.y - ppt.y);
            //root.println(movement);
            
            //if(movement > 25){
            //root.curveVertex(pt.x, pt.y);
            root.line(ppt.x, ppt.y, pt.x, pt.y);
            
            //} else {
            // root.vertex(pt.x, pt.y);
            //}
        }
        /*
        if (endLine){
            root.vertex(end.x, end.y);
        }
         */
        
        //root.endShape();
        
    }
    
    public void drag(Point pt) {
        // Get the last point
        Point ppt = (Point)points.get(points.size()-1);
        
        // If there is movement then add another point
        int movement = root.abs(pt.x - ppt.x) + root.abs(pt.y - ppt.y);
        
        // If drawing a small mark or the start of a mark
        if(points.size() < space){
            
            // Draw a points if there is movement
            if(movement > 0){
                points.add(pt);
                
                /*
                canvas.beginDraw();
                canvas.line(ppt.x, ppt.y, pt.x, pt.y);
                canvas.endDraw();
                 */
            }
        } else{
            
            if(movement > space) {
                points.add(pt);
                
                /*
                canvas.beginDraw();
                canvas.line(ppt.x, ppt.y, pt.x, pt.y);
                canvas.endDraw();
                 */
                
                endLine = false;
            } else {
                end = pt;
                endLine = true;
            }
            
        }
    }
    
    public void release(Point pt) {
        if(endLine){
            points.add(pt);
            //end.x = x;
            //end.y = y;
        }
        root.println(points.size());
    }
    
    public int getColour(){
        return colour;
    }
    
    public void setColour(int c){
        colour = c;
    }
    
}
