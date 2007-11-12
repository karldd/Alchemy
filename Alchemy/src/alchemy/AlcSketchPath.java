package alchemy;

import processing.core.PApplet;

import java.awt.Point;
import java.util.Vector;

public class AlcSketchPath{
    
    // Minimum space between points
    int space = 5;
    PApplet root; // The root PApplet that we will render ourselves onto
    
    Vector<Object> line;
    int colour;
    int inc = 0;
    
    boolean endLine = false;
    Point end = new Point();
    
    public AlcSketchPath(PApplet r, int x, int y){
        init(r, x, y, 0x000000);
    }
    
    public AlcSketchPath(PApplet r, int x, int y, int c){
        init(r, x, y, c);
    }
    
    public void init(PApplet r, int x, int y, int c){
        root = r;
        line = new Vector<Object>();
        line.ensureCapacity(100);
        line.add(new Point(x, y));
        colour = c;
    }
    
    public void draw(){
        
        //root.fill(colour);
        root.beginShape();
        
        Point pt1 = (Point)line.get(0);
        root.vertex(pt1.x, pt1.y);
        
        for(int i = 1; i < line.size(); i++) {
            Point pt = (Point)line.get(i);
            root.curveVertex(pt.x, pt.y);
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
        // Get the last point added
        Point pt = (Point)line.get(line.size()-1);
        
        // If there is movement then add another point
        int movement = root.abs(x - pt.x) + root.abs(y - pt.y);
        
        if(line.size() < space){
            if(movement > 0){
                line.add(new Point(x, y));
            }
        } else{
            
            if(movement > space) {
                line.add(new Point(x, y));
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