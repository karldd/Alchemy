/**
 * InverseShapes.java
 *
 * Created on December 6, 2007, 10:34 AM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.create;

import alchemy.AlcShape;
import alchemy.AlcModule;
import java.awt.Point;
import java.awt.event.MouseEvent;


public class InverseShapes extends AlcModule{
    
    private AlcShape tempShape;
    private boolean moveTo = true;
    private boolean mouseUp = true;
    
    /**
     * Creates a new instance of InverseShapes
     */
    public InverseShapes() {
    }
    
    @Override
    public void setup(){
        canvas.setShapeCreation(false);
    }
    
    @Override
    public void reselect(){
        mouseUp = true;
        moveTo = true;
    }
    
    @Override
    public void deselect(){
        canvas.setShapeCreation(true);
        canvas.commitTempShape();
    }
    
    @Override
    public void cleared(){
        moveTo = true;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        mouseUp = false;
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if(mouseUp){
            Point p = e.getPoint();
            if(moveTo){
                canvas.addShape( makeShape(p) );
                moveTo = false;
            } else {
                canvas.getCurrentShape().addCurvePoint(p);
                canvas.applyAffects();
                canvas.redraw();
            }
        }
        
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseUp = true;
        moveTo = true;
    }
    
    private AlcShape makeShape(Point p){
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }
    
}
