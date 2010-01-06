/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
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
package org.alchemy.affect;

import org.alchemy.core.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Random Alchemy Module
 * @author Karl D.D. Willis 
 */
public class Random extends AlcModule {

//    private float distortion = 0.1F;
//    private float noiseScale = 0.0F;
//    private float scale;
    // Timing
//    private long mouseDelayGap = 250;
//    private boolean mouseFirstRun = true;
//    private long mouseDelayTime;
    private int initialDistortion = 50;
    private float distortionScaler = 0.15F;
    private float bottomEnd = (initialDistortion * distortionScaler) * -1;
    private float topEnd = initialDistortion * distortionScaler;
    private boolean mouseDown = false;
//
//    private int activeShape = -1;
    private int proximity = 5;
    //
    private AlcToolBarSubSection subToolBarSection;

    public Random() {

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

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Distortion Slider
        final AlcSubSlider distortionSlider = new AlcSubSlider("Distortion", 1, 100, initialDistortion);

        distortionSlider.setToolTipText("Adjust the distortion level");
        distortionSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!distortionSlider.getValueIsAdjusting()) {
                            int value = distortionSlider.getValue();
                            bottomEnd = (value * distortionScaler) * -1;
                            topEnd = value * distortionScaler;
                        }
                    }
                });
        subToolBarSection.add(distortionSlider);

    }

    private void randomiseShape(Point currentLoc, int shapeNumber) {
        AlcShape shape = canvas.shapes.get(shapeNumber);
        GeneralPath randomisedShape = randomise(shape.getPath(), currentLoc);
        shape.setPath(randomisedShape);
        canvas.redraw(true);
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
        //noiseScale += distortion;
        //float n = (root.math.noise(noiseScale) * scale) - halfScale;
        float n = math.random(bottomEnd, topEnd);
        //n = n * 0.5F;
        //System.out.println(n);
        return n + f;
    }

//    protected void affectShapes(int[] activeShapes, Point cursorLocation) {
//        if (!mouseDown) {
//            if (activeShapes != null) {
//                int last = activeShapes[activeShapes.length - 1];
//                randomiseShape(cursorLocation, last);
//            }
//        }
//    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!mouseDown) {
            int firstShape = -1;
            Point pt = e.getPoint();
            // Loop through from the newest shape and find the first one the mouse is over
            for (int i = canvas.shapes.size()-1; i >= 0; i--) {
                AlcShape thisShape = canvas.shapes.get(i);
                if (thisShape.getPath().contains(pt)) {
                    firstShape = i;
                     break;
                }
            }
            if (firstShape >= 0) {
               randomiseShape(pt, firstShape);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }
}

