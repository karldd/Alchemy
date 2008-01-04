/*
 *   Part of the Alchemy project - http://al.chemy.org
 * 
 *   Copyright (c) 2007 Karl D.D. Willis
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General
 *   Public License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA  02111-1307  USA
 */
package alchemy.create;

import alchemy.AlcModule;
import alchemy.AlcShape;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * MedianShapes
 * 
 * 
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
    // TODO - Change name, implement so it works with other affects
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

            canvas.addShape(makeShape(p));

            controlShapePoints.add(p);

        } else {
            Point controlOrigin = controlShapePoints.get(0);

            pointCount = 0;

            originPoint = p;

            originDifference = new Point(p.x - (p.x - controlOrigin.x) / 2, p.y - (p.y - controlOrigin.y) / 2);
            //System.out.println(originDifference + " " + controlOrigin + " " + p);
            canvas.addShape(makeShape(originDifference));

            // Set the current point into memory for nexttime
            controlShapePointsBuffer.add(pointCount, p);

            canvas.setTempShape(makeShape(p));
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it null incase the shape has been auto-cleared
        if (canvas.getCurrentShape() != null) {

            if (captureControlGesture) {

                canvas.getCurrentShape().addCurvePoint(p);
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
                    canvas.getCurrentShape().addCurvePoint(tempPoint);

                } else {
                //canvas.getCurrentShape().addCurvePoint(p);
                }
                controlShapePointsBuffer.add(pointCount, p);

                canvas.getTempShape().addCurvePoint(p);
                canvas.redraw();
            }


        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it null incase the shape has been auto-cleared
        if (canvas.getCurrentShape() != null) {

            if (captureControlGesture) {
                captureControlGesture = false;

                canvas.getCurrentShape().addLastPoint(p);
                canvas.redraw();

                controlShapePoints.add(p);

            } else {
                controlShapePoints = controlShapePointsBuffer;
                controlShapePointsBuffer = new ArrayList<Point>(1000);
                //canvas.getCurrentShape().addLastPoint(p);


                canvas.getTempShape().addLastPoint(p);
                canvas.commitTempShape();
                canvas.redraw();
            //controlShapePoints.removeRange(pointCount, 100);
            }


        }
    }

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }

    // KEY EVENTS
    /*
    @Override
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
