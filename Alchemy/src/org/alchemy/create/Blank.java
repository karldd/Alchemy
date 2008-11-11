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
import java.awt.event.MouseEvent;
import org.alchemy.core.*;

/**
 *
 * Blank.java
 */
public class Blank extends AlcModule {

    public Blank() {
        // This should be left blank, use setup() instead
    }

    @Override
    public void setup() {
        // Called when the module is first selected
    }

    @Override
    protected void cleared() {
        // Called when the canvas is cleared
    }

    @Override
    protected void reselect() {
        // Called when the module is reselected
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        // Add a shape the 'createShapes' array 
        canvas.createShapes.add(new AlcShape(p));
        // Tell the canvas to redraw
        // All the shapes are then drawn to the canvas
        canvas.redraw();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it is null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            // Add a 'curve point' to the current shape
            // This gives the shapes more natural curves
            canvas.getCurrentCreateShape().addCurvePoint(p);
            canvas.redraw();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it is null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            canvas.getCurrentCreateShape().addLastPoint(p);
        }
        // Last Redraw
        canvas.redraw();
        // To keep things speedy shapes are committed to a image buffer
        // So tell the canvas this shape is complete and it can be committed
        canvas.commitShapes();

    }
}
