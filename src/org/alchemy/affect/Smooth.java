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
package org.alchemy.affect;

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.alchemy.core.*;

/**
 * Smooth Module
 * @author Karl D.D. Willis 
 */
public class Smooth extends AlcModule {

    private int spacing = 75;
    private long time;
    private boolean repeat = false;
    private AlcToolBarSubSection subToolBarSection;
    private AlcSubToggleButton repeatButton;

    @Override
    protected void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }


    @Override
    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Repeat button
        repeatButton = new AlcSubToggleButton("Repeat", AlcUtil.getUrlPath("repeat.png", getClassLoader()));
        repeatButton.setSelected(repeat);
        repeatButton.setToolTipText("Toggle repeat on/off");

        repeatButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        repeat = !repeat;
                    }
                });
        subToolBarSection.add(repeatButton);

    }

    private void smoothShape(Point currentLoc, int index) {
        AlcShape shape = canvas.shapes.get(index);
        AlcShape smoothedShape = smoothShape(shape.getPoints(), currentLoc);
        if (repeat) {
            canvas.shapes.add(smoothedShape);
        } else {
            shape.setPath(smoothedShape.getPath());
        }
        canvas.redraw(true);
    }

    private AlcShape smoothShape(ArrayList<Point2D.Float> points, Point currentLoc) {
        //shape.setPath(null);

        GeneralPath path = new GeneralPath(GeneralPath.WIND_NON_ZERO, points.size());
        for (int i = 0; i < points.size(); i++) {

            // FIRST
            if (i == 0) {
//                shape.addLinePoint(points.get(i));
                Point2D.Float pt = points.get(i);
                path.moveTo(pt.x, pt.y);

            // LAST
            } else if (i == points.size() - 1) {
//                shape.addLinePoint(points.get(i));
                Point2D.Float pt = points.get(i);
                path.lineTo(pt.x, pt.y);

            } else {
                Point2D.Float p0 = points.get(i - 1);
                Point2D.Float p1 = points.get(i);
                Point2D.Float p2 = points.get(i + 1);

//                if (p0.distance(currentLoc) < 25) {

                // Average the 3 points
                float x = p0.x * 0.25F + p1.x * 0.5F + p2.x * 0.25F;
                float y = p0.y * 0.25F + p1.y * 0.5F + p2.y * 0.25F;

                // Setup for curves
                Point2D.Float pt = new Point2D.Float();
                pt.x = (p0.x + x) / 2F;
                pt.y = (p0.y + y) / 2F;

                // Add the Quadratic curve - control point x1, y1 and actual point x2, y2
                path.quadTo(p0.x, p0.y, pt.x, pt.y);
            }
        }
        AlcShape shape = new AlcShape(path);
        return shape;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!canvas.isPenDown()) {
            if (System.currentTimeMillis() - time >= spacing) {
                int firstShape = -1;
                Point pt = e.getPoint();
                // Loop through from the newest shape and find the first one the mouse is over
                for (int i = canvas.shapes.size() - 1; i >= 0; i--) {
                    AlcShape thisShape = canvas.shapes.get(i);
                    if (thisShape.getPath().contains(pt)) {
                        firstShape = i;
                        break;
                    }
                }
                if (firstShape >= 0) {
                    smoothShape(pt, firstShape);
                }
                time = System.currentTimeMillis();
            }
        }
    }
}
