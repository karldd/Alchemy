package org.alchemy.create;

import java.awt.Point;
import java.awt.event.MouseEvent;
import org.alchemy.core.*;

public class PitchShapes extends AlcModule implements AlcConstants {

        @Override
    public void setup() {
        // This function is called when the module is first selected in the menu
        // It will only be called once, so is useful for doing stuff like
        // loading interface elements into the menu bar etc...
    }

    @Override
    protected void cleared() {
        // This function is called when the canvas is cleared
        // You might sometimes need to use it
        // if you are say counting the number of shapes
        // and you want to know when to set it back to zero
    }

    @Override
    protected void reselect() {
        // This function is called when the module is reselected in the menu
        // i.e. the module is turned off then on again
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // This function is called when the mouse/pen moves
        // It is called A LOT
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // This function is called when the mouse/pen is pressed

        // Here we get the location of the mouse as a point object
        Point p = e.getPoint();

        // You can either access the x & y data from the point object
        // like p.x or p.y
        // Or get it straight from the MouseEvent object
        // like e.getX() or e.getY()


        // Add a shape to the 'createShapes' array
        //
        // AlcShape is a custom object used contain shape information
        // Here we create a new AlcShape object
        // and feed it the point to start a new shape
        //
        // The canvas is the Alchemy canvas where everything is drawn
        //
        // createShapes is an ArrayList of shapes
        // Basically a long list of shapes that get drawn to the canvas
        // So we add our new shape to that list
        canvas.createShapes.add(new AlcShape(p));

        // Tell the canvas to redraw
        // All the shapes are then drawn to the canvas
        canvas.redraw();

        // The println function is useful for debugging
        // It can print messages to the output/console window!
        System.out.println("Mouse Pressed!");
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // This function is called when the mouse/pen is dragged
        Point p = e.getPoint();
        // So we need to retreive the shape we last added
        // so we can add to it
        //
        // First we need to test if it is still there
        // incase the shape has been cleared
        if (canvas.getCurrentCreateShape() != null) {
            // Now we add a 'curve point' to the current shape
            // This gives the shapes more natural curves
            canvas.getCurrentCreateShape().addCurvePoint(p);
            canvas.redraw();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // This function is called when the mouse/pen is released
        Point p = e.getPoint();
        // Again, need to test if it is null incase the shape has been cleared
        if (canvas.getCurrentCreateShape() != null) {
            // This time we call the addLastPoint() function
            // Which will close the shape
            canvas.getCurrentCreateShape().addLastPoint(p);
        }
        // Last Redraw
        canvas.redraw();
        // To keep things speedy shapes are committed to an image buffer
        // So we tell the canvas this shape is complete and it can be committed
        // It is much faster to copy an image to the screen, than to redraw
        // hundreds of shapes with transparency to the canvas each time
        canvas.commitShapes();
    }

}
