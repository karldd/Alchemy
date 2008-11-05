/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2008 Karl D.D. Willis
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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

/**
 *
 * PullShapes.java
 */
public class PullShapes extends AlcModule implements AlcConstants {

    private AlcToolBarSubSection subToolBarSection;
    //
    private AlcShape[] shapes;
    private boolean pathsLoaded = false;    // Timing
    private long mouseDelayGap = 51;
    private boolean mouseFirstRun = true;
    private long mouseDelayTime;
    private boolean mouseDown = false;
//    private int pathNum = 0;
//    //
//    private int baseMaxPoint = 0;
//    private int baseHalfPoint = 0;
//    private int basePointCount = 0;
//    private AlcShape baseShape = null;
//    private Point[] basePoints;
//    //
//    private AlcShape currentShape = null;
//    private Point oldP;
    public PullShapes() {
    }

    @Override
    protected void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
        loadShapes();
    }

    @Override
    protected void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
        loadShapes();
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);


        AlcSubButton directoryButton = new AlcSubButton("Reload", AlcUtil.getUrlPath("reload.png", getClassLoader()));

        directoryButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        loadShapes();
                    }
                });
        subToolBarSection.add(directoryButton);


        // Spacing Slider
        int initialSliderValue = 25;
        final AlcSubSlider spacingSlider = new AlcSubSlider("Spacing", 0, 100, initialSliderValue);
        spacingSlider.setToolTipText("Adjust the spacing interval");
        spacingSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!spacingSlider.getValueIsAdjusting()) {
                            int value = spacingSlider.getValue();
                            mouseDelayGap = 1 + value * 2;
                        }
                    }
                });
        subToolBarSection.add(spacingSlider);
    }

    private void loadShapes() {
        shapes = AlcUtil.loadShapes();
        if (shapes != null) {
            pathsLoaded = true;
        }
    }

    private void addRandomShape(MouseEvent e) {
        int rand = (int) math.random(shapes.length);
        AlcShape movedShape = (AlcShape) shapes[rand].clone();
        Rectangle bounds = movedShape.getBounds();
        int x = e.getX() - (bounds.width >> 1);
        int y = e.getY() - (bounds.height >> 1);
        movedShape.move(x, y);
        movedShape.setupDefaultAttributes();
        canvas.createShapes.add(movedShape);
        canvas.redraw();
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (pathsLoaded) {

            mouseDelayTime = System.currentTimeMillis();
            addRandomShape(e);


//            baseShape = new AlcShape(paths[pathNum]);
//            baseShape.recalculateTotalPoints();
//            baseHalfPoint = baseShape.getTotalPoints() / 2;
//            baseMaxPoint = baseShape.getTotalPoints();
//            basePoints = baseShape.getPoints();
//            
//            System.out.println("baseMaxPoint: "+ baseMaxPoint + " baseHalfPoint : " + baseHalfPoint);
//
//            currentShape = new AlcShape(p);
//            canvas.setCurrentCreateShape(currentShape);
//            oldP = p;

        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (pathsLoaded) {

            if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
                mouseDelayTime = System.currentTimeMillis();
                //System.out.println(e.getPoint());
                addRandomShape(e);
            }



//            Point p = e.getPoint();
//            if (basePointCount < baseHalfPoint) {
//                Point p1 = basePoints[basePointCount];
//                Point p2 = basePoints[baseMaxPoint - basePointCount];
//                float distance = AlcMath.distance(p1.x, p1.y, p2.x, p2.y);
//
//                Point newP = rightAngle(oldP, p, distance);
//                currentShape.addCurvePoint(newP);
//                canvas.setCurrentCreateShape(currentShape);
//
//                basePointCount++;
//            }
//            canvas.redraw();
//            oldP = p;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (pathsLoaded) {
            canvas.commitShapes();
        }
    }

    private Point rightAngle(Point p1, Point p2, double distance) {

        // Calculate the angle between the last point and the new point
        double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x) - HALF_PI;
        //System.out.println(angle);
        // Convert the polar coordinates to cartesian
        double x = p1.x + (distance * Math.cos(angle));
        double y = p1.y + (distance * Math.sin(angle));

        return new Point((int) x, (int) y);
    }
}
