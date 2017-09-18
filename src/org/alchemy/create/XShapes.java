/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2010 Karl D.D. Willis
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
 * GNU General Public License for more details.
 */
package org.alchemy.create;

import org.alchemy.core.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * XShapes
 * @author Karl D.D. Willis
 */
public class XShapes extends AlcModule implements AlcConstants {

    private Point oldP;
    private int multiplier = 25;
    private AlcToolBarSubSection subToolBarSection;
//    private boolean freeform = true;
    private ArrayList<Integer> averageSpeed = new ArrayList<Integer>();
    private int counter = 0;

    public XShapes() {

    }

    @Override
    protected void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);
//
//        // Shake/Fatten button
//        AlcSubToggleButton lineStyleButton = new AlcSubToggleButton("Style", AlcUtil.getUrlPath("linestyle.png", getClassLoader()));
//        lineStyleButton.setToolTipText("Select the style of line");
//
//        lineStyleButton.addActionListener(
//                new ActionListener() {
//
//                    public void actionPerformed(ActionEvent e) {
//                        freeform = !freeform;
//                    }
//                });
//        subToolBarSection.add(lineStyleButton);


        final AlcSubSlider speedSlider = new AlcSubSlider("Distance", 0, 100, 50);
        speedSlider.setToolTipText("Change the amount cursor movement is sped up by");
        speedSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!speedSlider.getValueIsAdjusting()) {
                            int value = speedSlider.getValue();
                            multiplier = value / 2;
                        //System.out.println(multiplier);
                        }
                    }
                });
        subToolBarSection.add(speedSlider);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        canvas.createShapes.add(new AlcShape(p));
        canvas.redraw();
        oldP = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        Point p = e.getPoint();
        if (oldP == null) {
            oldP = p;
//            double speed = getAverageCursorSpeed(p, oldP);
//            Point pt = getAngle(p, oldP, speed);
//            canvas.createShapes.add(makeShape(pt));
        }


        // Need to test if it is null incase the shape has been auto-cleared

        if (canvas.hasCreateShapes()) {
            double speed = getAverageCursorSpeed(p, oldP);
            double points = canvas.getCurrentCreateShape().getTotalPoints() * 0.01;
            //System.out.println(points);
            Point pt = getAngle(p, oldP, points + speed / 2);
//            if (freeform) {
//                canvas.getCurrentCreateShape().addCurvePoint(pt);
//            } else {
            canvas.getCurrentCreateShape().lineTo(pt);
//            }

            canvas.redraw();
            oldP = p;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        oldP = null;
        canvas.commitShapes();
    }

    private double getAverageCursorSpeed(Point p1, Point p2) {

        int diffX = Math.abs(p1.x - p2.x);
        int diffY = Math.abs(p1.y - p2.y);
        //int rand = (int) root.math.random(2);

        int speed = diffX + diffY;
        int thisSlot = counter % 10;
        averageSpeed.add(thisSlot, new Integer(speed));

        counter++;

        //averageSpeed[counter % averageSpeed.length] = speed;
        return mean(averageSpeed);
    //return speed;
    }

    private double mean(ArrayList<Integer> list) {
        int sum = 0;  // sum of all the elements
        for (int i = 0; i < list.size(); i++) {
            Integer thisInteger = list.get(i);
            sum += thisInteger.intValue();
        }
        return sum / list.size();
    }

    private Point getAngle(Point p2, Point p1, double distance) {
        double adjustedDistance = distance * multiplier;
        // Calculate the angle between the last point and the new point
        //double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x) - Math.sin(counter * 0.01);
        //double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x) - counter * 0.001;
        double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x) - MATH_HALF_PI;
        //System.out.println(angle);

        //double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x);
        //System.out.println(angle);
        // Conver the polar coordinates to cartesian
        double x = p1.x + (adjustedDistance * Math.cos(angle));
        double y = p1.y + (adjustedDistance * Math.sin(angle));

        return new Point((int) x, (int) y);
    }
}
