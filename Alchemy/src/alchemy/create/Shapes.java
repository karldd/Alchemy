/**
 * Shape.java
 *
 * Created on December 6, 2007, 10:28 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.create;

import alchemy.AlcModule;
import alchemy.AlcShape;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class Shapes extends AlcModule{
    
    /**
     * Creates a new instance of Shapes
     */
    public Shapes() {
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        canvas.addShape( makeShape(p) );
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        canvas.getCurrentShape().addCurvePoint(p);
        canvas.applyAffects();
    }
    
    @Override
    public void mouseReleased(MouseEvent e){
        Point p = e.getPoint();
        canvas.getCurrentShape().addLastPoint(p);
        canvas.applyAffects();
    }
    
    private AlcShape makeShape(Point p){
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }
    
    
}
