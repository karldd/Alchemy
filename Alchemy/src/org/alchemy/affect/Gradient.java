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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import org.alchemy.core.*;

/**
 *
 * Gradient.java
 */
public class Gradient extends AlcModule {

    private Color transparent;

    @Override
    protected void affect() {

        //System.out.println(canvas.createShapes.size() + " " + canvas.affectShapes.size());

        // int shapeTally = shapeCount * numOfCreateShapes;
        Color bgColour = canvas.getBackgroundColour();
        transparent = new Color(bgColour.getRed(), bgColour.getGreen(), bgColour.getBlue(), 0);

        for (int i = 0; i < canvas.createShapes.size(); i++) {
            setGradient(canvas.createShapes.get(i));
        }
        for (int i = 0; i < canvas.affectShapes.size(); i++) {
            setGradient(canvas.affectShapes.get(i));
        }
    }

    private void setGradient(AlcShape shape) {

        GeneralPath path = shape.getPath();
        PathIterator iterator = path.getPathIterator(null);

        Point2D p1 = new Point2D.Float(0, 0);
        Point2D p2 = new Point2D.Float(0, 0);
        int halfPoints = shape.getTotalPoints() / 2;
        //System.out.println(halfPoints);
        boolean closed = shape.isPathClosed();
        int numberOfPoints = 0;
        float[] currentPoints = new float[6];
        int currentPointType;

        search:
        while (!iterator.isDone()) {
            currentPointType = iterator.currentSegment(currentPoints);
            
            switch (currentPointType) {
                case PathIterator.SEG_MOVETO:
                    p1 = new Point2D.Float(currentPoints[0], currentPoints[1]);
                    // If this is not closed then break here and set the last point
                    if (!closed) {
                        break search;
                    }
                    break;
                case PathIterator.SEG_LINETO:
                    if (closed) {
                        // Find the mid point if it is closed
                        if (numberOfPoints >= halfPoints) {
                            p2 = new Point2D.Float(currentPoints[0], currentPoints[1]);
                            break search;
                        }
                    }
                    break;
                case PathIterator.SEG_QUADTO:
                    if (closed) {
                        // Find the mid point if it is closed
                        if (numberOfPoints >= halfPoints) {
                            p2 = new Point2D.Float(currentPoints[0], currentPoints[1]);
                            break search;
                        }
                    }
                    break;
            }
            iterator.next();
            numberOfPoints++;
        }

        // If this path is not closed then use the last point
        if (!closed) {
            p2 = path.getCurrentPoint();
        }

        GradientPaint gradient = new GradientPaint(p1, shape.getColour(), p2, transparent);
        shape.setGradientPaint(gradient);
    }
}
