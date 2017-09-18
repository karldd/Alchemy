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
 */
package org.alchemy.create;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

/**
 * DetachShapes
 * @author Karl D.D. Willis
 */
public class DetachShapes extends AlcModule {

    private double x = 100;
    private double y = 100;
    private double angle1 = 0;
    private int distance = 100;
    //private Point oldP;
    private boolean newPath;
    private AlcToolBarSubSection subToolBarSection;

    public DetachShapes() {

    }

    @Override
    protected void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void reselect() {
        // Add this modules toolbar to the main ui
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {

        subToolBarSection = new AlcToolBarSubSection(this);

        // Tolerance Slider
        final AlcSubSlider distanceSlider = new AlcSubSlider("Distance", 0, 300, distance);
        distanceSlider.setToolTipText("Adjust the drawing distance");
        distanceSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!distanceSlider.getValueIsAdjusting()) {
                            distance = distanceSlider.getValue();

                        }
                    }
                });
        subToolBarSection.add(distanceSlider);

    }

//    private static int getCursorSpeed(Point p1, Point p2) {
//        int diffX = Math.abs(p1.x - p2.x);
//        int diffY = Math.abs(p1.y - p2.y);
//        return diffX + diffY;
//    }
    @Override
    public void mousePressed(MouseEvent e) {
//        Point p = e.getPoint();
//        canvas.createShapes.add(new AlcShape(p));
//        canvas.redraw();
        newPath = true;
    //oldP = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        //int speed = 100 - (getCursorSpeed(p, oldP));
        //int speed = 100;
        //System.out.println(speed);

        double dx = p.x - x;
        double dy = p.y - y;
        angle1 = Math.atan2(dy, dx);
        x = p.x - (Math.cos(angle1) * distance);
        y = p.y - (Math.sin(angle1) * distance);

        Point newPt = new Point((int) x, (int) y);

        if (newPath) {
            canvas.createShapes.add(new AlcShape(newPt));
            canvas.redraw();
            //oldP = p;
            newPath = false;
        } else {
            if (canvas.hasCreateShapes()) {
                canvas.getCurrentCreateShape().curveTo(newPt);
                canvas.redraw();
            //oldP = p;
            }
        }
//        double xx = x + (segLength * Math.cos(angle1));
//        double yy = y + (segLength * Math.sin(angle1));

    //segment(x, y, angle1);
    //ellipse(x, y, 20, 20);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canvas.redraw();
        canvas.commitShapes();
    //oldP = null;
    }
}
