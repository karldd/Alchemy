/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2008 Karl D.D. Willis
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MicShapes.java
 * @author  Karl D.D. Willis
 */
public class MicShapes extends AlcModule implements AlcConstants {

    private AlcMicrophone micIn;
    private Point lastPt;
    private float volume;
    private AlcSubToolBarSection subToolBarSection;
    private boolean shake = false;
    private ArrayList<Point> points = new ArrayList<Point>(1000);
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
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

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

        Point p0 = points.get(0);
        shape.setPoint(p0);

        // Draw the outer points
        for (int i = 1; i < points.size(); i++) {
            Point p2 = points.get(i - 1);
            Point p1 = points.get(i);
            float level = (levels.get(i)).floatValue();
            Point pOut = rightAngle(p1, p2, level);
            shape.addCurvePoint(pOut);
        }
        //System.out.println("SIZE " + points.size());
        // Draw the inner points
        for (int j = 1; j < points.size(); j++) {
            int index = (points.size() - j);
            //System.out.print(index + " ");
            Point p2 = points.get(index);
            Point p1 = points.get(index - 1);
            float level = (levels.get(index)).floatValue();
            Point pIn = rightAngle(p1, p2, level);
            shape.addCurvePoint(pIn);
        }
    //System.out.println(" ");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        canvas.createShapes.add(new AlcShape(p));
        lastPt = p;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        AlcShape currentShape = canvas.getCurrentCreateShape();
        // Need to test if it is null incase the shape has been auto-cleared
        if (currentShape != null) {
            if (!p.equals(lastPt)) {
                if (shake) {
                    byte[] buffer = micIn.getBuffer();
                    //int[] samples = micIn.getSamples();
                    //Point pt = rightAngle(p, oldP, micIn.getMicLevel());
                    Point pt = rightAngle(p, lastPt, buffer[0]);
                    currentShape.addCurvePoint(pt);
                } else {

                    float thisLevel = (float) micIn.getMicLevel() * 2;
                    levels.add(new Float(thisLevel));
                    points.add(p);
                    makeBlob(currentShape);
                }
                canvas.redraw();
                lastPt = p;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
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

    private Point rightAngle(Point p1, Point p2, double distance) {
        double adjustedDistance = distance * volume;
        // Calculate the angle between the last point and the new point
        double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x) - HALF_PI;
        //System.out.println(angle);
        // Convert the polar coordinates to cartesian
        double x = p1.x + (adjustedDistance * Math.cos(angle));
        double y = p1.y + (adjustedDistance * Math.sin(angle));

        return new Point((int) x, (int) y);
    }
}
