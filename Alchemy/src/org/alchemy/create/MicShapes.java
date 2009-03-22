/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2009 Karl D.D. Willis
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

import org.alchemy.core.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MicShapes.java
 * @author  Karl D.D. Willis
 */
public class MicShapes extends AlcModule implements AlcConstants {

    private AlcMicrophone micIn;
    private Point2D.Float lastPt;
    private float volume;
    private AlcToolBarSubSection subToolBarSection;
    private boolean shake = false;
    private ArrayList<Point2D.Float> points = new ArrayList<Point2D.Float>(1000);
    private ArrayList<Float> levels = new ArrayList<Float>(1000);

    /** Creates a new instance of MicShapes */
    public MicShapes() {
    }

    @Override
    protected void setup() {
        points.ensureCapacity(1000);
        // Create a new MicInput Object with a buffer of 10
        micIn = new AlcMicrophone(2);
        micIn.start();
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void deselect() {
        micIn.stop();
        micIn = null;
    }

    @Override
    protected void reselect() {
        micIn = new AlcMicrophone(2);
        micIn.start();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void cleared() {
        points.clear();
        levels.clear();
        lastPt = null;
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Draw mode button
        AlcSubToggleButton drawModeButton = new AlcSubToggleButton("Draw Mode", AlcUtil.getUrlPath("drawmode.png", getClassLoader()));
        drawModeButton.setToolTipText("Change the draw mode between fatten and shake style");

        drawModeButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        shake = !shake;
                    }
                });
        subToolBarSection.add(drawModeButton);


        // Volume Slider
        int initialSliderValue = 50;
        final float levelOffset = 0.02F;
        volume = initialSliderValue * levelOffset;
        final AlcSubSlider volumeSlider = new AlcSubSlider("Volume", 0, 100, initialSliderValue);
        volumeSlider.setToolTipText("Adjust the microphone input volume");
        volumeSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!volumeSlider.getValueIsAdjusting()) {
                            int value = volumeSlider.getValue();
                            volume = value * levelOffset;
                        //System.out.println(volume);
                        }
                    }
                });
        subToolBarSection.add(volumeSlider);
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
        canvas.createShapes.add(new AlcShape(p));
        lastPt = new Point2D.Float(p.x, p.y);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D.Float p = canvas.getPenLocation();
        AlcShape currentShape = canvas.getCurrentCreateShape();
        // Need to test if it is null incase the shape has been auto-cleared
        if (currentShape != null) {
            if (canvas.isPenLocationChanged()) {
                float micLevel = (float) micIn.getMicLevel() * 2;

                // SHAKE MODE
                if (shake) {
                    byte[] buffer = micIn.getBuffer();
                    //int[] samples = micIn.getSamples();
                    //Point2D.Float pt = rightAngle(p, lastPt,  micIn.getMicLevel() * 2);
                    double thisLevel;
                    if (buffer[0] == 0) {
                        thisLevel = micLevel;
                    } else {
                        thisLevel = buffer[0] * micLevel;
                    }

                    Point2D.Float pt = rightAngle(p, lastPt, thisLevel);
                    currentShape.addCurvePoint(pt);

                // FATTEN MODE
                } else {
                    // Set the min level to 1
                    if (micLevel < 0) {
                        micLevel = 1;
                    }
                    if (points.size() > 0) {
                        if (p.distance(lastPt) > 3) {
                            levels.add(new Float(micLevel));
                            points.add(p);
                            makeBlob(currentShape);
                        }
                    } else {
                        levels.add(new Float(micLevel));
                        points.add(p);
                        makeBlob(currentShape);
                    }
                }
                canvas.redraw();
                lastPt = new Point2D.Float(p.x, p.y);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point2D.Float p = canvas.getPenLocation();
        AlcShape currentShape = canvas.getCurrentCreateShape();
        // Need to test if it is null incase the shape has been auto-cleared
        if (currentShape != null) {
            if (shake) {
                currentShape.addLastPoint(p);
            } else {
                points.clear();
                levels.clear();
            }
            canvas.redraw();
            canvas.commitShapes();
        }
    }

    private Point2D.Float rightAngle(Point2D.Float p1, Point2D.Float p2, double distance) {
        double adjustedDistance = distance * volume;
        // Calculate the angle between the last point and the new point
        double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x) - MATH_HALF_PI;
        //System.out.println(angle);
        // Convert the polar coordinates to cartesian
        double x = p1.x + (adjustedDistance * Math.cos(angle));
        double y = p1.y + (adjustedDistance * Math.sin(angle));

        return new Point2D.Float((float) x, (float) y);
    }
}
