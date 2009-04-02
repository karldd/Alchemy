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

    private final Color transparent = new Color(255, 255, 255, 0);

    @Override
    protected void affect() {
        int numOfCreateShapes = canvas.createShapes.size();
        // int shapeTally = shapeCount * numOfCreateShapes;

        for (int i = 0; i < numOfCreateShapes; i++) {
            AlcShape shape = canvas.createShapes.get(i);
            GeneralPath path = shape.getPath();
            PathIterator iterator = path.getPathIterator(null);

            float x1 = 0;
            float y1 = 0;
            Point2D p2 = path.getCurrentPoint();
            float x2 = (float) p2.getX();
            float y2 = (float) p2.getY();

            int numberOfPoints = 0;
            float[] currentPoints = new float[6];

            while (numberOfPoints < 1) {
                switch (iterator.currentSegment(currentPoints)) {
                    case PathIterator.SEG_MOVETO:
                        x1 = currentPoints[0];
                        y1 = currentPoints[1];
                }
                numberOfPoints++;
            }

            GradientPaint gradient = new GradientPaint(x1, y1, canvas.getColour(), x2, y2, transparent);
            shape.setGradientPaint(gradient);
        }
    }
}
