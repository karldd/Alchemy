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

public class InverseShapes extends AlcModule {

    private boolean moveTo = true;
    private boolean mouseUp = true;

    /**
     * Creates a new instance of InverseShapes
     */
    public InverseShapes() {
    }

    @Override
    protected void setup() {
    }

    @Override
    protected void reselect() {
        mouseUp = true;
        moveTo = true;
    }

    @Override
    protected void deselect() {
    }

    @Override
    protected void cleared() {
        moveTo = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseUp = false;
        canvas.redraw();
        canvas.commitShapes();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseUp) {
            Point p = e.getPoint();
            if (moveTo) {
                canvas.createShapes.add(makeShape(p));
                moveTo = false;
            } else {
                // Need to test if it is null incase the shape has been auto-cleared
                if (canvas.getCurrentCreateShape() != null) {
                    canvas.getCurrentCreateShape().addCurvePoint(p);
                    canvas.redraw();
                }
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseUp = true;
        moveTo = true;
    }

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }
}
