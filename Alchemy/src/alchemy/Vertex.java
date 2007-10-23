package alchemy;

import processing.core.PApplet;

import java.awt.Point;
import java.util.Vector;

public class Vertex{
    
    // Minimum space between points
    int space = 5;
    PApplet root; // The root PApplet that we will render ourselves onto
    
    Vector<Object> line;
    int inc = 0;
    
    boolean endLine = false;
    Point end = new Point();
    
    public Vertex(PApplet r, int x, int y){
        root = r;
        line = new Vector<Object>();
        line.ensureCapacity(100);
        line.add(new Point(x, y));
        //root.println(x + " " + y);
    }
    
    public void draw(){
        root.noFill();
        root.stroke(0);
        
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
        root.redraw();
    }
    
    public void release(int x, int y) {
        if(endLine){
            line.add(new Point(x, y));
            //end.x = x;
            //end.y = y;
            root.redraw();
        }
    }
    
    public void remove(){
        root.unregisterDraw(this);
    }
}