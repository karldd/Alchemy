package alchemy;
import processing.core.PApplet;

import java.awt.Point;
import java.util.Vector;

public class Line{
    
    // Minimum space between points
    int space = 0;
    // When to start smoothing the lines
    int curving = 7;
    PApplet parent; // The parent PApplet that we will render ourselves onto
    
    Vector shape;
    //int[] col;
    
    public Line(PApplet p){
        parent = p;
        //col = colour;
        shape = new Vector();
    }
    
    void draw(){
        //parent.fill(col[0], col[1], col[2], col[3]);
        //parent.noStroke();
        parent.beginShape();
        for(int i = 1; i < shape.size(); i++) {
            Point ppt = (Point)shape.get(i-1);
            Point pt = (Point)shape.get(i);
            // Calculate the movement since the last point was added
            int movement = parent.abs(pt.x - ppt.x) + parent.abs(pt.y - ppt.y);
            if (movement > curving) {
                // When there is more movement (and thus fewer points) smooth the line to avoid kinks
                parent.curveVertex(pt.x, pt.y);
            } else {
                // Draw normal point to point line when movement is slow
                parent.vertex(pt.x, pt.y);
            }
        }
        parent.endShape();
    }
    
    void addPoint(int x, int y, int px, int py) {
        // If there is movement then add another point
        int movement = parent.abs(x - px) + parent.abs(y - py);
        if(movement > space) {
            shape.add(new Point(x, y));
        }
    }
    
    
}