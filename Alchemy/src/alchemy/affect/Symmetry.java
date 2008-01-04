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

    /** Creates a new instance of Symmetry */
    public Symmetry() {
    }

    @Override
    protected void setup() {
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
    protected void incrementShape(AlcShape shape) {

        GeneralPath rawShape = shape.getShape();    // Get the raw shape from the custom class
        AffineTransform reflection = horizontalReflect();  // Get a horizontal transform
        GeneralPath processedShape = (GeneralPath) rawShape.createTransformedShape(reflection);
        // Clone the shape and add the processedShape
        canvas.setTempShape(shape.customClone(processedShape));

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
//        canvas.getCurrentShape().getShape().closePath();
//        canvas.getCurrentShape().getShape().setWindingRule(GeneralPath.WIND_EVEN_ODD);
//        canvas.getTempShape().getShape().closePath();
//        canvas.getTempShape().getShape().setWindingRule(GeneralPath.WIND_NON_ZERO);

        if (canvas.getStyle() == SOLID) {
            //canvas.getCurrentShape().getShape().setWindingRule(GeneralPath.WIND_NON_ZERO);
            //canvas.appendTempShape(false);
            // TODO - Find a way to deal with merging or appending with a SOLID shape and transparency on
            if (canvas.getTempShape() != null) {
                System.out.println("temp shape not null");
                canvas.mergeTempShape();
            }
        } else {
            // Join this shape to the current one (no connecting)
            canvas.appendTempShape(false);
        }
    }
}
