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
package alchemy.create;

import alchemy.AlcModule;
import alchemy.AlcShape;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * Shape.java
 * @author  Karl D.D. Willis
 */


public class Shapes extends AlcModule {

    /**
     * Creates a new instance of Shapes
     */
    public Shapes() {
    }

    
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        canvas.createShapes.add(makeShape(p));
        canvas.redraw();
    }

    
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            canvas.getCurrentCreateShape().addCurvePoint(p);
            canvas.redraw();
        }

    }

    
    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            canvas.getCurrentCreateShape().addLastPoint(p);
            canvas.redraw();
            canvas.commitShapes();
        }
    }

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }
}
