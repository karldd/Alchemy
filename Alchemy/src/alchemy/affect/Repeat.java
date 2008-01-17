/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007 Karl D.D. Willis
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
package alchemy.affect;

import alchemy.AlcModule;
import alchemy.AlcShape;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

/**
 * Repeat a given shape on the canvas at a given rate
 * @author Karl D.D. Willis
 */
public class Repeat extends AlcModule {

    // Timing
    private long mouseDelayGap = 150;
    private boolean mouseFirstRun = true;
    private long mouseDelayTime;
    private boolean mouseDown = false;
    //
    //private int activeShape = -1;
    public Repeat() {

    }

    public void setup() {

    }

    public void deselect() {

    }

    public void reselect() {

    }

    private void repeatShape(Point pt, int activeShape) {
        AlcShape originalShape = (AlcShape) canvas.shapes.get(activeShape);
        // Make a completely new shape
        AlcShape shape = (AlcShape) originalShape.clone();
        GeneralPath path = shape.getPath();
        Rectangle bounds = path.getBounds();
        //Point centre = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        
        Point offset = new Point(pt.x - bounds.x, pt.y - bounds.y);
        //System.out.println(offset);
        //Point finalOffset = new Point(offset.x - bounds.width, offset.y - bounds.height);
        Point finalOffset = new Point(offset.x - bounds.width/2, offset.y - bounds.height/2);
        // TODO - Adjust the offset so the shape repeats correctly

        AffineTransform moveTransform = new AffineTransform();
        moveTransform.translate(finalOffset.x, finalOffset.y);
        GeneralPath movedPath = (GeneralPath) path.createTransformedShape(moveTransform);
        shape.setPath(movedPath);
        canvas.shapes.add(shape);

        //GeneralPath randomisedShape = randomise(shape.getShape(), currentLoc);
        //shape.setPath(randomisedShape);
        canvas.redraw();
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

            repeatShape(p, currentActiveShape);

        // Outside a shape
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
                    //System.out.println(e.getPoint());
                    mouseInside(e.getPoint());
                }
            }
        }
    }
}
