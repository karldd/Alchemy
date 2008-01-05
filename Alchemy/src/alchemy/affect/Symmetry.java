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
    // Reflect it using a negative scale

    }

    @Override
    protected void commited() {
    }

    @Override
    protected void incrementShape() {

        for (int i = 0; i < canvas.createShapes.size(); i++) {
            AlcShape shape = canvas.createShapes.get(i);
            GeneralPath reflectedPath = (GeneralPath) shape.getShape().createTransformedShape(getHorizontalReflection());
            // 
            if (canvas.affectShapes.size() < canvas.createShapes.size()) {
                canvas.affectShapes.add(shape.customClone(reflectedPath));
            } else {
                canvas.affectShapes.set(i, shape.customClone(reflectedPath));
            }
        }

    }

    /* Updates the horizontal reflection transform based on the current window width */
    private AffineTransform getHorizontalReflection() {
        AffineTransform horizontalReflection = new AffineTransform();
        horizontalReflection.scale(-1, 1);
        horizontalReflection.translate(root.getWindowSize().width * -1, 0);  // Move the reflection into place
        return horizontalReflection;
    }
}
