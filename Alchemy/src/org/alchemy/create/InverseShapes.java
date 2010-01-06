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
package org.alchemy.create;

import org.alchemy.core.AlcModule;
import org.alchemy.core.AlcShape;

import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * InverseShapes.java
 * @author  Karl D.D. Willis
 */
public class InverseShapes extends AlcModule {

    private boolean moveTo = true;
    private boolean mouseUp = true;

    /**
     * Creates a new instance of InverseShapes
     */
    public InverseShapes() {
    }

    @Override
    protected void setup() {
    }

    @Override
    protected void reselect() {
        mouseUp = true;
        moveTo = true;
    }

    @Override
    protected void deselect() {
        canvas.commitShapes();
    }

    @Override
    protected void cleared() {
        moveTo = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseUp = false;
        canvas.redraw();
        canvas.commitShapes();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseUp) {
            Point p = e.getPoint();
            if (moveTo) {
                canvas.createShapes.add(new AlcShape(p));
                moveTo = false;
            } else {
                // Need to test if it is null incase the shape has been auto-cleared
                if (canvas.hasCreateShapes()) {
                    canvas.getCurrentCreateShape().curveTo(p);
                    canvas.redraw();
                }
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseUp = true;
        moveTo = true;
    }
}
