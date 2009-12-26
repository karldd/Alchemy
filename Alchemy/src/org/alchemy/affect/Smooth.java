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
package org.alchemy.affect;

import java.awt.Point;
import java.awt.event.*;
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
        AlcShape smoothedShape = smoothShape(shape.getPoints());
        if (repeat) {
            canvas.shapes.add(smoothedShape);
        } else {
            shape.setPath(smoothedShape.getPath());
        }
        canvas.redraw(true);
    }

    private AlcShape smoothShape(ArrayList<Point2D.Float> points) {
        //shape.setPath(null);
        
        Point2D.Float firstPoint = points.get(0);
        AlcShape shape = new AlcShape(new Point2D.Float(firstPoint.x, firstPoint.y));
        for (int i = 0; i < points.size(); i++) {
            if (i == points.size() - 2) {
                Point2D.Float pt = points.get(i);
                shape.addCurvePoint(new Point2D.Float(pt.x, pt.y));
            } else if (i == points.size() - 1) {
                Point2D.Float pt = points.get(i);
                shape.addLinePoint(new Point2D.Float(pt.x, pt.y));

            } else {
                Point2D.Float p0 = points.get(i);
                Point2D.Float p1 = points.get(i + 1);
                Point2D.Float p2 = points.get(i + 2);

                float x = p0.x * 0.25F + p1.x * 0.5F + p2.x * 0.25F;
                float y = p0.y * 0.25F + p1.y * 0.5F + p2.y * 0.25F;
                shape.addCurvePoint(new Point2D.Float(x, y));
            }
        }
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
