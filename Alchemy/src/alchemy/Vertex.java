package alchemy;

import processing.core.PApplet;

import java.awt.Point;
import java.util.Vector;

public class Vertex{
    
    // Minimum space between points
    int space = 5;
    PApplet parent; // The parent PApplet that we will render ourselves onto
    
    Vector line;
    int inc = 0;
    
    boolean endLine = false;
    Point end = new Point();
    
    public Vertex(PApplet p, int x, int y){
        parent = p;
        line = new Vector();
        line.ensureCapacity(100);
        line.add(new Point(x, y));
        //parent.println(x + " " + y);
    }
    
    public void draw(){
        parent.noFill();
        parent.stroke(0);
        
        parent.beginShape();
        
        Point pt1 = (Point)line.get(0);
        parent.vertex(pt1.x, pt1.y);
        
        for(int i = 1; i < line.size(); i++) {
            Point pt = (Point)line.get(i);
            parent.curveVertex(pt.x, pt.y);
        }
        if (endLine){
            parent.vertex(end.x, end.y);
        }
        
        parent.endShape();
        
    }
    
    public void drag(int x, int y) {
        // Get the last point added
        Point pt = (Point)line.get(line.size()-1);
        
        // If there is movement then add another point
        int movement = parent.abs(x - pt.x) + parent.abs(y - pt.y);
        
        if(line.size() < space){
            if(movement > 0){
                line.add(new Point(x, y));
            }
        } else{
            
            if(movement > space) {
                line.add(new Point(x, y));
                endLine = false;
                //parent.println(inc++);
                // parent.redraw();
            } else {
                end.x = x;
                end.y = y;
                endLine = true;
            }
        }
        parent.redraw();
    }
    
    public void release(int x, int y) {
        if(endLine){
            line.add(new Point(x, y));
            //end.x = x;
            //end.y = y;
            parent.redraw();
        }
    }
    
    public void remove(){
        parent.unregisterDraw(this);
    }
}