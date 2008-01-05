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
import java.awt.geom.PathIterator;
import java.util.ArrayList;

public class Symmetry extends AlcModule implements AlcConstants {

    private int[] tempShapeIndex;
    private boolean firstTime = true;
    
    private AffineTransform horizontalReflection = new AffineTransform();

    /** Creates a new instance of Symmetry */
    public Symmetry() {
    }

    @Override
    protected void setup() {
    }

    @Override
    protected void commited() {
        // When the temp shapes are committed, reset the index
        tempShapeIndex = null;
        firstTime = true;
    }

    @Override
    protected AlcShape processShape(AlcShape shape) {
        GeneralPath rawShape = shape.getShape();    // Get the raw shape fromt the custom class
        Area shapeArea = new Area(rawShape);        // Make this into an area
        updateHorizontalReflection();   // Update the horizontal transform
        Area reflectShape = shapeArea.createTransformedArea(horizontalReflection);   // Apply the transform
        shapeArea.add(reflectShape);    // Union the two together
        GeneralPath processedShape = new GeneralPath((Shape) shapeArea);
        shape.setShape(processedShape);
        return shape;
    }

    @Override
    protected ArrayList<AlcShape> incrementShape(ArrayList<AlcShape> shapes) {

//        if (firstTime) {
//            tempShapeIndex = new int[shapes.size()];
//        }

        int length = shapes.size();

        for (int i = 0; i < length; i++) {
            AlcShape shape = shapes.get(i);
            GeneralPath singlePath = new GeneralPath();
            PathIterator cut = shape.getShape().getPathIterator(null);
            float[] pts = new float[6];
            int ptType;
            int segCount = 0;

            while (!cut.isDone()) {
                ptType = cut.currentSegment(pts);
                // Count the number of new segments
                if (ptType == PathIterator.SEG_MOVETO) {
                    segCount++;
                //System.out.println("SegCount " + segCount);
                }
                // Only add the first segment
                if (segCount == 1) {
                    switch (ptType) {
                        case PathIterator.SEG_MOVETO:
                            singlePath.moveTo(pts[0], pts[1]);
                            break;
                        case PathIterator.SEG_LINETO:
                            singlePath.lineTo(pts[0], pts[1]);
                            break;
                        case PathIterator.SEG_QUADTO:
                            singlePath.quadTo(pts[0], pts[1], pts[2], pts[3]);
                            break;
                        case PathIterator.SEG_CUBICTO:
                            singlePath.curveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
                            break;
                        case PathIterator.SEG_CLOSE:
                            singlePath.closePath();
                            break;
                        }
                }
                cut.next();
            }
            // Create the reflected path
            updateHorizontalReflection();  // Get a horizontal transform
            GeneralPath reflectedPath = (GeneralPath) singlePath.createTransformedShape(horizontalReflection);
            // Add the first reflectedPath to the single path
            singlePath.append(reflectedPath, false);
            // Set the shape in the original arraylist
            shapes.get(i).setShape(singlePath);
        //System.out.println("inc called");

        // Use the tempShapeIndex to keep track of which shapes we are messing with
        // If it is null, initialise it with
//            if (firstTime) {
//                tempShapeIndex[i] = canvas.getTempShapesSize();
//                // Add a clone
//                System.out.println("ADD " + tempShapeIndex[i]);
//                canvas.addTempShape(shape.customClone(processedShape));
//            } else {
//                // Replace a clone
//                System.out.println("REPLACE " + tempShapeIndex[i]);
//                canvas.setTempShape(tempShapeIndex[i], shape.customClone(processedShape));
//            }
        }

        return shapes;
//        if (firstTime) {
//            firstTime = false;
//        }
    }

    /* Updates the horizontal reflection transform based on the current window width */
    private void updateHorizontalReflection() {
        horizontalReflection.scale(-1, 1);                             // Reflect it using a negative scale
        horizontalReflection.translate(root.getWindowSize().width * -1, 0);  // Move the reflection into place
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        canvas.getCurrentShape().getShape().closePath();
//        canvas.getCurrentShape().getShape().setWindingRule(GeneralPath.WIND_EVEN_ODD);
//        canvas.getTempShape().getShape().closePath();
//        canvas.getTempShape().getShape().setWindingRule(GeneralPath.WIND_NON_ZERO);

//        if (canvas.getStyle() == SOLID) {
//            //canvas.getCurrentShape().getShape().setWindingRule(GeneralPath.WIND_NON_ZERO);
//            //canvas.appendTempShape(false);
//            // TODO - Find a way to deal with merging or appending with a SOLID shape and transparency on
//            if (canvas.getTempShape() != null) {
//                System.out.println("temp shape not null");
//                canvas.mergeTempShape();
//            }
//        } else {
//            // Join this shape to the current one (no connecting)
//            canvas.appendTempShape(false);
//        }
    }
}
