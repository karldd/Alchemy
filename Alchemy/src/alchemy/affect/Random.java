/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007 Karl D.D. Willis
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
package alchemy.affect;

import alchemy.AlcModule;
import alchemy.AlcShape;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 * Random Alchemy Module
 * @author Karl D.D. Willis 
 */
public class Random extends AlcModule {

    private float noisiness = 0.1F;
    private float noiseScale = 0.0F;
    private float scale,  halfScale;
    // Timing
    private long mouseDelayGap = 250;
    private boolean mouseFirstRun = true;
    private long mouseDelayTime;
    private boolean mouseDown = false;
    //
    private int activeShape = -1;
    private int proximity = 5;

    public Random() {

    }

    public void setup() {

    }

    public void deselect() {

    }

    public void reselect() {

    }

    private void randomiseShape(Point currentLoc) {
        //noisiness = root.math.random(-0.01F, 0.1F);

        scale = 100F;
        halfScale = scale / 2;
        AlcShape shape = (AlcShape) canvas.shapes.get(activeShape);
        GeneralPath randomisedShape = randomise(shape.getPath(), currentLoc);
        shape.setPath(randomisedShape);
        canvas.redraw();

    //return shape;

    }

    private GeneralPath randomise(GeneralPath shape, Point p) {

        GeneralPath newShape = new GeneralPath();
        PathIterator cut = shape.getPathIterator(null);
        float[] cutPts = new float[6];
        int cutType;

        while (!cut.isDone()) {
            cutType = cut.currentSegment(cutPts);

            switch (cutType) {
                case PathIterator.SEG_MOVETO:
                    newShape.moveTo(cutPts[0], cutPts[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    if (closeBy(p.x, p.y, (int) cutPts[0], (int) cutPts[1])) {
                        newShape.lineTo(mess(cutPts[0]), mess(cutPts[1]));
                    } else {
                        newShape.lineTo(cutPts[0], cutPts[1]);
                    }
                    break;
                case PathIterator.SEG_QUADTO:
                    if (closeBy(p.x, p.y, (int) cutPts[2], (int) cutPts[3])) {
                        newShape.quadTo(mess(cutPts[0]), mess(cutPts[1]), mess(cutPts[2]), mess(cutPts[3]));
                    } else {
                        newShape.quadTo(cutPts[0], cutPts[1], cutPts[2], cutPts[3]);
                    }
                    break;
                case PathIterator.SEG_CUBICTO:
                    // Randomising the curves tends to generate errors and unresposiveness
                    if (closeBy(p.x, p.y, (int) cutPts[2], (int) cutPts[3])) {
                        newShape.curveTo(mess(cutPts[0]), mess(cutPts[1]), mess(cutPts[2]), mess(cutPts[3]), mess(cutPts[4]), mess(cutPts[5]));
                    } else {
                        newShape.curveTo(cutPts[0], cutPts[1], cutPts[2], cutPts[3], cutPts[4], cutPts[5]);
                    }
                    break;
                case PathIterator.SEG_CLOSE:
                    newShape.closePath();
                    break;
            }
            cut.next();
        }

        return newShape;

    }

    /** Find out if the two points are closeby */
    private boolean closeBy(int x1, int y1, int x2, int y2) {
        int xgap = Math.abs(x1 - x2);
        if (xgap < proximity) {
            return true;
        }
        int ygap = Math.abs(y1 - y2);
        if (ygap < proximity) {
            return true;
        }
        return false;
    }

    /** Apply Perlin noise to the given float */
    private float mess(float f) {
        //noiseScale += noisiness;
        //float n = (root.math.noise(noiseScale) * scale) - halfScale;
        float n = root.math.random(-10F, 10F);
        //n = n * 0.5F;
        //System.out.println(n);
        return n + f;
    }

    // TODO - make an interface to allow setting of noisines and scale
    /** Set the level of variation */
    private void setNoisiness(float f) {
        noisiness = f;
    }

    /** Set the scale on which to apply the noise */
    private void setScale(float f) {
        scale = f;
        halfScale = scale / 2;
    }

    private void mouseInside(Point p) {
        int currentActiveShape = -1;
        for (int i = 0; i < canvas.shapes.size(); i++) {
            AlcShape thisShape = (AlcShape) canvas.shapes.get(i);
            GeneralPath currentPath = thisShape.getPath();
            Rectangle bounds = currentPath.getBounds();
            if (bounds.contains(p)) {
                currentActiveShape = i;
            }
        }
        // Inside a shape
        if (currentActiveShape >= 0) {
            // Filter out repeat calls
            //if (currentActiveShape != activeShape) {
            activeShape = currentActiveShape;
            randomiseShape(p);
        //}
        // Outside a shape
        } else {
            activeShape = -1;
        //stopExpand();
        }
    }

    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    //            canvas.setCurrentShape(randomiseShape(currentShape));
    }

    public void mouseMoved(MouseEvent e) {
        if (!mouseDown) {
            // Dispatch checking for intersection at a slow rate
            if (mouseFirstRun) {
                mouseDelayTime = System.currentTimeMillis();
                mouseInside(e.getPoint());
                mouseFirstRun = false;
            } else {
                if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
                    mouseDelayTime = System.currentTimeMillis();
                    mouseInside(e.getPoint());
                }
            }
        }
    }
}

