/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2009 Karl D.D. Willis
 * 
 * Alchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.alchemy.create;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

/**
 *
 * PressureShapes.java
 */
public class PressureShapes extends AlcModule {

    private AlcToolBarSubSection subToolBarSection;
    private ArrayList<Point2D.Float> points = new ArrayList<Point2D.Float>(1000);
    private ArrayList<Float> levels = new ArrayList<Float>(1000);
    private int pressureAmount;
    private int startPressure = 25;

    @Override
    public void setup() {
        points.ensureCapacity(1000);
        pressureAmount = startPressure;
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void cleared() {
        points.clear();
        levels.clear();
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);


        final int pressureMin = 1;
        final int pressureMax = 200;

        final AlcSubSpinner pressureSpinner = new AlcSubSpinner(
                "Pressure",
                "Control the amount of pressure",
                startPressure,
                pressureMin,
                pressureMax,
                1);

        pressureSpinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        pressureAmount = pressureSpinner.getValue();
                    }
                });

//        subToolBarSection.add(drawModeButton);
//
//
//        // Volume Slider
//        int initialSliderValue = 50;
//        final float levelOffset = 0.02F;
//        volume = initialSliderValue * levelOffset;
//        final AlcSubSlider volumeSlider = new AlcSubSlider("Volume", 0, 100, initialSliderValue);
//        volumeSlider.setToolTipText("Adjust the microphone input volume");
//        volumeSlider.addChangeListener(
//                new ChangeListener() {
//
//                    public void stateChanged(ChangeEvent e) {
//                        if (!volumeSlider.getValueIsAdjusting()) {
//                            int value = volumeSlider.getValue();
//                            volume = value * levelOffset;
//                        //System.out.println(volume);
//                        }
//                    }
//                });
        subToolBarSection.add(pressureSpinner);
    }

    private void makeBlob(AlcShape shape) {

        // Reset the shape and create the first point
        Point2D.Float p0 = points.get(0);
        shape.setPoint(p0);

        // Draw the outer points
        for (int i = 1; i < points.size(); i++) {
            Point2D.Float p2 = points.get(i - 1);
            Point2D.Float p1 = points.get(i);
            float level = (levels.get(i)).floatValue();
            Point2D.Float pOut = rightAngle(p1, p2, level);
            shape.addCurvePoint(pOut);
        }
        // Draw the inner points
        for (int j = 1; j < points.size(); j++) {
            int index = (points.size() - j);
            Point2D.Float p2 = points.get(index);
            Point2D.Float p1 = points.get(index - 1);
            float level = (levels.get(index)).floatValue();
            Point2D.Float pIn = rightAngle(p1, p2, level);
            shape.addCurvePoint(pIn);
        }
        // Close the shape going back to the first point
        shape.addLastPoint(p0);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point2D.Float p = canvas.getPenLocation();
        addData(p);
        canvas.createShapes.add(new AlcShape(p));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        AlcShape currentShape = canvas.getCurrentCreateShape();
        // Need to test if it is null incase the shape has been auto-cleared
        if (currentShape != null) {
            if (canvas.isPenLocationChanged()) {
                Point2D.Float p = canvas.getPenLocation();
                //System.out.println(p);
                if (points.size() > 0) {
                    Point2D.Float lastPt = points.get(points.size() - 1);
                    double distance = p.distance(lastPt);
                    if (distance > 3) {
                        addData(p);
                        makeBlob(currentShape);
                        canvas.redraw();
                    }
                }
            } else {
                //System.out.println("No Change: " + canvas.getPenLocation());
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        points.clear();
        levels.clear();
        canvas.redraw();
        canvas.commitShapes();
    }

    private void addData(Point2D.Float p) {
        float pressure = 1;
        if (canvas.getPenType() != PEN_CURSOR) {
            pressure = canvas.getPenPressure() * (float) pressureAmount;
        }
        levels.add(new Float(pressure));
        points.add(p);
    }

    private Point2D.Float rightAngle(Point2D.Float p1, Point2D.Float p2, double distance) {
        //double adjustedDistance = distance;
        // Calculate the angle between the last point and the new point
        double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x) - MATH_HALF_PI;
        //System.out.println(angle);
        // Convert the polar coordinates to cartesian
        double x = p1.x + (distance * Math.cos(angle));
        double y = p1.y + (distance * Math.sin(angle));

        return new Point2D.Float((float) x, (float) y);
    }
}
