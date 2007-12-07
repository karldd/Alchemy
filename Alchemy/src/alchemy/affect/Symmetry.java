/**
 * Symmetry.java
 *
 * Created on December 2, 2007, 9:55 AM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.affect;

import alchemy.*;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

public class Symmetry extends AlcModule implements AlcConstants {

    private AlcShape tempShape;

    /** Creates a new instance of Symmetry */
    public Symmetry() {
    }

    @Override
    public void setup() {
    }

    @Override
    public AlcShape processShape(AlcShape shape) {

        GeneralPath rawShape = shape.getShape();    // Get the raw shape fromt the custom class
        Area shapeArea = new Area(rawShape);        // Make this into an area

        AffineTransform reflection = horizontalReflect();   // Get a horizontal transform
        Area reflectShape = shapeArea.createTransformedArea(reflection);   // Apply the transform

        shapeArea.add(reflectShape);    // Union the two together

        GeneralPath processedShape = new GeneralPath((Shape) shapeArea);
        shape.setShape(processedShape);

        return shape;
    }

    @Override
    public void incrementShape(AlcShape shape) {

        GeneralPath rawShape = shape.getShape();    // Get the raw shape from the custom class
        AffineTransform reflection = horizontalReflect();  // Get a horizontal transform
        GeneralPath processedShape = (GeneralPath) rawShape.createTransformedShape(reflection);
        // Make a new shape from the given shape
        tempShape = new AlcShape(processedShape, shape.getColour(), shape.getAlpha(), shape.getStyle(), shape.getLineWidth());
        //tempShape.setShape(processedShape); // Add the transformed shape

        canvas.addTempShape(tempShape);

    }

    /* Returns a horizontal reflection transform based on the current window width */
    private AffineTransform horizontalReflect() {
        AffineTransform reflect = new AffineTransform();    // Make a transform to horizontalReflect it
        reflect.scale(-1, 1);                             // Reflect it using a negative scale
        reflect.translate(root.getWindowSize().width * -1, 0);  // Move the reflection into place
        return reflect;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canvas.commitTempShape();
    }
}
