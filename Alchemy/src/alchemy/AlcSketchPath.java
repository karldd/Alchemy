package alchemy;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.awt.Point;
import java.util.ArrayList;

public class AlcSketchPath{
    
    // Minimum space between points
    int space = 5;
    PApplet root; // The root PApplet that we will render ourselves onto
    PGraphics canvas;
    
    ArrayList<Point> line;
    int colour;
    int inc = 0;
    
    boolean endLine = false;
    Point end = new Point();
    
    public AlcSketchPath(PApplet r, PGraphics g, int x, int y){
        init(r, g, x, y, 0x000000);
    }
    
    public AlcSketchPath(PApplet r, PGraphics g, int x, int y, int c){
        init(r, g, x, y, c);
    }
    
    public void init(PApplet r,  PGraphics g, int x, int y, int c){
        root = r;
        this.canvas = g;
        line = new ArrayList<Point>(1000);
        line.ensureCapacity(1000);
        line.add(new Point(x, y));
        colour = c;
    }
    
    public void drawPDF(){
        
        //root.fill(colour);
        root.beginShape();
        
        Point pt1 = (Point)line.get(0);
        root.vertex(pt1.x, pt1.y);
        
        for(int i = 1; i < line.size(); i++) {
            //Point ppt = (Point)line.get(i-1);
            Point pt = (Point)line.get(i);
            
            //int movement = root.abs(pt.x - ppt.x) + root.abs(pt.y - ppt.y);
            //root.println(movement);
            
            //if(movement > 25){
            root.curveVertex(pt.x, pt.y);
            //} else {
            // root.vertex(pt.x, pt.y);
            //}
        }
        if (endLine){
            root.vertex(end.x, end.y);
        }
        
        root.endShape();
        
    }
    
    public void drag(int x, int y) {
        drag(x, y, true);
    }
    
    public void drag(int x, int y, boolean b) {
        // Get the last bunch of points
        Point pt = (Point)line.get(line.size()-1);
        //Point pt1 = (Point)line.get(line.size()-2);
        //Point pt2 = (Point)line.get(line.size()-3);
        
        // If there is movement then add another point
        int movement = root.abs(x - pt.x) + root.abs(y - pt.y);
        
        // If drawing a small mark or the start of a mark
        if(line.size() < space){
            
            
            // Draw a line if there is movement
            if(movement > 0){
                line.add(new Point(x, y));
                
                canvas.beginDraw();
                canvas.line(pt.x, pt.y, x, y);
                canvas.endDraw();
            }
        } else{
            
            if(movement > space) {
                line.add(new Point(x, y));
                
                //canvas.curve(pt2.x, pt2.y, pt1.x, pt1.y, pt.x, pt.y, x, y);
                
                //root.println("FAST " + root.random(10));
                
                canvas.beginDraw();
                canvas.line(pt.x, pt.y, x, y);
                canvas.endDraw();
                
                endLine = false;
                //root.println(inc++);
                // root.redraw();
            } else {
                end.x = x;
                end.y = y;
                endLine = true;
            }
        }
        if(b) root.redraw();
    }
    
    public void release(int x, int y) {
        release(x, y, true);
    }
    
    public void release(int x, int y, boolean b) {
        if(endLine){
            line.add(new Point(x, y));
            //end.x = x;
            //end.y = y;
        }
        if(b) root.redraw();
        root.println(line.size());
    }
    
    public int getColour(){
        return colour;
    }
    
    public void setColour(int c){
        colour = c;
    }
    
    public void remove(){
        //root.unregisterDraw(this);
    }
    
    
}