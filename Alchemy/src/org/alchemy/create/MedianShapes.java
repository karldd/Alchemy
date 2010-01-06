/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
 * 
 *  Alchemy is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Alchemy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.alchemy.create;

import org.alchemy.core.AlcModule;
import org.alchemy.core.AlcShape;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * MedianShapes
 * @author Karl D.D. Willis 
 */
public class MedianShapes extends AlcModule {

    /** Capture a gesture or not */
    private boolean captureControlGesture = true;
    /** Array list to store the points of the control shape */
    private ArrayList<Point> controlShapePoints = new ArrayList<Point>(1000);
    private ArrayList<Point> controlShapePointsBuffer = new ArrayList<Point>(1000);
    /** Origin Point - when redrawing, where the mouse starts from */
    Point originPoint;
    /** Origin Difference - when redrawing, how far between the origin of the current shape 
     *  and the origin of the control shape  */
    Point originDifference;
    /** Counter for the points made when redrawing */
    int pointCount = 0;

    public MedianShapes() {
    }

    @Override
    protected void cleared() {
        captureControlGesture = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        if (captureControlGesture) {

            /*
            // Clear the arraylist if it has content
            if (controlShapeTiming != null) {
            controlShapeTiming.clear();
            }
             */
            if (controlShapePoints != null) {
                controlShapePoints.clear();
            }
            //startTime = System.currentTimeMillis();
            canvas.createShapes.add(new AlcShape(p));

            controlShapePoints.add(p);

        } else {
            Point controlOrigin = controlShapePoints.get(0);
            pointCount = 0;
            originPoint = p;

            originDifference = new Point(p.x - (p.x - controlOrigin.x) / 2, p.y - (p.y - controlOrigin.y) / 2);
            //System.out.println(originDifference + " " + controlOrigin + " " + p);
            canvas.createShapes.add(new AlcShape(originDifference));

            // Set the current point into memory for nexttime
            controlShapePointsBuffer.add(pointCount, p);

            canvas.createShapes.add(new AlcShape(p));
        }

        canvas.redraw();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it null incase the shape has been auto-cleared
        if (canvas.hasCreateShapes()) {

            if (captureControlGesture) {

                canvas.getCurrentCreateShape().curveTo(p);
                canvas.redraw();

                controlShapePoints.add(p);

            } else {
                pointCount++;
                // If there are enough recorded points
                if (controlShapePoints.size() > pointCount) {
                    Point controlPoint = controlShapePoints.get(pointCount);
                    // Difference between this point and the parallel control point
                    int xOffset = p.x - (p.x - controlPoint.x) / 2;
                    int yOffset = p.y - (p.y - controlPoint.y) / 2;

                    //int x = p.x + xOffset;
                    //int y = p.y + yOffset;
                    Point tempPoint = new Point(xOffset, yOffset);
                    //controlPoint.x += p.x + xOffset;
                    //controlPoint.y += p.y + yOffset;
                    AlcShape thisShape = canvas.createShapes.get(canvas.createShapes.size() - 2);
                    thisShape.curveTo(tempPoint);

                }
                controlShapePointsBuffer.add(pointCount, p);
                canvas.getCurrentCreateShape().curveTo(p);
                canvas.redraw();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it null incase the shape has been auto-cleared
        if (canvas.hasCreateShapes()) {

            if (captureControlGesture) {
                captureControlGesture = false;

                canvas.getCurrentCreateShape().lineTo(p);
                //canvas.commitTempShape(tempShapeIndex1);
                canvas.redraw();
                canvas.commitShapes();

                controlShapePoints.add(p);

            } else {
                controlShapePoints = controlShapePointsBuffer;
                controlShapePointsBuffer = new ArrayList<Point>(1000);
                //canvas.getCurrentShape().addLastPoint(p);


                //canvas.createShapes.get(canvas.createShapes.size() - 2).addLastPoint(p);
                //canvas.commitTempShape(tempShapeIndex2);
                canvas.redraw();
                canvas.commitShapes();
            //controlShapePoints.removeRange(pointCount, 100);
            }


        }
    }

    // KEY EVENTS
    /*
    public void keyReleased(KeyEvent e) {
    int keyCode = e.getKeyCode();
    switch (keyCode) {
    case KeyEvent.VK_SPACE:
    System.out.println("Capture true");
    captureControlGesture = true;
    canvas.clear();
    break;
    }
    }
     */
}
